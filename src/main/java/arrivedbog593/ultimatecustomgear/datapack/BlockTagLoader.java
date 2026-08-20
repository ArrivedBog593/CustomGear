package arrivedbog593.ultimatecustomgear.datapack;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Feeds the TagFileBuilder with block tags derived from required_tool and
 * harvest_level. The builder emits the files — see TagFileBuilder for why no
 * loader writes tag files directly.
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

    /** Vanilla's mineable tags. There is no mineable/sword — see class doc. */
    private static final String[] TOOL_TYPES = {"pickaxe", "axe", "shovel", "hoe"};

    public static void loadAll(TagFileBuilder tags, List<BlockData> blockList) {
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

        // Inject tool type tags (1.21 path: tags/block/ — singular)
        for (String tool : TOOL_TYPES) {
            for (String blockId : byTool.get(tool)) {
                tags.add("block", mc("mineable/" + tool), blockId, true);
            }
        }
        for (String blockId : swordEfficient) tags.add("block", mc("sword_efficient"), blockId, true);
        for (String blockId : needsStone)     tags.add("block", mc("needs_stone_tool"), blockId, true);
        for (String blockId : needsIron)      tags.add("block", mc("needs_iron_tool"), blockId, true);
        for (String blockId : needsDiamond)   tags.add("block", mc("needs_diamond_tool"), blockId, true);
    }

    private static ResourceLocation mc(String path) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", path);
    }

}