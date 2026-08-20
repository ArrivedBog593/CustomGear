package arrivedbog593.ultimatecustomgear.datapack;

import arrivedbog593.ultimatecustomgear.data.ContainerContentData;
import arrivedbog593.ultimatecustomgear.resources.PackSink;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Wires carried containers into Curios: which slot types the player gets, and
 * which items fit in them.
 * <p>
 * DATAPACK, NOT CODE. Curios accepts either an ICurioItem implementation or a
 * pair of datapack files, and the datapack route keeps every reference to its
 * API out of the item — which matters because it is an optional dependency. It
 * also means a pack author declares this in JSON like everything else.
 * <p>
 * TWO FILES, and both are needed:
 * <ul>
 *   <li>{@code data/curios/tags/item/&lt;slot&gt;.json} — what fits in that slot.
 *       Emitted through the shared TagFileBuilder like every other tag.</li>
 *   <li>{@code data/customgear/curios/entities/entities.json} — which slot types
 *       the player has at all. Curios DEFINES the ten presets but assigns none
 *       of them; without this the tags above have nowhere to appear.</li>
 * </ul>
 * <p>
 * Emitted whether Curios is installed or not. Files nobody reads cost nothing,
 * and generating them conditionally would make the content hash differ between
 * a client with the mod and one without — which the handshake would then reject.
 */
public class CuriosTagLoader {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Gson   GSON   = new GsonBuilder().setPrettyPrinting().create();
    /**
     * The slot set emitted last time, to notice when it changes.
     * <p>
     * Curios reads its slot assignment outside the datapack reload this mod
     * triggers, so a change here does not take effect until a plain /reload —
     * and nothing else about the reload hints at that. Warning at the moment the
     * set changes puts the note in front of whoever just edited the pack.
     */
    private static Set<String> lastDeclared = Set.of();

    public static void loadAll(PackSink pack, TagFileBuilder tags,
                               List<ContainerContentData> containers) {
        // Sorted: the content hash the handshake compares must not depend on the
        // order definitions happened to load in.
        Set<String> declared = new TreeSet<>();

        for (ContainerContentData data : containers) {
            if (data.container == null) continue;
            for (String slot : data.container.curiosSlots()) {
                if (slot == null || slot.isBlank()) continue;
                declared.add(slot);
                tags.add("item", ResourceLocation.fromNamespaceAndPath("curios", slot),
                        "customgear:" + data.id, true);
            }
        }

        if (!declared.equals(lastDeclared)) {
            LOGGER.warn("[CustomGear] Curios slot assignment changed. Curios reads this outside "
                    + "the reload this command triggers, so run /reload as well for the slots to "
                    + "appear or disappear.");
            lastDeclared = declared;
        }

        injectEntitySlots(pack, declared);
    }

    /**
     * ONLY THE DECLARED SLOTS. Assigning every preset would put ten empty slots
     * in the inventory of anyone who installed this mod to make a sword, and
     * they would have no way to remove them.
     */
    private static void injectEntitySlots(PackSink pack, Set<String> slots) {
        if (slots.isEmpty()) return;

        JsonObject root = new JsonObject();

        JsonArray entities = new JsonArray();
        entities.add("minecraft:player");
        root.add("entities", entities);

        JsonArray slotArray = new JsonArray();
        slots.forEach(slotArray::add);
        root.add("slots", slotArray);

        // Under OUR namespace, not curios' — the file assigns slots, it does not
        // define them, and Curios scans every namespace for these.
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                "customgear", "curios/entities/entities.json");
        pack.addRaw(loc, GSON.toJson(root).getBytes(StandardCharsets.UTF_8));

        LOGGER.info("[CustomGear] Gave the player {} Curios slot type(s): {}",
                slots.size(), String.join(", ", slots));
    }
}