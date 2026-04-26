package com.github.arrivedbog593.resources;

import com.github.arrivedbog593.data.BlockData;
import com.github.arrivedbog593.data.FluidData;
import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.data.ItemData;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final String[] TOOL_TYPES   = {"pickaxe", "axe", "shovel", "hoe", "sword"};
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
    private static final String HANDHELD_PARENT = "minecraft:item/handheld";
    private static final String GENERATED_PARENT = "minecraft:item/generated";

    // ========== ERROR MESSAGE CONSTANTS ==========
    private static final String ERROR_ARMOR_LAYERS_REQUIRED    = "[CustomGear] 'armor_layers' is required for armor in custom mode: {}";
    private static final String ERROR_LAYER_NOT_FOUND          = "[CustomGear] Layer {} not found: {}";
    private static final String ERROR_LAYER_NOT_DEFINED        = "[CustomGear] 'layer_{}' not defined in armor_layers for: {}";
    private static final String ERROR_REFS_REQUIRED            = "[CustomGear] 'refs' is required for {} in {} mode: {}";
    private static final String ERROR_PIECE_TEXTURE_NOT_FOUND  = "[CustomGear] Piece texture not found: {}";
    private static final String ERROR_TOOL_TEXTURE_NOT_FOUND   = "[CustomGear] Tool texture not found: {}";
    private static final String ERROR_ITEM_TEXTURE_NOT_FOUND   = "[CustomGear] Item texture not found: {}";
    private static final String ERROR_BLOCK_TEXTURE_NOT_FOUND  = "[CustomGear] Block texture not found: {}";
    private static final String ERROR_REFS_MISSING_KEY         = "[CustomGear] 'refs' missing key '{}' for: {}";
    private static final String ERROR_REFS_MISSING_KEY_IN      = "[CustomGear] 'refs' missing key '{}' in: {}";
    private static final String ERROR_REFS_REQUIRED_SIMPLE     = "[CustomGear] 'refs' is required in reference mode: {}";

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
                    String itemId = data.id + "_" + piece;
                    generateItemModelWithRef(pack, itemId, getDefaultArmorTexture(piece));
                }
            }
            case "tool_set" -> {
                if (data.tools == null) return;
                for (String toolType : TOOL_TYPES) {
                    if (!hasTool(data, toolType)) continue;
                    String itemId = data.id + "_" + toolType;
                    generateItemModelWithRef(pack, itemId, getDefaultToolTexture(toolType));
                }
            }
            default -> generateItemModelWithRef(pack, data.id, getDefaultToolTexture(data.type));
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

    private static String getDefaultToolTexture(String type) {
        return switch (type) {
            case "pickaxe" -> DEFAULT_PICKAXE;
            case "axe"     -> DEFAULT_AXE;
            case "shovel"  -> DEFAULT_SHOVEL;
            case "hoe"     -> DEFAULT_HOE;
            default        -> DEFAULT_SWORD;
        };
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
        pack.addRaw(itemModelLoc(data.id + "_bucket"), json.getBytes());
    }

    // ========== CUSTOM LOADING (GEAR) ==========

    private static void loadCustom(DynamicResourcePack pack, GearData data) {
        switch (data.type) {
            case "armor_set" -> loadCustomArmor(pack, data);
            case "tool_set"  -> loadCustomToolSet(pack, data);
            default          -> loadCustomTool(pack, data);
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

    // ========== REFERENCE LOADING (GEAR) ==========

    private static void loadReference(DynamicResourcePack pack, GearData data) {
        if (!hasRefs(data)) {
            LOGGER.error(ERROR_REFS_REQUIRED_SIMPLE, data.id);
            return;
        }

        switch (data.type) {
            case "armor_set" -> loadReferenceArmor(pack, data);
            case "tool_set"  -> loadReferenceToolSet(pack, data);
            default          -> loadReferenceTool(pack, data);
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
                generateItemModelWithRef(pack, data.id + "_" + toolType, ref);
            } else {
                LOGGER.error(ERROR_REFS_MISSING_KEY_IN, toolType, data.id);
            }
        }
    }

    private static void loadReferenceTool(DynamicResourcePack pack, GearData data) {
        String ref = data.texture.refs.get(data.type);
        if (ref != null) {
            generateItemModelWithRef(pack, data.id, ref);
        } else {
            LOGGER.error(ERROR_REFS_MISSING_KEY_IN, data.type, data.id);
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
        pack.addRaw(itemModelLoc(itemId), json.getBytes());
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
        pack.addRaw(itemModelLoc(itemId), json.getBytes());
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
        pack.addRaw(itemModelLoc(itemId), json.getBytes());
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
        pack.addRaw(itemModelLoc(itemId), json.getBytes());
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
        pack.addRaw(blockModelLoc(blockId), json.getBytes());
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
        pack.addRaw(blockStateLoc(blockId), json.getBytes());
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
        pack.addRaw(itemModelLoc(blockId), json.getBytes());
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
        pack.addRaw(blockModelLoc(blockId), blockJson.getBytes());
        generateBlockState(pack, blockId);
        generateBlockItemModel(pack, blockId);
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
                    case "armor_set" -> addArmorLangEntries(entries, data, lang);
                    case "tool_set"  -> addToolSetLangEntries(entries, data, lang);
                    default          -> addToolLangEntry(entries, data, lang);
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
                // Obtener nombre del fluido
                String fluidName = data.names != null && data.names.containsKey("fluid_name")
                        ? getLocalizedFluidName(data.names.get("fluid_name"), lang)
                        : (data.names != null
                           ? data.names.getOrDefault(lang, data.names.getOrDefault("en_us", data.id))
                           : data.id);

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
        if (data.name != null) {
            value = data.name.getOrDefault(lang, data.name.getOrDefault("en_us", data.id));
        } else {
            value = data.id;
        }
        entries.put(key, value);
    }

    private static String buildJsonLang(Map<String, String> entries) {
        StringBuilder json = new StringBuilder("{\n");
        int i = 0;
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            json.append("  \"").append(entry.getKey()).append("\": \"")
                    .append(entry.getValue()).append("\"");
            if (++i < entries.size()) json.append(",");
            json.append("\n");
        }
        json.append("}");
        return json.toString();
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

    private static String getFluidBucketName(String fluidName, String lang) {
        if (lang.equals("es_mx")) {
            return "Cubeta de " + fluidName;
        } else if (lang.equals("es_es")) {
            return "Cubo de " + fluidName;
        }
        // En_us y otros idiomas por defecto
        return fluidName + " Bucket";
    }

    private static String getLocalizedFluidName(Object fluidNameObj, String lang) {
        if (fluidNameObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> names = (Map<String, String>) fluidNameObj;
            return names.getOrDefault(lang, names.getOrDefault("en_us", ""));
        }
        return fluidNameObj != null ? fluidNameObj.toString() : "";
    }

}