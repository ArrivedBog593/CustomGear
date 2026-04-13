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
                loadArmorLayer(pack, data, 1);
                loadArmorLayer(pack, data, 2);
                String[] pieces = {"helmet", "chestplate", "leggings", "boots"};
                for (String piece : pieces) {
                    if (data.pieces == null || !data.pieces.containsKey(piece)) continue;
                    generateItemModel(pack, data.id + "_" + piece);
                }
            }
            case "tool_set" -> {
                if (data.tools == null) return;
                String[] toolTypes = {"pickaxe", "axe", "shovel", "hoe", "sword"};
                for (String toolType : toolTypes) {
                    if (!data.tools.containsKey(toolType)) continue;
                    String itemId = data.id + "_" + toolType;
                    Path texPath = GEAR_FOLDER.resolve(data.texture.path + "_" + toolType + ".png");
                    if (!Files.exists(texPath)) {
                        System.err.println("[CustomGear] Textura no encontrada: " + texPath);
                        continue;
                    }
                    ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                            "customgear", "textures/item/" + itemId + ".png");
                    pack.addTexture(loc, texPath);
                    generateItemModel(pack, itemId);
                }
            }
            default -> {
                Path texPath = GEAR_FOLDER.resolve(data.texture.path);
                if (!Files.exists(texPath)) {
                    System.err.println("[CustomGear] Textura no encontrada: " + texPath);
                    return;
                }
                ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                        "customgear", "textures/item/" + data.id + ".png");
                pack.addTexture(loc, texPath);
                generateItemModel(pack, data.id);
            }
        }
    }

    private static void loadArmorLayer(DynamicResourcePack pack, GearData data, int layer) {
        String fileName = data.texture.path + "_layer_" + layer + ".png";
        Path texPath = GEAR_FOLDER.resolve(fileName);
        if (!Files.exists(texPath)) {
            System.err.println("[CustomGear] Capa de armadura no encontrada: " + texPath);
            return;
        }
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                "customgear",
                "textures/models/armor/" + data.id + "_layer_" + layer + ".png");
        pack.addTexture(loc, texPath);
    }

    private static void loadReference(DynamicResourcePack pack, GearData data) {
        // Requiere al menos ref o refs
        if (data.texture.ref == null && data.texture.refs == null) {
            System.err.println("[CustomGear] 'ref' y 'refs' vacíos en modo reference: " + data.id);
            return;
        }

        switch (data.type) {
            case "armor_set" -> {
                String[] pieces = {"helmet", "chestplate", "leggings", "boots"};
                for (String piece : pieces) {
                    if (data.pieces == null || !data.pieces.containsKey(piece)) continue;
                    String ref = resolveRef(data.texture, piece);
                    if (ref != null) generateItemModelWithRef(pack, data.id + "_" + piece, ref);
                }
            }
            case "tool_set" -> {
                if (data.tools == null) return;
                String[] toolTypes = {"pickaxe", "axe", "shovel", "hoe", "sword"};
                for (String toolType : toolTypes) {
                    if (!data.tools.containsKey(toolType)) continue;
                    String ref = resolveRef(data.texture, toolType);
                    if (ref != null) generateItemModelWithRef(pack, data.id + "_" + toolType, ref);
                    else System.err.println("[CustomGear] Sin ref para: " + data.id + "_" + toolType);
                }
            }
            default -> {
                String ref = data.texture.ref != null ? data.texture.ref : null;
                if (ref != null) generateItemModelWithRef(pack, data.id, ref);
            }
        }
    }

    // Resuelve el ref correcto para un tipo de herramienta/pieza
    private static String resolveRef(GearData.TextureData texture, String toolType) {
        if (texture.refs != null && texture.refs.containsKey(toolType)) {
            return texture.refs.get(toolType);
        }
        if (texture.ref != null) {
            // Si el ref termina en "/" lo concatena directo, si no agrega "_"
            if (texture.ref.endsWith("/")) {
                return texture.ref + toolType;
            }
            return texture.ref + "_" + toolType;
        }
        return null;
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
                            String value = getLocalizedToolName(data, lang, piece,
                                    data.pieceNameFormat, data.pieceNames,
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
                            String value = getLocalizedToolName(data, lang, toolType,
                                    data.toolNameFormat, data.toolNames,
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

    private static String getLocalizedToolName(GearData data, String lang, String toolType,
                                               java.util.Map<String, String> nameFormat,
                                               java.util.Map<String, java.util.Map<String, String>> toolNames,
                                               String defaultToolName) {
        String setName = getLocalizedName(data, lang);

        String toolName = defaultToolName;
        if (toolNames != null) {
            java.util.Map<String, String> namesForLang = toolNames.getOrDefault(lang,
                    toolNames.get("en_us"));
            if (namesForLang != null && namesForLang.containsKey(toolType)) {
                toolName = namesForLang.get(toolType);
            }
        }

        String format = "{name} {tool}";
        if (nameFormat != null) {
            format = nameFormat.getOrDefault(lang,
                    nameFormat.getOrDefault("en_us", "{name} {tool}"));
        }

        return format.replace("{name}", setName).replace("{tool}", toolName)
                .replace("{piece}", toolName); // compatibilidad con armor
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