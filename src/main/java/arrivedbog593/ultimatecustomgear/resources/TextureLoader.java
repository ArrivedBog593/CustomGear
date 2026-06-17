package arrivedbog593.ultimatecustomgear.resources;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import arrivedbog593.ultimatecustomgear.data.FluidData;
import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.data.ItemData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static arrivedbog593.ultimatecustomgear.resources.ModelConstants.*;

/**
 * Public coordinator for all resource generation.
 * <p>
 * Delegates to:
 *   - GearModelGenerator  → armor, tools, weapons, bows, shields
 *   - BlockModelGenerator → blocks and fluid buckets
 *   - LangGenerator       → lang files for all types
 * <p>
 * This class only contains the public API used by CustomGearMod.
 */
public final class TextureLoader {

    private static final Logger LOGGER      = LogManager.getLogger("CustomGear");
    private static final Path   GEAR_FOLDER = Paths.get(".", "customgear");

    private TextureLoader() {}

    /**
     * Loads all textures and models into the dynamic resource pack.
     */
    public static void loadAll(DynamicResourcePack pack, List<GearData> gearList,
                               List<ItemData> itemList, List<BlockData> blockList,
                               List<FluidData> fluidList) {
        // Load vanilla shield entity texture into the item atlas
        GearModelGenerator.loadVanillaShieldTexture(pack);

        // Gear (armor, tools, weapons and individual items)
        for (GearData data : gearList) {
            GearModelGenerator.load(pack, data);
        }

        // Simple items and food items
        for (ItemData data : itemList) {
            loadItem(pack, data);
        }

        // Blocks
        for (BlockData data : blockList) {
            BlockModelGenerator.loadBlock(pack, data);
        }

        // Fluid buckets
        for (FluidData data : fluidList) {
            BlockModelGenerator.generateBucketModel(pack, data);
        }
    }

    /**
     * Generates lang files for all registered types.
     */
    public static void generateLang(DynamicResourcePack pack, List<GearData> gearList,
                                    List<ItemData> itemList, List<BlockData> blockList,
                                    List<FluidData> fluidList) {
        LangGenerator.generateLang(pack, gearList, itemList, blockList, fluidList);
    }

    // ── Item loading ──────────────────────────────────────────────────────────

    private static void loadItem(DynamicResourcePack pack, ItemData data) {
        LOGGER.info("[CustomGear] Loading item texture: {}", data.id);
        if (data.texture == null || data.texture.mode == null) {
            GearModelGenerator.generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
            return;
        }
        switch (data.texture.mode) {
            case "reference" -> {
                String ref = data.texture.refs != null ? data.texture.refs.get("item") : null;
                if (ref != null) {
                    GearModelGenerator.generateItemModelWithRef(pack, data.id, ref);
                } else {
                    LOGGER.error("[CustomGear] 'refs.item' missing in reference mode: {}", data.id);
                    GearModelGenerator.generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
                }
            }
            case "custom" -> {
                String path = data.texture.refs != null ? data.texture.refs.get("item") : null;
                if (path != null) {
                    Path texPath = GEAR_FOLDER.resolve(path);
                    if (Files.exists(texPath)) {
                        pack.addTexture(itemTextureLoc(data.id), texPath);
                        GearModelGenerator.generateGeneratedItemModel(pack, data.id);
                    } else {
                        LOGGER.error("[CustomGear] Item texture not found: {}", texPath);
                        GearModelGenerator.generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
                    }
                } else {
                    LOGGER.error("[CustomGear] 'refs.item' missing in custom mode: {}", data.id);
                    GearModelGenerator.generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
                }
            }
            default -> GearModelGenerator.generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
        }
    }
}