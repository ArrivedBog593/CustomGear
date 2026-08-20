package arrivedbog593.ultimatecustomgear.resources;

import arrivedbog593.ultimatecustomgear.data.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static arrivedbog593.ultimatecustomgear.resources.ModelConstants.*;

/**
 * Generates lang JSON files for all registered items, blocks and fluids.
 * Supports en_us, es_mx and es_es.
 */
public final class LangGenerator {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    private LangGenerator() {}

    // ── Public entry point ────────────────────────────────────────────────────

    public static void generateLang(PackSink pack, List<GearData> gearList,
                                    List<ItemData> itemList, List<BlockData> blockList,
                                    List<FluidData> fluidList, List<ContainerContentData> backpacks) {
        for (String lang : LANGS) {
            Map<String, String> entries = new LinkedHashMap<>();

            // Gear
            for (GearData data : gearList) {
                switch (data.type) {
                    case "armor_set"  -> addArmorLangEntries(entries, data, lang);
                    case "tool_set"   -> addToolSetLangEntries(entries, data, lang);
                    case "weapon_set" -> addWeaponSetLangEntries(entries, data, lang);
                    default           -> addToolLangEntry(entries, data, lang);
                }
            }

            // Simple items (includes food)
            for (ItemData data : itemList) {
                String key   = "item.customgear." + data.id;
                String value = data.names != null
                        ? data.names.getOrDefault(lang, data.names.getOrDefault("en_us", data.id))
                        : data.id;
                entries.put(key, value);
            }

            // item.*, not block.*: a backpack registers only an Item, so the
            // block key would never be looked up and the name shows raw.
            for (ContainerContentData data : backpacks) {
                String key   = "item.customgear." + data.id;
                String value = data.names != null
                        ? data.names.getOrDefault(lang, data.names.getOrDefault("en_us", data.id))
                        : data.id;
                entries.put(key, value);
            }

            // Blocks
            for (BlockData data : blockList) {
                LOGGER.debug("Generating lang for block: {}", data.id);
                String key   = "block.customgear." + data.id;
                String value = data.names != null
                        ? data.names.getOrDefault(lang, data.names.getOrDefault("en_us", data.id))
                        : data.id;
                entries.put(key, value);
            }

            // Fluids
            for (FluidData data : fluidList) {
                String fluidName = data.names != null
                        ? data.names.getOrDefault(lang, data.names.getOrDefault("en_us", data.id))
                        : data.id;

                entries.put("fluid.customgear." + data.id, fluidName);
                entries.put("block.customgear." + data.id, fluidName);

                String bucketKey;
                String bucketValue;
                bucketKey = "item.customgear." + data.id + "_bucket";
                if (data.bucketNames != null && data.bucketNames.containsKey(lang)) {
                    bucketValue = data.bucketNames.get(lang).replace("{fluid_name}", fluidName);
                } else if (data.bucketNames != null && data.bucketNames.containsKey("en_us")) {
                    bucketValue = data.bucketNames.get("en_us").replace("{fluid_name}", fluidName);
                } else {
                    bucketValue = getFluidBucketName(fluidName, lang);
                }
                entries.put(bucketKey, bucketValue);
            }

            pack.addRaw(langLoc(lang), GSON.toJson(entries).getBytes(StandardCharsets.UTF_8));
        }
    }

    // ── Gear lang helpers ─────────────────────────────────────────────────────

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

    private static void addWeaponSetLangEntries(Map<String, String> entries, GearData data, String lang) {
        if (data.weapons == null) return;
        for (String weaponType : WEAPON_TYPES) {
            if (!data.weapons.containsKey(weaponType)) continue;
            String key   = "item.customgear." + data.id + "_" + weaponType;
            String value = getLocalizedToolName(lang, weaponType, data.weaponNames, getDefaultWeaponTypeName(weaponType, lang));
            entries.put(key, value);
        }
    }

    private static void addToolLangEntry(Map<String, String> entries, GearData data, String lang) {
        String key   = "item.customgear." + data.id;
        String value = data.names != null
                ? data.names.getOrDefault(lang, data.names.getOrDefault("en_us", data.id))
                : data.id;
        entries.put(key, value);
    }

    // ── Name resolution helpers ───────────────────────────────────────────────

    private static String getLocalizedToolName(String lang, String toolType,
                                               Map<String, Map<String, String>> toolNames,
                                               String defaultName) {
        if (toolNames != null) {
            Map<String, String> namesForLang = toolNames.getOrDefault(lang, toolNames.get("en_us"));
            if (namesForLang != null && namesForLang.containsKey(toolType)) {
                return namesForLang.get(toolType);
            }
        }
        return defaultName;
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
        if (lang.equals("es_mx")) return "Cubeta de " + fluidName;
        if (lang.equals("es_es")) return "Cubo de "   + fluidName;
        return fluidName + " Bucket";
    }
}