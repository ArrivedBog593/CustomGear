package arrivedbog593.ultimatecustomgear.datapack;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import arrivedbog593.ultimatecustomgear.data.ContainerContentData;
import arrivedbog593.ultimatecustomgear.resources.PackSink;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Generates a self-drop loot table for every custom block and injects it
 * into the DynamicResourcePack as SERVER_DATA.
 * <p>
 * WHY THIS EXISTS: blocks in Minecraft don't drop anything by themselves —
 * every drop comes from a loot table. Vanilla ships one per block
 * (minecraft:loot_table/blocks/stone.json etc.); without one, breaking a
 * block silently drops NOTHING regardless of tool or harvest level. This
 * loader gives every custom block the standard "drops itself" table:
 * <pre>
 * customgear:loot_table/blocks/&lt;id&gt;.json  (1.21 singular directory name)
 * </pre>
 * which matches the default loot table id Minecraft derives for a block
 * ("customgear:blocks/&lt;id&gt;"). The ordinary table includes the standard
 * survives_explosion condition, so explosions destroy a percentage of
 * drops like vanilla.
 * <p>
 * Interaction with harvest gating: requiresCorrectToolForDrops (see
 * CustomBlock) suppresses the loot table when the wrong tool/tier is used —
 * the two systems compose exactly like vanilla ore.
 * <p>
 * Containers with keeps_contents get an EMPTY table instead: their drop is
 * emitted in code by CustomContainerBlock.onRemove, which attaches the
 * inventory component to the item. The two halves must agree — if this loader
 * emits a normal table for one of them, breaking it yields two full containers.
 * <p>
 * Fluid blocks are NOT covered (they use noLootTable, like vanilla fluids).
 * <p>
 * IMPORTANT: like RecipeLoader and BlockTagLoader, this must be called BOTH
 * at startup and in the /customgear reload command — the reload clears the
 * dynamic pack, and anything not regenerated is wiped on the next data pack
 * reload.
 */
public class BlockLootLoader {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Gson   GSON   = new GsonBuilder().setPrettyPrinting().create();

    public static void loadAll(PackSink pack, List<BlockData> blockList) {
        int count = 0;
        for (BlockData data : blockList) {
            // The drop for a keeps_contents container comes from
            // CustomContainerBlock.onRemove, which attaches the inventory
            // component to the item. Its table must therefore be EMPTY, or the
            // break yields two full containers.
            if (data instanceof ContainerContentData c
                    && c.container != null && c.container.keepsContents()) {
                injectEmptyTable(pack, data.id);
            } else {
                injectSelfDropTable(pack, data.id);
            }
            count++;
        }
        LOGGER.info("[CustomGear] Generated {} block loot tables", count);
    }

    /** Standard vanilla-style "block drops itself" loot table. */
    private static void injectSelfDropTable(PackSink pack, String blockId) {
        JsonArray pools = getJsonElements(blockId);

        JsonObject table = new JsonObject();
        table.addProperty("type", "minecraft:block");
        table.add("pools", pools);

        // 1.21 renamed the data directory to singular: loot_table/
        Identifier loc = Identifier.fromNamespaceAndPath(
                "customgear", "loot_table/blocks/" + blockId + ".json");
        pack.addRaw(loc, GSON.toJson(table).getBytes(StandardCharsets.UTF_8));
        LOGGER.debug("[CustomGear] Injected loot table: {}", loc);
    }

    private static @NotNull JsonArray getJsonElements(String blockId) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", "customgear:" + blockId);

        JsonObject condition = new JsonObject();
        condition.addProperty("condition", "minecraft:survives_explosion");

        JsonArray entries = new JsonArray();
        entries.add(entry);
        JsonArray conditions = new JsonArray();
        conditions.add(condition);

        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        pool.add("entries", entries);
        pool.add("conditions", conditions);

        JsonArray pools = new JsonArray();
        pools.add(pool);
        return pools;
    }

    /**
     * EMPTY table for keeps_contents containers.
     * <p>
     * Their drop is emitted by CustomContainerBlock.onRemove, which attaches the
     * inventory component to the item. A normal table here would drop a SECOND
     * copy of the block — and since the loot table runs before onRemove clears
     * the block entity, that second copy carries the same contents. Two full
     * containers from one break: item duplication.
     * <p>
     * The pool list is deliberately empty rather than absent: a block with no
     * loot table at all falls back to vanilla's default lookup and logs a
     * missing-table warning on every break.
     */
    private static void injectEmptyTable(PackSink pack, String blockId) {
        JsonObject table = new JsonObject();
        table.addProperty("type", "minecraft:block");
        table.add("pools", new JsonArray());

        Identifier loc = Identifier.fromNamespaceAndPath(
                "customgear", "loot_table/blocks/" + blockId + ".json");
        pack.addRaw(loc, GSON.toJson(table).getBytes(StandardCharsets.UTF_8));
        LOGGER.debug("[CustomGear] Injected empty loot table (drop handled in code): {}", loc);
    }

}