package arrivedbog593.ultimatecustomgear.loader;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import arrivedbog593.ultimatecustomgear.resources.DynamicResourcePack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates block tag JSON files and injects them into the DynamicResourcePack
 * as SERVER_DATA, so Minecraft loads them through the normal data pack system.
 * <p>
 * This controls:
 *   - Which tool mines the block efficiently (pickaxe, axe, shovel, hoe, sword)
 *   - What harvest level is required (stone, iron, diamond, netherite)
 * <p>
 * JSON fields in BlockData:
 *   "required_tool": "pickaxe" | "axe" | "shovel" | "hoe" | "sword" | "none"
 *   "harvest_level": 0 (wood) | 1 (stone) | 2 (iron) | 3 (diamond) | 4 (netherite)
 * <p>
 * Generated tags (injected as SERVER_DATA — note: 1.21 renamed the tag
 * directory from "tags/blocks/" to SINGULAR "tags/block/"; using the old
 * plural path makes vanilla silently ignore the tags):
 *   minecraft:tags/block/mineable/pickaxe.json   → required_tool = "pickaxe"
 *   minecraft:tags/block/sword_efficient.json    → required_tool = "sword"
 *   minecraft:tags/block/needs_stone_tool.json   → harvest_level >= 1
 *   minecraft:tags/block/needs_iron_tool.json    → harvest_level >= 2
 *   minecraft:tags/block/needs_diamond_tool.json → harvest_level >= 3
 * <p>
 * Notes:
 *   - harvest_level 4 (netherite) uses needs_diamond_tool — there is no
 *     needs_netherite_tool tag in vanilla 1.21.1. The netherite tier
 *     requirement is handled by the item itself, not by block tags.
 *   - "sword" is special: vanilla has no mineable/sword tag. Sword mining
 *     works through per-block rules inside the sword ITEM (cobweb) or the
 *     sword_efficient block tag (leaves). We route "sword" to
 *     sword_efficient: the block is mined FASTER with any sword. Unlike the
 *     mining tools, this does NOT gate drops, and harvest_level is ignored
 *     for it (needs_*_tool tags are only evaluated by mining tools) — a
 *     warning is logged if both are combined.
 *   - These tags only take effect if the BLOCK also declares
 *     requiresCorrectToolForDrops() in its properties — see
 *     CustomBlock.buildProperties.
 * <p>
 * IMPORTANT: like RecipeLoader, this must be called BOTH at startup and in
 * the /customgear reload command — the reload clears the dynamic pack, and
 * anything not regenerated is wiped on the next data pack reload.
 */
public class BlockTagLoader {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Gson   GSON   = new GsonBuilder().setPrettyPrinting().create();

    /** Vanilla's mineable tags. There is no mineable/sword — see class doc. */
    private static final String[] TOOL_TYPES = {"pickaxe", "axe", "shovel", "hoe"};

    public static void loadAll(DynamicResourcePack pack, List<BlockData> blockList) {
        // Group blocks by tool type and harvest level
        Map<String, List<String>> byTool         = new HashMap<>();
        List<String>              swordEfficient = new ArrayList<>();
        List<String>              needsStone     = new ArrayList<>();
        List<String>              needsIron      = new ArrayList<>();
        List<String>              needsDiamond   = new ArrayList<>();

        for (String tool : TOOL_TYPES) {
            byTool.put(tool, new ArrayList<>());
        }

        for (BlockData data : blockList) {
            String blockId = "customgear:" + data.id;

            // Tool type tag
            if (data.requiredTool != null) {
                if (byTool.containsKey(data.requiredTool)) {
                    byTool.get(data.requiredTool).add(blockId);
                } else if (data.requiredTool.equals("sword")) {
                    // Vanilla has no mineable/sword — sword speed works through
                    // the sword_efficient tag (how leaves-style blocks work).
                    // Speeds up mining with any sword; does NOT gate drops.
                    swordEfficient.add(blockId);
                    if (data.harvestLevel > 0) {
                        LOGGER.warn("[CustomGear] Block '{}': harvest_level {} is ignored with "
                                        + "required_tool 'sword' — needs_*_tool tags are only "
                                        + "evaluated by mining tools (pickaxe/axe/shovel/hoe)",
                                data.id, data.harvestLevel);
                    }
                } else if (!data.requiredTool.equals("none")) {
                    LOGGER.warn("[CustomGear] Block '{}': required_tool '{}' is not a valid "
                                    + "tool (pickaxe, axe, shovel, hoe, sword) — ignored",
                            data.id, data.requiredTool);
                }
            }

            // Harvest level tags (cumulative — level 3 also needs level 2 and 1).
            // Not applied for sword blocks (see warning above).
            boolean isSword = "sword".equals(data.requiredTool);
            if (!isSword) {
                if (data.harvestLevel >= 1) needsStone.add(blockId);
                if (data.harvestLevel >= 2) needsIron.add(blockId);
                if (data.harvestLevel >= 3) needsDiamond.add(blockId);
            }
        }

        int count = 0;

        // Inject tool type tags (1.21 path: tags/block/ — singular)
        for (String tool : TOOL_TYPES) {
            List<String> blocks = byTool.get(tool);
            if (!blocks.isEmpty()) {
                injectTag(pack, "tags/block/mineable/" + tool, blocks);
                count++;
            }
        }

        // Inject sword efficiency tag
        if (!swordEfficient.isEmpty()) {
            injectTag(pack, "tags/block/sword_efficient", swordEfficient);
            count++;
        }

        // Inject harvest level tags
        if (!needsStone.isEmpty()) {
            injectTag(pack, "tags/block/needs_stone_tool", needsStone);
            count++;
        }
        if (!needsIron.isEmpty()) {
            injectTag(pack, "tags/block/needs_iron_tool", needsIron);
            count++;
        }
        if (!needsDiamond.isEmpty()) {
            injectTag(pack, "tags/block/needs_diamond_tool", needsDiamond);
            count++;
        }

        LOGGER.info("[CustomGear] Generated {} block tag files", count);
    }

    private static void injectTag(DynamicResourcePack pack,
                                  String path, List<String> blockIds) {
        JsonObject obj = new JsonObject();
        obj.addProperty("replace", false); // always false — never replace vanilla tags

        JsonArray values = new JsonArray();
        for (String id : blockIds) {
            values.add(id);
        }
        obj.add("values", values);

        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                "minecraft", path + ".json");
        pack.addRaw(loc, GSON.toJson(obj).getBytes(StandardCharsets.UTF_8));
        LOGGER.debug("[CustomGear] Injected tag: {}", loc);
    }
}