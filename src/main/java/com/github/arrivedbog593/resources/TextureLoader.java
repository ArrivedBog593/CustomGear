package com.github.arrivedbog593.resources;

import com.github.arrivedbog593.data.GearData;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TextureLoader {

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
                default -> System.err.println("[CustomGear] Modo de textura inválido: "
                        + data.texture.mode);
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
            case "helmet"     -> DEFAULT_HELMET;
            case "chestplate" -> DEFAULT_CHESTPLATE;
            case "leggings"   -> DEFAULT_LEGGINGS;
            case "boots"      -> DEFAULT_BOOTS;
            default           -> DEFAULT_HELMET;
        };
    }

    private static String getDefaultToolTexture(String type) {
        return switch (type) {
            case "sword"   -> DEFAULT_SWORD;
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
                    System.err.println("[CustomGear] 'armor_layers' es obligatorio para armaduras en modo custom: " + data.id);
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
                        System.err.println("[CustomGear] Capa 1 no encontrada: " + texPath);
                    }
                } else {
                    System.err.println("[CustomGear] 'layer_1' no definido en armor_layers para: " + data.id);
                }

                if (layer2 != null) {
                    Path texPath = GEAR_FOLDER.resolve(layer2);
                    if (Files.exists(texPath)) {
                        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                                "customgear",
                                "textures/models/armor/" + data.id + "_layer_2.png");
                        pack.addTexture(loc, texPath);
                    } else {
                        System.err.println("[CustomGear] Capa 2 no encontrada: " + texPath);
                    }
                } else {
                    System.err.println("[CustomGear] 'layer_2' no definido en armor_layers para: " + data.id);
                }

                if (data.texture.refs == null || data.texture.refs.isEmpty()) {
                    System.err.println("[CustomGear] 'refs' es obligatorio para piezas de armadura en modo custom: " + data.id);
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
                            System.err.println("[CustomGear] Textura de pieza no encontrada: " + texPath);
                        }
                    } else {
                        System.err.println("[CustomGear] 'refs' no contiene: " + piece + " para " + data.id);
                    }
                }
            }
            case "tool_set" -> {
                if (data.tools == null) return;

                if (data.texture.refs == null || data.texture.refs.isEmpty()) {
                    System.err.println("[CustomGear] 'refs' es obligatorio para herramientas en modo custom: " + data.id);
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
                            System.err.println("[CustomGear] Textura de herramienta no encontrada: " + texPath);
                        }
                    } else {
                        System.err.println("[CustomGear] 'refs' no contiene: " + toolType + " para " + data.id);
                    }
                }
            }
        }
    }

    private static void loadReference(DynamicResourcePack pack, GearData data) {
        if (data.texture.refs == null || data.texture.refs.isEmpty()) {
            System.err.println("[CustomGear] 'refs' es obligatorio en modo reference: " + data.id);
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
                        System.err.println("[CustomGear] 'refs' no contiene: " + piece + " en " + data.id);
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
                        System.err.println("[CustomGear] 'refs' no contiene: " + toolType + " en " + data.id);
                    }
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
            StringBuilder json = new StringBuilder("{\n");
            int totalEntries = countLangEntries(gearList);
            int[] counter = {0};

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
                            counter[0]++;
                            json.append("  \"").append(key).append("\": \"")
                                    .append(value).append("\"")
                                    .append(counter[0] < totalEntries ? ",\n" : "\n");
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
                            counter[0]++;
                            json.append("  \"").append(key).append("\": \"")
                                    .append(value).append("\"")
                                    .append(counter[0] < totalEntries ? ",\n" : "\n");
                        }
                    }
                    default -> {
                        String key = "item.customgear." + data.id;
                        String value = getLocalizedName(data, lang);
                        counter[0]++;
                        json.append("  \"").append(key).append("\": \"")
                                .append(value).append("\"")
                                .append(counter[0] < totalEntries ? ",\n" : "\n");
                    }
                }
            }

            json.append("}");
            pack.addRaw(ResourceLocation.fromNamespaceAndPath(
                            "customgear", "lang/" + lang + ".json"),
                    json.toString().getBytes());
        }
    }

    private static String getLocalizedToolName(String lang, String toolType,
                                               java.util.Map<String, java.util.Map<String, String>> toolNames,
                                               String defaultToolName) {
        String toolName = defaultToolName;
        if (toolNames != null) {
            java.util.Map<String, String> namesForLang = toolNames.getOrDefault(lang,
                    toolNames.get("en_us"));
            if (namesForLang != null && namesForLang.containsKey(toolType)) {
                toolName = namesForLang.get(toolType);
            }
        }

        return toolName;
    }

    private static int countLangEntries(List<GearData> gearList) {
        int count = 0;
        for (GearData data : gearList) {
            if (data.type.equals("armor_set") && data.pieces != null)
                count += data.pieces.size();
            else if (data.type.equals("tool_set") && data.tools != null)
                count += data.tools.size();
            else
                count++;
        }
        return count;
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

    private static String getLocalizedName(GearData data, String lang) {
        if (data.name == null) return "Unknown";
        return data.name.getOrDefault(lang,
                data.name.getOrDefault("en_us", "Unknown"));
    }

}