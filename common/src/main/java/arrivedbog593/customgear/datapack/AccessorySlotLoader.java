package arrivedbog593.customgear.datapack;

import arrivedbog593.customgear.data.AccessorySlots;
import arrivedbog593.customgear.data.ContainerContentData;
import arrivedbog593.customgear.resources.PackSink;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Wires carried containers into the accessory mods: Curios (NeoForge) and
 * Trinkets (Fabric). For each, which slots the player gets and which items
 * fit in them.
 * <p>
 * DATAPACK, NOT CODE. Both mods accept either an item implementing their API
 * or a pair of datapack files, and the datapack route keeps every reference
 * to an API out of the item — which matters because both are optional. It
 * also means a pack author declares this in JSON like everything else.
 * <p>
 * TWO FILES PER MOD, and both are needed. Each mod DEFINES its preset slots but
 * enables none of them, so without the second file the first has nowhere to
 * appear:
 * <ul>
 *   <li>Curios: {@code data/curios/tags/item/<slot>.json} (what fits) and
 *       {@code data/customgear/curios/entities/entities.json} (the player has it).</li>
 *   <li>Trinkets: {@code data/trinkets/tags/item/<group>/<slot>.json} and
 *       {@code data/trinkets/entities/customgear.json}.</li>
 * </ul>
 * <p>
 * Both mods' files are emitted on both loaders, installed or not. Files nobody
 * reads cost nothing, and generating them conditionally would make the pack
 * differ between installs for no gain.
 */
public final class AccessorySlotLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger("CustomGear");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * The slots emitted last time, to notice when they change.
     * <p>
     * Both mods read their slot assignment outside the datapack reload this mod
     * triggers, so a change here does not take effect until a plain /reload —
     * and nothing else about the reload hints at that. Warning at the moment the
     * set changes puts the note in front of whoever just edited the pack.
     */
    private static Set<String> lastDeclared = Set.of();

    private AccessorySlotLoader() {}

    public static void loadAll(PackSink pack, TagFileBuilder tags,
                               List<ContainerContentData> containers) {
        // Sorted: the pack must not depend on the order definitions happened
        // to load in.
        Set<String> declared = new TreeSet<>();
        Set<String> curiosSlots = new TreeSet<>();
        Set<String> trinketsSlots = new TreeSet<>();

        for (ContainerContentData data : containers) {
            if (data.container == null) continue;
            for (String slot : data.container.accessorySlots()) {
                if (slot == null || slot.isBlank() || AccessorySlots.isMalformed(slot)) continue;
                declared.add(slot);

                String curios = AccessorySlots.toCurios(slot);
                if (curios != null) {
                    curiosSlots.add(curios);
                    tags.add("item", ResourceLocation.fromNamespaceAndPath("curios", curios),
                            "customgear:" + data.id, true);
                }

                String trinkets = AccessorySlots.toTrinkets(slot);
                if (trinkets != null) {
                    trinketsSlots.add(trinkets);
                    tags.add("item", ResourceLocation.fromNamespaceAndPath("trinkets", trinkets),
                            "customgear:" + data.id, true);
                }
            }
        }

        if (!declared.equals(lastDeclared)) {
            if (!lastDeclared.isEmpty() || !declared.isEmpty()) {
                LOGGER.warn("[CustomGear] Accessory slot assignment changed. Curios and Trinkets "
                        + "read this outside the reload this command triggers, so run /reload as "
                        + "well for the slots to appear or disappear.");
            }
            lastDeclared = declared;
        }

        // Under OUR namespace for Curios, which scans every namespace for these;
        // under trinkets' for Trinkets, where its loader looks.
        writeEntitySlots(pack, curiosSlots, "minecraft:player",
                ResourceLocation.fromNamespaceAndPath("customgear", "curios/entities/entities.json"));
        writeEntitySlots(pack, trinketsSlots, "player",
                ResourceLocation.fromNamespaceAndPath("trinkets", "entities/customgear.json"));

        if (!curiosSlots.isEmpty() || !trinketsSlots.isEmpty()) {
            LOGGER.info("[CustomGear] Gave the player accessory slots — Curios: {}; Trinkets: {}",
                    curiosSlots.isEmpty() ? "none" : String.join(", ", curiosSlots),
                    trinketsSlots.isEmpty() ? "none" : String.join(", ", trinketsSlots));
        }
    }

    /** Assigns the slots to the player. It does not define them: each mod ships its own. */
    private static void writeEntitySlots(PackSink pack, Set<String> slots, String player,
                                         ResourceLocation location) {
        if (slots.isEmpty()) return;

        JsonObject root = new JsonObject();
        JsonArray entities = new JsonArray();
        entities.add(player);
        root.add("entities", entities);

        JsonArray slotArray = new JsonArray();
        slots.forEach(slotArray::add);
        root.add("slots", slotArray);

        pack.addRaw(location, GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }
}
