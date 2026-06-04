package com.github.arrivedbog593.resources;

import com.github.arrivedbog593.data.BlockData;
import com.github.arrivedbog593.data.FluidData;
import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.data.ItemData;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TextureLoader {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Path GEAR_FOLDER = Paths.get(".", "customgear");

    // ========== ARRAY CONSTANTS ==========
    private static final String[] ARMOR_PIECES = {"helmet", "chestplate", "leggings", "boots"};
    private static final String[] TOOL_TYPES   = {"pickaxe", "axe", "shovel", "hoe"};
    private static final String[] WEAPON_TYPES = {"sword", "bow", "crossbow", "shield"};
    private static final String[] LANGS        = {"en_us", "es_mx", "es_es"};

    // ========== PATH CONSTANTS ==========
    private static final String NAMESPACE          = "customgear";
    private static final String ARMOR_TEXTURE_PATH = "textures/models/armor/";
    private static final String ITEM_TEXTURE_PATH  = "textures/item/";
    private static final String BLOCK_TEXTURE_PATH = "textures/block/";
    private static final String MODELS_ITEM_PATH   = "models/item/";
    private static final String BLOCK_MODEL_PATH   = "models/block/";
    private static final String LANG_PATH          = "lang/";

    // ========== JSON MODEL CONSTANTS ==========
    private static final String HANDHELD_PARENT  = "minecraft:item/handheld";
    private static final String GENERATED_PARENT = "minecraft:item/generated";

    // ========== ERROR MESSAGE CONSTANTS ==========
    private static final String ERROR_ARMOR_LAYERS_REQUIRED   = "[CustomGear] 'armor_layers' is required for armor in custom mode: {}";
    private static final String ERROR_LAYER_NOT_FOUND         = "[CustomGear] Layer {} not found: {}";
    private static final String ERROR_LAYER_NOT_DEFINED       = "[CustomGear] 'layer_{}' not defined in armor_layers for: {}";
    private static final String ERROR_REFS_REQUIRED           = "[CustomGear] 'refs' is required for {} in {} mode: {}";
    private static final String ERROR_PIECE_TEXTURE_NOT_FOUND = "[CustomGear] Piece texture not found: {}";
    private static final String ERROR_TOOL_TEXTURE_NOT_FOUND  = "[CustomGear] Tool texture not found: {}";
    private static final String ERROR_ITEM_TEXTURE_NOT_FOUND  = "[CustomGear] Item texture not found: {}";
    private static final String ERROR_BLOCK_TEXTURE_NOT_FOUND = "[CustomGear] Block texture not found: {}";
    private static final String ERROR_REFS_MISSING_KEY        = "[CustomGear] 'refs' missing key '{}' for: {}";
    private static final String ERROR_REFS_MISSING_KEY_IN     = "[CustomGear] 'refs' missing key '{}' in: {}";
    private static final String ERROR_REFS_REQUIRED_SIMPLE    = "[CustomGear] 'refs' is required in reference mode: {}";

    // ========== DEFAULT TEXTURES CONSTANTS ==========
    private static final String DEFAULT_HELMET     = "minecraft:item/iron_helmet";
    private static final String DEFAULT_CHESTPLATE = "minecraft:item/iron_chestplate";
    private static final String DEFAULT_LEGGINGS   = "minecraft:item/iron_leggings";
    private static final String DEFAULT_BOOTS      = "minecraft:item/iron_boots";
    private static final String DEFAULT_SWORD      = "minecraft:item/iron_sword";
    private static final String DEFAULT_PICKAXE    = "minecraft:item/iron_pickaxe";
    private static final String DEFAULT_AXE        = "minecraft:item/iron_axe";
    private static final String DEFAULT_SHOVEL     = "minecraft:item/iron_shovel";
    private static final String DEFAULT_HOE        = "minecraft:item/iron_hoe";
    private static final String DEFAULT_ITEM       = "minecraft:item/paper";
    private static final String DEFAULT_BLOCK      = "minecraft:block/stone";
    private static final String DEFAULT_BOW        = "minecraft:item/bow";
    private static final String DEFAULT_CROSSBOW   = "minecraft:item/crossbow";
    private static final String DEFAULT_SHIELD     = "minecraft:item/shield";

    // ========== DEFAULT PULLING MODEL CONSTANTS ==========
    private static final String DEFAULT_BOW_PULLING_0      = "minecraft:item/bow_pulling_0";
    private static final String DEFAULT_BOW_PULLING_1      = "minecraft:item/bow_pulling_1";
    private static final String DEFAULT_BOW_PULLING_2      = "minecraft:item/bow_pulling_2";
    private static final String DEFAULT_CROSSBOW_PULLING_0 = "minecraft:item/crossbow_pulling_0";
    private static final String DEFAULT_CROSSBOW_PULLING_1 = "minecraft:item/crossbow_pulling_1";
    private static final String DEFAULT_CROSSBOW_PULLING_2 = "minecraft:item/crossbow_pulling_2";
    private static final String DEFAULT_CROSSBOW_ARROW     = "minecraft:item/crossbow_arrow";
    private static final String DEFAULT_CROSSBOW_FIREWORK  = "minecraft:item/crossbow_firework";

    // ========== SHIELD TEXTURE CONSTANT ==========
    // The vanilla shield texture lives in the entity atlas (textures/entity/),
    // not in the item atlas. We extract it from the classpath at runtime and
    // register it in the item atlas so flat shield models can reference it.
    private static final ResourceLocation SHIELD_TEXTURE_LOC =
            ResourceLocation.fromNamespaceAndPath(NAMESPACE, "textures/item/shield_base_nopattern.png");


    // ========== PATH HELPER METHODS ==========

    private static ResourceLocation armorTextureLoc(String gearId, String layer) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, ARMOR_TEXTURE_PATH + gearId + "_" + layer + ".png");
    }

    private static ResourceLocation itemTextureLoc(String itemId) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, ITEM_TEXTURE_PATH + itemId + ".png");
    }

    private static ResourceLocation blockTextureLoc(String blockId) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, BLOCK_TEXTURE_PATH + blockId + ".png");
    }

    private static ResourceLocation itemModelLoc(String itemId) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, MODELS_ITEM_PATH + itemId + ".json");
    }

    private static ResourceLocation blockModelLoc(String blockId) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, BLOCK_MODEL_PATH + blockId + ".json");
    }

    private static ResourceLocation blockStateLoc(String blockId) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, "blockstates/" + blockId + ".json");
    }

    private static ResourceLocation langLoc(String lang) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, LANG_PATH + lang + ".json");
    }

    // ========== VALIDATION HELPER METHODS ==========

    private static boolean hasArmorLayers(GearData data) {
        return data.texture != null && data.texture.armorLayers != null && !data.texture.armorLayers.isEmpty();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean hasRefs(GearData data) {
        return data.texture != null && data.texture.refs != null && !data.texture.refs.isEmpty();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean hasPiece(GearData data, String piece) {
        return data.pieces != null && data.pieces.containsKey(piece);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean hasTool(GearData data, String toolType) {
        return data.tools != null && data.tools.containsKey(toolType);
    }

    // ========== MAIN LOADING METHODS ==========

    /**
     * Loads textures for gear, items, blocks and fluids.
     */
    public static void loadAll(DynamicResourcePack pack, List<GearData> gearList,
                               List<ItemData> itemList, List<BlockData> blockList, List<FluidData> fluidList) {
        // Load vanilla shield entity texture into the item atlas so flat shield models can use it
        loadVanillaShieldTexture(pack);

        // Gear
        for (GearData data : gearList) {
            if (data.texture == null || data.texture.mode == null) {
                generateDefaultModels(pack, data);
                continue;
            }
            switch (data.texture.mode) {
                case "custom"    -> loadCustom(pack, data);
                case "reference" -> loadReference(pack, data);
                case "default"   -> generateDefaultModels(pack, data);
                default -> LOGGER.warn("[CustomGear] Invalid texture mode: {}", data.texture.mode);
            }
        }

        // Simple items
        for (ItemData data : itemList) loadItem(pack, data);

        // Blocks
        for (BlockData data : blockList) loadBlock(pack, data);

        // Fluids Buckets
        for (FluidData data : fluidList) generateBucketModel(pack, data);
    }

    // ========== DEFAULT MODEL GENERATION ==========

    private static void generateDefaultModels(DynamicResourcePack pack, GearData data) {
        switch (data.type) {
            case "armor_set" -> {
                for (String piece : ARMOR_PIECES) {
                    if (!hasPiece(data, piece)) continue;
                    generateItemModelWithRef(pack, data.id + "_" + piece, getDefaultArmorTexture(piece));
                }
            }
            case "tool_set" -> {
                if (data.tools == null) return;
                for (String toolType : TOOL_TYPES) {
                    if (!hasTool(data, toolType)) continue;
                    generateParentOnlyModel(pack, data.id + "_" + toolType, getDefaultToolModelPath(toolType));
                }
            }
            case "weapon_set" -> {
                if (data.weapons == null) return;
                for (String weaponType : WEAPON_TYPES) {
                    if (!data.weapons.containsKey(weaponType)) continue;
                    String itemId = data.id + "_" + weaponType;
                    if (weaponType.equals("shield")) {
                        generateShieldFlatModel(pack, itemId);
                    } else {
                        generateParentOnlyModel(pack, itemId, getDefaultWeaponParent(weaponType));
                    }
                }
            }
            case "bow"      -> generateBowModelWithRef(pack, data.id, DEFAULT_BOW, Map.of());
            case "crossbow" -> generateCrossbowModelWithRef(pack, data.id, DEFAULT_CROSSBOW, Map.of());
            case "shield" -> generateShieldFlatModel(pack, data.id);
            case "sword"    -> generateParentOnlyModel(pack, data.id, DEFAULT_SWORD);
            default         -> generateParentOnlyModel(pack, data.id, getDefaultToolModelPath(data.type));
        }
    }

    private static String getDefaultArmorTexture(String piece) {
        return switch (piece) {
            case "chestplate" -> DEFAULT_CHESTPLATE;
            case "leggings"   -> DEFAULT_LEGGINGS;
            case "boots"      -> DEFAULT_BOOTS;
            default           -> DEFAULT_HELMET;
        };
    }

    /**
     * Returns the vanilla model path for tools/weapons.
     * Used as the model parent to correctly inherit handheld orientation.
     */
    private static String getDefaultToolModelPath(String type) {
        return switch (type) {
            case "axe"    -> DEFAULT_AXE;
            case "shovel" -> DEFAULT_SHOVEL;
            case "hoe"    -> DEFAULT_HOE;
            default       -> DEFAULT_PICKAXE;
        };
    }

    private static String getDefaultWeaponParent(String type) {
        return switch (type) {
            case "bow"      -> DEFAULT_BOW;
            case "crossbow" -> DEFAULT_CROSSBOW;
            case "shield"   -> DEFAULT_SHIELD;
            default         -> DEFAULT_SWORD;
        };
    }

    private static void generateParentOnlyModel(DynamicResourcePack pack,
                                                String itemId, String parent) {
        String json = """
        {
          "parent": "%s"
        }
        """.formatted(parent);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    // ========== ITEM LOADING ==========

    /**
     * Loads a simple item texture.
     * - No texture field or blank → default (paper)
     * - Contains ":" → reference mode (resource location from another mod)
     * - Otherwise → custom mode (relative path from customgear folder)
     */
    private static void loadItem(DynamicResourcePack pack, ItemData data) {
        LOGGER.info("[CustomGear] Loading item texture: {}", data.id);
        if (data.texture == null || data.texture.mode == null) {
            generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
            return;
        }
        switch (data.texture.mode) {
            case "reference" -> {
                String ref = data.texture.refs != null
                        ? data.texture.refs.get("item")
                        : null;
                if (ref != null) {
                    generateItemModelWithRef(pack, data.id, ref);
                } else {
                    LOGGER.error("[CustomGear] 'refs.item' missing in reference mode: {}", data.id);
                    generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
                }
            }
            case "custom" -> {
                String path = data.texture.refs != null
                        ? data.texture.refs.get("item")
                        : null;
                if (path != null) {
                    Path texPath = GEAR_FOLDER.resolve(path);
                    if (Files.exists(texPath)) {
                        pack.addTexture(itemTextureLoc(data.id), texPath);
                        generateGeneratedItemModel(pack, data.id);
                    } else {
                        LOGGER.error(ERROR_ITEM_TEXTURE_NOT_FOUND, texPath);
                        generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
                    }
                } else {
                    LOGGER.error("[CustomGear] 'refs.item' missing in custom mode: {}", data.id);
                    generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
                }
            }
            default -> generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
        }
    }

    // ========== BLOCK LOADING ==========

    /**
     * Loads a block texture and generates its model, blockstate and item model.
     * - No texture field or blank → default (stone)
     * - Contains ":" → reference mode (resource location from another mod)
     * - Otherwise → custom mode (relative path from customgear folder)
     */
    private static void loadBlock(DynamicResourcePack pack, BlockData data) {
        LOGGER.info("[CustomGear] Loading block texture: {}", data.id);
        if (data.texture == null || data.texture.mode == null) {
            generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
            return;
        }
        switch (data.texture.mode) {
            case "reference" -> {
                String ref = data.texture.refs != null
                        ? data.texture.refs.get("block")
                        : null;
                if (ref != null) {
                    generateBlockWithRef(pack, data.id, ref);
                } else {
                    LOGGER.error("[CustomGear] 'refs.block' missing in reference mode: {}", data.id);
                    generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
                }
            }
            case "custom" -> {
                String path = data.texture.refs != null
                        ? data.texture.refs.get("all")
                        : null;
                if (path != null) {
                    Path texPath = GEAR_FOLDER.resolve(path);
                    if (Files.exists(texPath)) {
                        pack.addTexture(blockTextureLoc(data.id), texPath);
                        generateBlockModel(pack, data.id);
                        generateBlockState(pack, data.id);
                        generateBlockItemModel(pack, data.id);
                    } else {
                        LOGGER.error(ERROR_BLOCK_TEXTURE_NOT_FOUND, texPath);
                        generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
                    }
                } else {
                    LOGGER.error("[CustomGear] 'refs.all' missing in custom mode: {}", data.id);
                    generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
                }
            }
            default -> generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
        }
    }

    // ========== FLUID BUCKET LOADING ==========

    /**
     * Loads a fluid texture and generates its bucket model.
     */
    private static void generateBucketModel(DynamicResourcePack pack, FluidData data) {
        String bucketTexture;

        if (data.texture == null || data.texture.mode == null || data.texture.mode.equals("default")) {
            bucketTexture = "minecraft:item/water_bucket";
        } else if (data.texture.mode.equals("custom")) {
            bucketTexture = NAMESPACE + ":item/" + data.id + "_bucket";
        } else {
            // reference mode
            String bucketRef = data.texture.refs != null ? data.texture.refs.get("bucket") : null;
            bucketTexture = bucketRef != null ? bucketRef : "minecraft:item/water_bucket";
        }

        String json = """
    {
      "parent": "minecraft:item/generated",
      "textures": {
        "layer0": "%s"
      }
    }
    """.formatted(bucketTexture);
        pack.addRaw(itemModelLoc(data.id + "_bucket"), json.getBytes(StandardCharsets.UTF_8));
    }

    // ========== CUSTOM LOADING (GEAR) ==========

    private static void loadCustom(DynamicResourcePack pack, GearData data) {
        switch (data.type) {
            case "armor_set"  -> loadCustomArmor(pack, data);
            case "tool_set"   -> loadCustomToolSet(pack, data);
            case "weapon_set" -> loadCustomWeaponSet(pack, data);
            default           -> loadCustomTool(pack, data);
        }
    }

    private static void loadCustomArmor(DynamicResourcePack pack, GearData data) {
        if (!hasArmorLayers(data)) {
            LOGGER.error(ERROR_ARMOR_LAYERS_REQUIRED, data.id);
            return;
        }

        loadArmorLayer(pack, data, "layer_1");
        loadArmorLayer(pack, data, "layer_2");

        if (!hasRefs(data)) {
            LOGGER.error(ERROR_REFS_REQUIRED, "armor pieces", "custom", data.id);
            return;
        }

        for (String piece : ARMOR_PIECES) {
            if (!hasPiece(data, piece)) continue;
            loadArmorPiece(pack, data, piece);
        }
    }

    private static void loadArmorLayer(DynamicResourcePack pack, GearData data, String layerKey) {
        String layerNum  = layerKey.split("_")[1];
        String layerPath = data.texture.armorLayers.get(layerKey);

        if (layerPath != null) {
            Path texPath = GEAR_FOLDER.resolve(layerPath);
            if (Files.exists(texPath)) {
                pack.addTexture(armorTextureLoc(data.id, layerKey), texPath);
            } else {
                LOGGER.error(ERROR_LAYER_NOT_FOUND, layerNum, texPath);
            }
        } else {
            LOGGER.error(ERROR_LAYER_NOT_DEFINED, layerNum, data.id);
        }
    }

    private static void loadArmorPiece(DynamicResourcePack pack, GearData data, String piece) {
        String ref = data.texture.refs.get(piece);
        if (ref != null) {
            Path texPath = GEAR_FOLDER.resolve(ref);
            if (Files.exists(texPath)) {
                String itemId = data.id + "_" + piece;
                pack.addTexture(itemTextureLoc(itemId), texPath);
                generateArmorItemModel(pack, itemId);
            } else {
                LOGGER.error(ERROR_PIECE_TEXTURE_NOT_FOUND, texPath);
            }
        } else {
            LOGGER.error(ERROR_REFS_MISSING_KEY, piece, data.id);
        }
    }

    private static void loadCustomToolSet(DynamicResourcePack pack, GearData data) {
        if (data.tools == null) return;

        if (!hasRefs(data)) {
            LOGGER.error(ERROR_REFS_REQUIRED, "tools", "custom", data.id);
            return;
        }

        for (String toolType : TOOL_TYPES) {
            if (!hasTool(data, toolType)) continue;
            loadCustomToolItem(pack, data, toolType);
        }
    }

    private static void loadCustomTool(DynamicResourcePack pack, GearData data) {
        if (!hasRefs(data)) {
            LOGGER.error(ERROR_REFS_REQUIRED, "tool", "custom", data.id);
            return;
        }

        String ref = data.texture.refs.get(data.type);
        if (ref != null) {
            loadToolTexture(pack, data.id, ref);
            generateToolItemModel(pack, data.id);
        }
    }

    private static void loadCustomToolItem(DynamicResourcePack pack, GearData data, String toolType) {
        String ref = data.texture.refs.get(toolType);
        if (ref != null) {
            String itemId = data.id + "_" + toolType;
            loadToolTexture(pack, itemId, ref);
            generateToolItemModel(pack, itemId);
        } else {
            LOGGER.error(ERROR_REFS_MISSING_KEY, toolType, data.id);
        }
    }

    private static void loadToolTexture(DynamicResourcePack pack, String itemId, String ref) {
        Path texPath = GEAR_FOLDER.resolve(ref);
        if (Files.exists(texPath)) {
            pack.addTexture(itemTextureLoc(itemId), texPath);
        } else {
            LOGGER.error(ERROR_TOOL_TEXTURE_NOT_FOUND, texPath);
        }
    }

    private static void loadCustomWeaponSet(DynamicResourcePack pack, GearData data) {
        if (data.weapons == null) return;
        if (!hasRefs(data)) {
            LOGGER.error(ERROR_REFS_REQUIRED, "weapons", "custom", data.id);
            return;
        }
        for (String weaponType : WEAPON_TYPES) {
            if (!data.weapons.containsKey(weaponType)) continue;
            String ref = data.texture.refs.get(weaponType);
            if (ref == null) {
                LOGGER.error(ERROR_REFS_MISSING_KEY, weaponType, data.id);
                continue;
            }
            String itemId = data.id + "_" + weaponType;
            if (weaponType.equals("bow")) {
                loadCustomBowTextures(pack, data, itemId, ref);
            } else {
                loadToolTexture(pack, itemId, ref);
                generateToolItemModel(pack, itemId);
            }
        }
    }

    private static void loadCustomBowTextures(DynamicResourcePack pack, GearData data,
                                              String itemId, String baseRef) {
        Path basePath = GEAR_FOLDER.resolve(baseRef);
        if (!Files.exists(basePath)) {
            LOGGER.error(ERROR_TOOL_TEXTURE_NOT_FOUND, basePath);
            generateParentOnlyModel(pack, itemId, DEFAULT_BOW);
            return;
        }
        pack.addTexture(itemTextureLoc(itemId), basePath);

        String[] frames = {"pulling_0", "pulling_1", "pulling_2"};
        boolean hasAllFrames = true;
        for (String frame : frames) {
            String frameRef = data.texture.refs.get("bow_" + frame);
            if (frameRef != null) {
                Path framePath = GEAR_FOLDER.resolve(frameRef);
                if (Files.exists(framePath)) {
                    pack.addTexture(itemTextureLoc(itemId + "_" + frame), framePath);
                } else {
                    LOGGER.error(ERROR_TOOL_TEXTURE_NOT_FOUND, framePath);
                    hasAllFrames = false;
                }
            } else {
                hasAllFrames = false;
            }
        }
        generateBowItemModel(pack, itemId, hasAllFrames);
    }

    private static void generateBowItemModel(DynamicResourcePack pack,
                                             String itemId, boolean withAnimation) {
        String json;
        if (withAnimation) {
            json = """
            {
              "parent": "%s",
              "textures": { "layer0": "%s:item/%s" },
              "overrides": [
                { "predicate": { "pulling": 1 },              "model": "%s:item/%s_pulling_0" },
                { "predicate": { "pulling": 1, "pull": 0.65 },"model": "%s:item/%s_pulling_1" },
                { "predicate": { "pulling": 1, "pull": 0.9 }, "model": "%s:item/%s_pulling_2" }
              ]
            }
            """.formatted(HANDHELD_PARENT, NAMESPACE, itemId,
                    NAMESPACE, itemId, NAMESPACE, itemId, NAMESPACE, itemId);
        } else {
            json = """
            {
              "parent": "%s",
              "textures": { "layer0": "%s:item/%s" }
            }
            """.formatted(HANDHELD_PARENT, NAMESPACE, itemId);
        }
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    // ========== REFERENCE LOADING (GEAR) ==========

    private static void loadReference(DynamicResourcePack pack, GearData data) {
        if (!hasRefs(data)) {
            LOGGER.error(ERROR_REFS_REQUIRED_SIMPLE, data.id);
            return;
        }
        switch (data.type) {
            case "armor_set"  -> loadReferenceArmor(pack, data);
            case "tool_set"   -> loadReferenceToolSet(pack, data);
            case "weapon_set" -> loadReferenceWeaponSet(pack, data);
            case "bow", "crossbow", "shield" -> loadReferenceWeapon(pack, data);
            default           -> loadReferenceTool(pack, data);
        }
    }

    private static void loadReferenceArmor(DynamicResourcePack pack, GearData data) {
        for (String piece : ARMOR_PIECES) {
            if (!hasPiece(data, piece)) continue;
            String ref = data.texture.refs.get(piece);
            if (ref != null) {
                generateItemModelWithRef(pack, data.id + "_" + piece, ref);
            } else {
                LOGGER.error(ERROR_REFS_MISSING_KEY_IN, piece, data.id);
            }
        }

        if (hasArmorLayers(data)) {
            String layer1 = data.texture.armorLayers.get("layer_1");
            String layer2 = data.texture.armorLayers.get("layer_2");
            if (layer1 != null) generateItemModelWithRef(pack, data.id + "_layer_1", layer1);
            if (layer2 != null) generateItemModelWithRef(pack, data.id + "_layer_2", layer2);
        }
    }

    private static void loadReferenceToolSet(DynamicResourcePack pack, GearData data) {
        for (String toolType : TOOL_TYPES) {
            if (!hasTool(data, toolType)) continue;
            String ref = data.texture.refs.get(toolType);
            if (ref != null) {
                generateParentOnlyModel(pack, data.id + "_" + toolType, ref);
            } else {
                LOGGER.error(ERROR_REFS_MISSING_KEY_IN, toolType, data.id);
            }
        }
    }

    private static void loadReferenceTool(DynamicResourcePack pack, GearData data) {
        String ref = data.texture.refs.get(data.type);
        if (ref != null) {
            generateParentOnlyModel(pack, data.id, ref);
        } else {
            LOGGER.error(ERROR_REFS_MISSING_KEY_IN, data.type, data.id);
        }
    }

    private static void loadReferenceWeaponSet(DynamicResourcePack pack, GearData data) {
        for (String weaponType : WEAPON_TYPES) {
            if (data.weapons == null || !data.weapons.containsKey(weaponType)) continue;
            String ref = data.texture.refs.get(weaponType);
            String itemId = data.id + "_" + weaponType;
            if (ref != null) {
                switch (weaponType) {
                    case "bow"      -> generateBowModelWithRef(pack, itemId, ref, data.texture.refs);
                    case "crossbow" -> generateCrossbowModelWithRef(pack, itemId, ref, data.texture.refs);
                    case "shield"   -> generateShieldFlatModel(pack, itemId);
                    default         -> generateParentOnlyModel(pack, itemId, ref);
                }
            } else {
                LOGGER.error(ERROR_REFS_MISSING_KEY_IN, weaponType, data.id);
                generateParentOnlyModel(pack, itemId, getDefaultWeaponParent(weaponType));
            }
        }
    }

    private static void loadReferenceWeapon(DynamicResourcePack pack, GearData data) {
        String ref = data.texture.refs.get(data.type);
        if (ref != null) {
            switch (data.type) {
                case "bow"      -> generateBowModelWithRef(pack, data.id, ref, data.texture.refs);
                case "crossbow" -> generateCrossbowModelWithRef(pack, data.id, ref, data.texture.refs);
                default         -> generateShieldFlatModel(pack, data.id);
            }
        } else {
            LOGGER.error(ERROR_REFS_MISSING_KEY_IN, data.type, data.id);
            generateParentOnlyModel(pack, data.id, getDefaultWeaponParent(data.type));
        }
    }

    // ========== JSON MODEL GENERATION ==========

    /**
     * Generates a tool item model (handheld) with custom texture.
     * Used for pickaxes, axes, shovels, hoes and swords.
     */
    private static void generateToolItemModel(DynamicResourcePack pack, String itemId) {
        String json = """
            {
              "parent": "%s",
              "textures": {
                "layer0": "%s:item/%s"
              }
            }
            """.formatted(HANDHELD_PARENT, NAMESPACE, itemId);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates an armor item model (generated) with custom texture.
     * Used for helmets, chestplates, leggings and boots.
     */
    private static void generateArmorItemModel(DynamicResourcePack pack, String itemId) {
        String json = """
            {
              "parent": "%s",
              "textures": {
                "layer0": "%s:item/%s"
              }
            }
            """.formatted(GENERATED_PARENT, NAMESPACE, itemId);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a flat-generated item model with custom texture.
     * Used for simple items.
     */
    private static void generateGeneratedItemModel(DynamicResourcePack pack, String itemId) {
        String json = """
            {
              "parent": "%s",
              "textures": {
                "layer0": "%s:item/%s"
              }
            }
            """.formatted(GENERATED_PARENT, NAMESPACE, itemId);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates an item or armor model pointing to an external resource location.
     */
    private static void generateItemModelWithRef(DynamicResourcePack pack,
                                                 String itemId, String ref) {
        String json = """
            {
              "parent": "%s",
              "textures": {
                "layer0": "%s"
              }
            }
            """.formatted(GENERATED_PARENT, ref);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a cube_all block model with a custom texture.
     */
    private static void generateBlockModel(DynamicResourcePack pack, String blockId) {
        String json = """
            {
              "parent": "minecraft:block/cube_all",
              "textures": {
                "all": "%s:block/%s"
              }
            }
            """.formatted(NAMESPACE, blockId);
        pack.addRaw(blockModelLoc(blockId), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a blockstate JSON pointing to the block's model.
     */
    private static void generateBlockState(DynamicResourcePack pack, String blockId) {
        String json = """
            {
              "variants": {
                "": { "model": "%s:block/%s" }
              }
            }
            """.formatted(NAMESPACE, blockId);
        pack.addRaw(blockStateLoc(blockId), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates an item model for a block that inherits from its block model.
     */
    private static void generateBlockItemModel(DynamicResourcePack pack, String blockId) {
        String json = """
            {
              "parent": "%s:block/%s"
            }
            """.formatted(NAMESPACE, blockId);
        pack.addRaw(itemModelLoc(blockId), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates block model, blockstate and item model using a reference texture.
     */
    private static void generateBlockWithRef(DynamicResourcePack pack, String blockId, String ref) {
        String blockJson = """
            {
              "parent": "minecraft:block/cube_all",
              "textures": {
                "all": "%s"
              }
            }
            """.formatted(ref);
        pack.addRaw(blockModelLoc(blockId), blockJson.getBytes(StandardCharsets.UTF_8));
        generateBlockState(pack, blockId);
        generateBlockItemModel(pack, blockId);
    }

    /**
     * Generates a bow item model for reference mode.
     * Pulling models are read from refs if declared; otherwise falls back to vanilla pulling models.
     * This allows referencing a bow from another mod including its pulling animation frames.
     *
     * @param refs the full refs map from the gear JSON (may contain bow_pulling_0/1/2 keys)
     */
    private static void generateBowModelWithRef(DynamicResourcePack pack,
                                                String itemId, String ref,
                                                Map<String, String> refs) {
        String p0 = refs.getOrDefault("bow_pulling_0", DEFAULT_BOW_PULLING_0);
        String p1 = refs.getOrDefault("bow_pulling_1", DEFAULT_BOW_PULLING_1);
        String p2 = refs.getOrDefault("bow_pulling_2", DEFAULT_BOW_PULLING_2);

        String json;
        if (ref.equals(DEFAULT_BOW)
                && p0.equals(DEFAULT_BOW_PULLING_0)
                && p1.equals(DEFAULT_BOW_PULLING_1)
                && p2.equals(DEFAULT_BOW_PULLING_2)) {
            // Pure vanilla reference — no texture override needed, just declare overrides explicitly
            json = """
            {
              "parent": "minecraft:item/bow",
              "overrides": [
                { "predicate": { "pulling": 1 },               "model": "minecraft:item/bow_pulling_0" },
                { "predicate": { "pulling": 1, "pull": 0.65 }, "model": "minecraft:item/bow_pulling_1" },
                { "predicate": { "pulling": 1, "pull": 0.9 },  "model": "minecraft:item/bow_pulling_2" }
              ]
            }
            """;
        } else {
            // Custom or foreign mod ref — apply texture and pulling models from refs
            json = """
            {
              "parent": "minecraft:item/bow",
              "textures": { "layer0": "%s" },
              "overrides": [
                { "predicate": { "pulling": 1 },               "model": "%s" },
                { "predicate": { "pulling": 1, "pull": 0.65 }, "model": "%s" },
                { "predicate": { "pulling": 1, "pull": 0.9 },  "model": "%s" }
              ]
            }
            """.formatted(ref, p0, p1, p2);
        }
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a crossbow item model for reference mode.
     * Pulling and charged models are read from refs if declared; otherwise falls back to vanilla models.
     * Supported optional keys: crossbow_pulling_0/1/2, crossbow_arrow, crossbow_firework.
     *
     * @param refs the full refs map from the gear JSON
     */
    private static void generateCrossbowModelWithRef(DynamicResourcePack pack,
                                                     String itemId, String ref,
                                                     Map<String, String> refs) {
        String p0       = refs.getOrDefault("crossbow_pulling_0", DEFAULT_CROSSBOW_PULLING_0);
        String p1       = refs.getOrDefault("crossbow_pulling_1", DEFAULT_CROSSBOW_PULLING_1);
        String p2       = refs.getOrDefault("crossbow_pulling_2", DEFAULT_CROSSBOW_PULLING_2);
        String arrow    = refs.getOrDefault("crossbow_arrow",     DEFAULT_CROSSBOW_ARROW);
        String firework = refs.getOrDefault("crossbow_firework",  DEFAULT_CROSSBOW_FIREWORK);

        String json;
        if (ref.equals(DEFAULT_CROSSBOW)
                && p0.equals(DEFAULT_CROSSBOW_PULLING_0)
                && p1.equals(DEFAULT_CROSSBOW_PULLING_1)
                && p2.equals(DEFAULT_CROSSBOW_PULLING_2)
                && arrow.equals(DEFAULT_CROSSBOW_ARROW)
                && firework.equals(DEFAULT_CROSSBOW_FIREWORK)) {
            // Pure vanilla reference
            json = """
            {
              "parent": "minecraft:item/crossbow",
              "overrides": [
                { "predicate": { "pulling": 1 },                "model": "minecraft:item/crossbow_pulling_0" },
                { "predicate": { "pulling": 1, "pull": 0.58 },  "model": "minecraft:item/crossbow_pulling_1" },
                { "predicate": { "pulling": 1, "pull": 1.0 },   "model": "minecraft:item/crossbow_pulling_2" },
                { "predicate": { "charged": 1 },                "model": "minecraft:item/crossbow_arrow" },
                { "predicate": { "charged": 1, "firework": 1 }, "model": "minecraft:item/crossbow_firework" }
              ]
            }
            """;
        } else {
            // Custom or foreign mod ref
            json = """
            {
              "parent": "minecraft:item/crossbow",
              "textures": { "layer0": "%s" },
              "overrides": [
                { "predicate": { "pulling": 1 },                "model": "%s" },
                { "predicate": { "pulling": 1, "pull": 0.58 },  "model": "%s" },
                { "predicate": { "pulling": 1, "pull": 1.0 },   "model": "%s" },
                { "predicate": { "charged": 1 },                "model": "%s" },
                { "predicate": { "charged": 1, "firework": 1 }, "model": "%s" }
              ]
            }
            """.formatted(ref, p0, p1, p2, arrow, firework);
        }
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Loads the vanilla shield entity texture into the dynamic pack's item atlas.
     * The texture lives in the Minecraft jar at textures/entity/shield_base_nopattern.png.
     * It is re-registered under customgear:item/shield_base_nopattern so flat shield
     * models can reference it via layer0 without touching the entity atlas.
     */
    private static void loadVanillaShieldTexture(DynamicResourcePack pack) {
        try (InputStream is = TextureLoader.class.getResourceAsStream(
                "/assets/minecraft/textures/entity/shield_base_nopattern.png")) {
            if (is != null) {
                pack.addRaw(SHIELD_TEXTURE_LOC, is.readAllBytes());
            } else {
                LOGGER.error("[CustomGear] Could not find vanilla shield texture in classpath");
            }
        } catch (IOException e) {
            LOGGER.error("[CustomGear] Failed to load vanilla shield texture: {}", e.getMessage());
        }
    }

    /**
     * Generates shield item models using builtin/entity as parent — the same approach
     * as the vanilla shield.json. This requires the BEWLR registered in
     * CustomShieldItem.initializeClient() to render correctly.
     * Display transforms and blocking override are copied verbatim from the vanilla
     * shield.json and shield_blocking.json found in the Minecraft jar.
     */
    private static void generateShieldFlatModel(DynamicResourcePack pack,
                                                String itemId) {
        String blockingModelId = itemId + "_blocking";

        // Main shield model — identical structure to vanilla shield.json
        String json = """
        {
          "parent": "builtin/entity",
          "gui_light": "front",
          "textures": {
            "particle": "block/dark_oak_planks"
          },
          "display": {
            "thirdperson_righthand": { "rotation": [0,90,0],    "translation": [10,6,-4],     "scale": [1,1,1] },
            "thirdperson_lefthand":  { "rotation": [0,90,0],    "translation": [10,6,12],     "scale": [1,1,1] },
            "firstperson_righthand": { "rotation": [0,180,5],   "translation": [-10,2,-10],   "scale": [1.25,1.25,1.25] },
            "firstperson_lefthand":  { "rotation": [0,180,5],   "translation": [10,0,-10],    "scale": [1.25,1.25,1.25] },
            "gui":                   { "rotation": [15,-25,-5],  "translation": [2,3,0],       "scale": [0.65,0.65,0.65] },
            "fixed":                 { "rotation": [0,180,0],   "translation": [-4.5,4.5,-5], "scale": [0.55,0.55,0.55] },
            "ground":                { "rotation": [0,0,0],     "translation": [2,4,2],        "scale": [0.25,0.25,0.25] }
          },
          "overrides": [
            { "predicate": { "blocking": 1 }, "model": "%s:item/%s" }
          ]
        }
        """.formatted(NAMESPACE, blockingModelId);

        // Blocking model — identical structure to vanilla shield_blocking.json
        String blockingJson = """
        {
          "parent": "builtin/entity",
          "gui_light": "front",
          "textures": {
            "particle": "block/dark_oak_planks"
          },
          "display": {
            "thirdperson_righthand": { "rotation": [45,155,0],  "translation": [-3.49,11,-2],  "scale": [1,1,1] },
            "thirdperson_lefthand":  { "rotation": [45,155,0],  "translation": [11.51,7,2.5],  "scale": [1,1,1] },
            "firstperson_righthand": { "rotation": [0,180,-5],  "translation": [-15,5,-11],    "scale": [1.25,1.25,1.25] },
            "firstperson_lefthand":  { "rotation": [0,180,-5],  "translation": [5,5,-11],      "scale": [1.25,1.25,1.25] },
            "gui":                   { "rotation": [15,-25,-5], "translation": [2,3,0],        "scale": [0.65,0.65,0.65] }
          }
        }
        """;

        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
        pack.addRaw(itemModelLoc(blockingModelId), blockingJson.getBytes(StandardCharsets.UTF_8));
    }

    // ========== LANGUAGE GENERATION ==========

    /**
     * Generates lang files for gear, items, blocks and fluids.
     */
    public static void generateLang(DynamicResourcePack pack, List<GearData> gearList,
                                    List<ItemData> itemList, List<BlockData> blockList,
                                    List<FluidData> fluidList) {
        for (String lang : LANGS) {
            Map<String, String> entries = new LinkedHashMap<>();

            // Gear entries
            for (GearData data : gearList) {
                switch (data.type) {
                    case "armor_set"  -> addArmorLangEntries(entries, data, lang);
                    case "tool_set"   -> addToolSetLangEntries(entries, data, lang);
                    case "weapon_set" -> addWeaponSetLangEntries(entries, data, lang);
                    default           -> addToolLangEntry(entries, data, lang);
                }
            }

            // Simple item entries
            for (ItemData data : itemList) {
                String key   = "item.customgear." + data.id;
                String value = data.names != null
                        ? data.names.getOrDefault(lang, data.names.getOrDefault("en_us", data.id))
                        : data.id;
                entries.put(key, value);
            }

            // Block entries
            for (BlockData data : blockList) {
                LOGGER.debug("Generating lang for block: {}", data.id);
                String key   = "block.customgear." + data.id;
                String value = data.names != null
                        ? data.names.getOrDefault(lang, data.names.getOrDefault("en_us", data.id))
                        : data.id;
                entries.put(key, value);
            }

            // Fluids entries
            for (FluidData data : fluidList) {
                String fluidName = data.names != null
                        ? data.names.getOrDefault(lang, data.names.getOrDefault("en_us", data.id))
                        : data.id;

                // Fluid entry
                String fluidKey = "fluid.customgear." + data.id;
                entries.put(fluidKey, fluidName);

                // Bucket entry
                String bucketKey = "item.customgear." + data.id + "_bucket";
                String bucketValue;

                if (data.bucketNames != null && data.bucketNames.containsKey(lang)) {
                    bucketValue = data.bucketNames.get(lang).replace("{fluid_name}", fluidName);
                } else if (data.bucketNames != null && data.bucketNames.containsKey("en_us")) {
                    bucketValue = data.bucketNames.get("en_us").replace("{fluid_name}", fluidName);
                } else {
                    bucketValue = getFluidBucketName(fluidName, lang);
                }

                entries.put(bucketKey, bucketValue);
            }

            pack.addRaw(langLoc(lang), buildJsonLang(entries).getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void addArmorLangEntries(Map<String, String> entries, GearData data, String lang) {
        if (data.pieces == null) return;
        for (String piece : ARMOR_PIECES) {
            if (!data.pieces.containsKey(piece)) continue;
            String key   = "item.customgear." + data.id + "_" + piece;
            String value = getLocalizedToolName(lang, piece, data.pieceNames, getDefaultPieceName(piece, lang));
            entries.put(key, value);
        }
    }

    private static void addToolSetLangEntries(Map<String, String> entries, GearData data, String lang) {
        if (data.tools == null) return;
        for (String toolType : TOOL_TYPES) {
            if (!data.tools.containsKey(toolType)) continue;
            String key   = "item.customgear." + data.id + "_" + toolType;
            String value = getLocalizedToolName(lang, toolType, data.toolNames, getDefaultToolTypeName(toolType, lang));
            entries.put(key, value);
        }
    }

    private static void addToolLangEntry(Map<String, String> entries, GearData data, String lang) {
        String key = "item.customgear." + data.id;
        String value;
        if (data.names != null) {
            value = data.names.getOrDefault(lang, data.names.getOrDefault("en_us", data.id));
        } else {
            value = data.id;
        }
        entries.put(key, value);
    }

    private static void addWeaponSetLangEntries(Map<String, String> entries,
                                                GearData data, String lang) {
        if (data.weapons == null) return;
        for (String weaponType : WEAPON_TYPES) {
            if (!data.weapons.containsKey(weaponType)) continue;
            String key = "item.customgear." + data.id + "_" + weaponType;
            String value = getLocalizedToolName(lang, weaponType,
                    data.weaponNames, getDefaultWeaponTypeName(weaponType, lang));
            entries.put(key, value);
        }
    }

    private static final com.google.gson.Gson LANG_GSON =
            new com.google.gson.GsonBuilder().setPrettyPrinting().create();

    private static String buildJsonLang(Map<String, String> entries) {
        return LANG_GSON.toJson(entries);
    }

    // ========== NAME HELPER METHODS ==========

    private static String getLocalizedToolName(String lang, String toolType,
                                               Map<String, Map<String, String>> toolNames,
                                               String defaultToolName) {
        if (toolNames != null) {
            Map<String, String> namesForLang = toolNames.getOrDefault(lang,
                    toolNames.get("en_us"));
            if (namesForLang != null && namesForLang.containsKey(toolType)) {
                return namesForLang.get(toolType);
            }
        }
        return defaultToolName;
    }

    private static String getDefaultPieceName(String piece, String lang) {
        if (lang.startsWith("es")) {
            return switch (piece) {
                case "helmet"     -> "Casco";
                case "chestplate" -> "Pechera";
                case "leggings"   -> "Pantalones";
                case "boots"      -> "Botas";
                default           -> piece;
            };
        }
        return switch (piece) {
            case "helmet"     -> "Helmet";
            case "chestplate" -> "Chestplate";
            case "leggings"   -> "Leggings";
            case "boots"      -> "Boots";
            default           -> piece;
        };
    }

    private static String getDefaultToolTypeName(String type, String lang) {
        if (lang.startsWith("es")) {
            return switch (type) {
                case "pickaxe" -> "Pico";
                case "axe"     -> "Hacha";
                case "shovel"  -> "Pala";
                case "hoe"     -> "Azada";
                case "sword"   -> "Espada";
                default        -> type;
            };
        }
        return switch (type) {
            case "pickaxe" -> "Pickaxe";
            case "axe"     -> "Axe";
            case "shovel"  -> "Shovel";
            case "hoe"     -> "Hoe";
            case "sword"   -> "Sword";
            default        -> type;
        };
    }

    private static String getDefaultWeaponTypeName(String type, String lang) {
        if (lang.startsWith("es")) {
            return switch (type) {
                case "bow"      -> "Arco";
                case "crossbow" -> "Ballesta";
                case "shield"   -> "Escudo";
                default         -> "Espada";
            };
        }
        return switch (type) {
            case "bow"      -> "Bow";
            case "crossbow" -> "Crossbow";
            case "shield"   -> "Shield";
            default         -> "Sword";
        };
    }

    private static String getFluidBucketName(String fluidName, String lang) {
        if (lang.equals("es_mx")) {
            return "Cubeta de " + fluidName;
        } else if (lang.equals("es_es")) {
            return "Cubo de " + fluidName;
        }
        return fluidName + " Bucket";
    }

}