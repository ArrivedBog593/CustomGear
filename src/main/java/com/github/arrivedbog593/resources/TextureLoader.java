package com.github.arrivedbog593.resources;

import com.github.arrivedbog593.data.GearData;
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

    private static final String DEFAULT_HELMET     = "minecraft:item/iron_helmet";
    private static final String DEFAULT_CHESTPLATE = "minecraft:item/iron_chestplate";
    private static final String DEFAULT_LEGGINGS   = "minecraft:item/iron_leggings";
    private static final String DEFAULT_BOOTS      = "minecraft:item/iron_boots";
    private static final String DEFAULT_SWORD      = "minecraft:item/iron_sword";
    private static final String DEFAULT_PICKAXE    = "minecraft:item/iron_pickaxe";
    private static final String DEFAULT_AXE        = "minecraft:item/iron_axe";
    private static final String DEFAULT_SHOVEL     = "minecraft:item/iron_shovel";
    private static final String DEFAULT_HOE        = "minecraft:item/iron_hoe";

    public static void loadAll(DynamicResourcePack pack, List<GearData> gearList) {
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
    }

    private static void generateDefaultModels(DynamicResourcePack pack, GearData data) {
        switch (data.type) {
            case "armor_set" -> {
                String[] pieces = {"helmet", "chestplate", "leggings", "boots"};
                for (String piece : pieces) {
                    if (data.pieces == null || !data.pieces.containsKey(piece)) continue;
                    String itemId = data.id + "_" + piece;
                    generateItemModelWithRef(pack, itemId, getDefaultArmorTexture(piece));
                }
            }
            case "tool_set" -> {
                if (data.tools == null) return;
                String[] toolTypes = {"pickaxe", "axe", "shovel", "hoe", "sword"};
                for (String toolType : toolTypes) {
                    if (!data.tools.containsKey(toolType)) continue;
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

    private static void loadCustom(DynamicResourcePack pack, GearData data) {
        switch (data.type) {
            case "armor_set" -> {
                if (data.texture.armorLayers == null || data.texture.armorLayers.isEmpty()) {
                    LOGGER.error("[CustomGear] 'armor_layers' is required for armor in custom mode: {}", data.id);
                    return;
                }

                String layer1 = data.texture.armorLayers.get("layer_1");
                String layer2 = data.texture.armorLayers.get("layer_2");

                if (layer1 != null) {
                    Path texPath = GEAR_FOLDER.resolve(layer1);
                    if (Files.exists(texPath)) {
                        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                                "customgear",
                                "textures/models/armor/" + data.id + "_layer_1.png");
                        pack.addTexture(loc, texPath);
                    } else {
                        LOGGER.error("[CustomGear] Layer 1 not found: {}", texPath);
                    }
                } else {
                    LOGGER.error("[CustomGear] 'layer_1' not defined in armor_layers for: {}", data.id);
                }

                if (layer2 != null) {
                    Path texPath = GEAR_FOLDER.resolve(layer2);
                    if (Files.exists(texPath)) {
                        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                                "customgear",
                                "textures/models/armor/" + data.id + "_layer_2.png");
                        pack.addTexture(loc, texPath);
                    } else {
                        LOGGER.error("[CustomGear] Layer 2 not found: {}", texPath);
                    }
                } else {
                    LOGGER.error("[CustomGear] 'layer_2' not defined in armor_layers for: {}", data.id);
                }

                if (data.texture.refs == null || data.texture.refs.isEmpty()) {
                    LOGGER.error("[CustomGear] 'refs' is required for armor pieces in custom mode: {}", data.id);
                    return;
                }

                String[] pieces = {"helmet", "chestplate", "leggings", "boots"};
                for (String piece : pieces) {
                    if (data.pieces == null || !data.pieces.containsKey(piece)) continue;
                    String ref = data.texture.refs.get(piece);
                    if (ref != null) {
                        Path texPath = GEAR_FOLDER.resolve(ref);
                        if (Files.exists(texPath)) {
                            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                                    "customgear", "textures/item/" + data.id + "_" + piece + ".png");
                            pack.addTexture(loc, texPath);
                            generateItemModel(pack, data.id + "_" + piece);
                        } else {
                            LOGGER.error("[CustomGear] Piece texture not found: {}", texPath);
                        }
                    } else {
                        LOGGER.error("[CustomGear] 'refs' missing key '{}' for: {}", piece, data.id);
                    }
                }
            }
            case "tool_set" -> {
                if (data.tools == null) return;

                if (data.texture.refs == null || data.texture.refs.isEmpty()) {
                    LOGGER.error("[CustomGear] 'refs' is required for tools in custom mode: {}", data.id);
                    return;
                }

                String[] toolTypes = {"pickaxe", "axe", "shovel", "hoe", "sword"};
                for (String toolType : toolTypes) {
                    if (!data.tools.containsKey(toolType)) continue;
                    String ref = data.texture.refs.get(toolType);
                    if (ref != null) {
                        Path texPath = GEAR_FOLDER.resolve(ref);
                        if (Files.exists(texPath)) {
                            String itemId = data.id + "_" + toolType;
                            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                                    "customgear", "textures/item/" + itemId + ".png");
                            pack.addTexture(loc, texPath);
                            generateItemModel(pack, itemId);
                        } else {
                            LOGGER.error("[CustomGear] Tool texture not found: {}", texPath);
                        }
                    } else {
                        LOGGER.error("[CustomGear] 'refs' missing key '{}' for: {}", toolType, data.id);
                    }
                }
            }
            default -> {
                // Individual tools: sword, pickaxe, axe, shovel, hoe
                if (data.texture.refs == null || data.texture.refs.isEmpty()) {
                    LOGGER.error("[CustomGear] 'refs' is required for tools in custom mode: {}", data.id);
                    return;
                }

                String ref = data.texture.refs.get(data.type);
                if (ref != null) {
                    Path texPath = GEAR_FOLDER.resolve(ref);
                    if (Files.exists(texPath)) {
                        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                                "customgear", "textures/item/" + data.id + ".png");
                        pack.addTexture(loc, texPath);
                        generateItemModel(pack, data.id);
                    } else {
                        LOGGER.error("[CustomGear] Tool texture not found: {}", texPath);
                    }
                }
            }
        }
    }

    private static void loadReference(DynamicResourcePack pack, GearData data) {
        if (data.texture.refs == null || data.texture.refs.isEmpty()) {
            LOGGER.error("[CustomGear] 'refs' is required in reference mode: {}", data.id);
            return;
        }

        switch (data.type) {
            case "armor_set" -> {
                String[] pieces = {"helmet", "chestplate", "leggings", "boots"};
                for (String piece : pieces) {
                    if (data.pieces == null || !data.pieces.containsKey(piece)) continue;
                    String ref = data.texture.refs.get(piece);
                    if (ref != null) {
                        generateItemModelWithRef(pack, data.id + "_" + piece, ref);
                    } else {
                        LOGGER.error("[CustomGear] 'refs' missing key '{}' in: {}", piece, data.id);
                    }
                }

                if (data.texture.armorLayers != null && !data.texture.armorLayers.isEmpty()) {
                    String layer1 = data.texture.armorLayers.get("layer_1");
                    String layer2 = data.texture.armorLayers.get("layer_2");
                    if (layer1 != null) {
                        generateItemModelWithRef(pack, data.id + "_layer_1", layer1);
                    }
                    if (layer2 != null) {
                        generateItemModelWithRef(pack, data.id + "_layer_2", layer2);
                    }
                }
            }
            case "tool_set" -> {
                String[] toolTypes = {"pickaxe", "axe", "shovel", "hoe", "sword"};
                for (String toolType : toolTypes) {
                    if (data.tools == null || !data.tools.containsKey(toolType)) continue;
                    String ref = data.texture.refs.get(toolType);
                    if (ref != null) {
                        generateItemModelWithRef(pack, data.id + "_" + toolType, ref);
                    } else {
                        LOGGER.error("[CustomGear] 'refs' missing key '{}' in: {}", toolType, data.id);
                    }
                }
            }
            default -> {
                // Individual tools: sword, pickaxe, axe, shovel, hoe
                String ref = data.texture.refs.get(data.type);
                if (ref != null) {
                    generateItemModelWithRef(pack, data.id, ref);
                } else {
                    LOGGER.error("[CustomGear] 'refs' missing key '{}' in: {}", data.type, data.id);
                }
            }
        }
    }

    private static void generateItemModel(DynamicResourcePack pack, String itemId) {
        String json = """
            {
              "parent": "minecraft:item/handheld",
              "textures": {
                "layer0": "customgear:item/%s"
              }
            }
            """.formatted(itemId);
        pack.addRaw(ResourceLocation.fromNamespaceAndPath(
                        "customgear", "models/item/" + itemId + ".json"),
                json.getBytes());
    }

    private static void generateItemModelWithRef(DynamicResourcePack pack,
                                                 String itemId, String ref) {
        String json = """
            {
              "parent": "minecraft:item/generated",
              "textures": {
                "layer0": "%s"
              }
            }
            """.formatted(ref);
        pack.addRaw(ResourceLocation.fromNamespaceAndPath(
                        "customgear", "models/item/" + itemId + ".json"),
                json.getBytes());
    }

    public static void generateLang(DynamicResourcePack pack, List<GearData> gearList) {
        String[] langs = {"en_us", "es_mx", "es_es"};

        for (String lang : langs) {
            Map<String, String> entries = new LinkedHashMap<>();

            for (GearData data : gearList) {
                switch (data.type) {
                    case "armor_set" -> {
                        if (data.pieces == null) break;
                        String[] pieces = {"helmet", "chestplate", "leggings", "boots"};
                        for (String piece : pieces) {
                            if (!data.pieces.containsKey(piece)) continue;
                            String key = "item.customgear." + data.id + "_" + piece;
                            String value = getLocalizedToolName(lang, piece,
                                    data.pieceNames,
                                    getDefaultPieceName(piece, lang));
                            entries.put(key, value);
                        }
                    }
                    case "tool_set" -> {
                        if (data.tools == null) break;
                        String[] toolTypes = {"pickaxe", "axe", "shovel", "hoe", "sword"};
                        for (String toolType : toolTypes) {
                            if (!data.tools.containsKey(toolType)) continue;
                            String key = "item.customgear." + data.id + "_" + toolType;
                            String value = getLocalizedToolName(lang, toolType,
                                    data.toolNames,
                                    getDefaultToolTypeName(toolType, lang));
                            entries.put(key, value);
                        }
                    }
                    default -> {
                        // Individual tools: sword, pickaxe, axe, shovel, hoe
                        String key = "item.customgear." + data.id;
                        String value;
                        if (data.name != null) {
                            value = data.name.getOrDefault(lang,
                                    data.name.getOrDefault("en_us", data.id));
                        } else {
                            value = data.id;
                        }
                        entries.put(key, value);
                    }
                }
            }

            StringBuilder json = new StringBuilder("{\n");
            int i = 0;
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                json.append("  \"").append(entry.getKey()).append("\": \"")
                        .append(entry.getValue()).append("\"");
                if (++i < entries.size()) json.append(",");
                json.append("\n");
            }
            json.append("}");

            pack.addRaw(ResourceLocation.fromNamespaceAndPath(
                            "customgear", "lang/" + lang + ".json"),
                    json.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    private static String getLocalizedToolName(String lang, String toolType,
                                               java.util.Map<String, java.util.Map<String, String>> toolNames,
                                               String defaultToolName) {
        if (toolNames != null) {
            java.util.Map<String, String> namesForLang = toolNames.getOrDefault(lang,
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
                case "hoe"     -> "Azadón";
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

}