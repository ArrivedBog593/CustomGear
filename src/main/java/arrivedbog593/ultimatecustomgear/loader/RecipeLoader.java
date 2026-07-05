package arrivedbog593.ultimatecustomgear.loader;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.data.ItemData;
import arrivedbog593.ultimatecustomgear.data.RecipeData;
import arrivedbog593.ultimatecustomgear.resources.DynamicResourcePack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generates vanilla recipe JSONs from RecipeData and injects them into the
 * dynamic pack as SERVER_DATA, so they load through the normal data pack
 * pipeline and are visible to JEI. No reflection involved.
 * <p>
 * VALIDATION PHILOSOPHY: everything vanilla would reject at data-pack load
 * time is caught HERE, with a message that names the item and the exact
 * problem. Vanilla's own errors are cryptic and don't point back to the
 * user's JSON — for a no-coding-required mod, clear validation IS the product.
 * Item existence is still NOT validated here (custom items aren't registered
 * yet at mod init); only structure and ID syntax are.
 * <p>
 * INGREDIENTS: every ingredient position accepts either a plain item ID
 * ("minecraft:diamond") or a TAG prefixed with '#' ("#minecraft:planks" =
 * any plank type). Tags work in shaped keys, shapeless ingredients, cooking
 * inputs, and smithing template/base/addition.
 * <p>
 * Supported recipe types:
 *   - shaped → crafting table with a specific pattern
 *   - shapeless → crafting table, ingredients in any order
 *   - smelting → furnace
 *   - blasting → blast furnace
 *   - smithing_transform → smithing table
 */
public class RecipeLoader {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Gson   GSON   = new GsonBuilder().setPrettyPrinting().create();

    // ── Public API ────────────────────────────────────────────────────────────

    public static void loadAll(DynamicResourcePack pack, List<GearData> gearList,
                               List<ItemData> itemList, List<BlockData> blockList) {
        int count = 0;

        for (GearData data : gearList) {
            if (data.recipes != null) {
                for (Map.Entry<String, List<RecipeData>> entry : data.recipes.entrySet()) {
                    String itemId = data.id + "_" + entry.getKey();
                    count += writeRecipeList(pack, itemId, entry.getValue());
                }
            }
            if (data.recipe != null) {
                count += writeRecipeList(pack, data.id, data.recipe);
            }
        }

        for (ItemData data : itemList) {
            if (data.recipe != null) {
                count += writeRecipeList(pack, data.id, data.recipe);
            }
        }

        for (BlockData data : blockList) {
            if (data.recipe != null) {
                count += writeRecipeList(pack, data.id, data.recipe);
            }
        }

        LOGGER.info("[CustomGear] Generated {} recipe JSON files", count);
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private static int writeRecipeList(DynamicResourcePack pack, String itemId,
                                       List<RecipeData> list) {
        if (list == null) return 0;
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            RecipeData rd     = list.get(i);
            String     suffix = list.size() == 1 ? "recipe" : "recipe_" + (i + 1);
            String     recipeId = itemId + "_" + suffix;
            JsonObject json   = buildRecipeJson(itemId, rd);
            if (json != null) {
                ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                        "customgear", "recipe/" + recipeId + ".json");
                pack.addRaw(loc, GSON.toJson(json).getBytes(StandardCharsets.UTF_8));
                LOGGER.debug("[CustomGear] Generated recipe: {}", recipeId);
                count++;
            }
        }
        return count;
    }

    private static JsonObject buildRecipeJson(String itemId, RecipeData data) {
        if (data.type == null) {
            LOGGER.warn("[CustomGear] Recipe for '{}' missing 'type' — skipping", itemId);
            return null;
        }
        return switch (data.type) {
            case "shaped"             -> buildShaped(itemId, data);
            case "shapeless"          -> buildShapeless(itemId, data);
            case "smelting"           -> buildCooking("minecraft:smelting", itemId, data, 200);
            case "blasting"           -> buildCooking("minecraft:blasting", itemId, data, 100);
            case "smithing_transform" -> buildSmithing(itemId, data);
            default -> {
                LOGGER.warn("[CustomGear] Recipe for '{}' has unknown type '{}' — skipping",
                        itemId, data.type);
                yield null;
            }
        };
    }

    // ── Shaped ────────────────────────────────────────────────────────────────

    private static JsonObject buildShaped(String itemId, RecipeData data) {
        if (data.pattern == null || data.pattern.isEmpty()) {
            LOGGER.warn("[CustomGear] Shaped recipe for '{}' missing 'pattern' — skipping", itemId);
            return null;
        }
        if (data.key == null || data.key.isEmpty()) {
            LOGGER.warn("[CustomGear] Shaped recipe for '{}' missing 'key' — skipping", itemId);
            return null;
        }
        if (data.pattern.size() > 3) {
            LOGGER.warn("[CustomGear] Shaped recipe for '{}' has more than 3 rows — skipping", itemId);
            return null;
        }

        // Row lengths: each 1-3 chars, and ALL rows the same length.
        // Vanilla rejects uneven or oversized patterns at data-pack load
        // with an error that doesn't point back to the user's JSON.
        int width = data.pattern.getFirst().length();
        for (String row : data.pattern) {
            if (row.isEmpty() || row.length() > 3) {
                LOGGER.warn("[CustomGear] Shaped recipe for '{}': row \"{}\" must be 1-3 characters — skipping",
                        itemId, row);
                return null;
            }
            if (row.length() != width) {
                LOGGER.warn("[CustomGear] Shaped recipe for '{}': all pattern rows must have the SAME length "
                                + "(row \"{}\" has {} chars, expected {}). Pad with spaces for empty slots — skipping",
                        itemId, row, row.length(), width);
                return null;
            }
        }

        // Collect the symbols actually used in the pattern (space = empty slot)
        Set<Character> usedSymbols = new HashSet<>();
        for (String row : data.pattern) {
            for (char c : row.toCharArray()) {
                if (c != ' ') usedSymbols.add(c);
            }
        }

        // Key entries: 1 char, not a space, and every key must be USED in the
        // pattern — vanilla errors on unused key symbols too.
        for (Map.Entry<String, String> entry : data.key.entrySet()) {
            String symbol = entry.getKey();
            if (symbol.length() != 1) {
                LOGGER.warn("[CustomGear] Shaped recipe for '{}': key '{}' must be exactly 1 char — skipping",
                        itemId, symbol);
                return null;
            }
            if (symbol.charAt(0) == ' ') {
                LOGGER.warn("[CustomGear] Shaped recipe for '{}': the space character is reserved for "
                        + "empty slots and can't be a key — skipping", itemId);
                return null;
            }
            if (!usedSymbols.contains(symbol.charAt(0))) {
                LOGGER.warn("[CustomGear] Shaped recipe for '{}': key '{}' is defined but never appears "
                        + "in the pattern — skipping. Remove it or use it in the pattern", itemId, symbol);
                return null;
            }
        }

        // Every pattern symbol must have a key entry
        for (char symbol : usedSymbols) {
            if (!data.key.containsKey(String.valueOf(symbol))) {
                LOGGER.warn("[CustomGear] Shaped recipe for '{}': pattern uses '{}' but 'key' doesn't "
                        + "define it — skipping", itemId, symbol);
                return null;
            }
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:crafting_shaped");

        JsonArray pattern = new JsonArray();
        for (String row : data.pattern) pattern.add(row);
        obj.add("pattern", pattern);

        JsonObject key = new JsonObject();
        for (Map.Entry<String, String> entry : data.key.entrySet()) {
            JsonObject ing = ingredient(entry.getValue(), itemId, "key '" + entry.getKey() + "'");
            if (ing == null) return null;
            key.add(entry.getKey(), ing);
        }
        obj.add("key", key);

        obj.add("result", buildResult(itemId, data.resultCount));
        return obj;
    }

    // ── Shapeless ─────────────────────────────────────────────────────────────

    private static JsonObject buildShapeless(String itemId, RecipeData data) {
        if (data.ingredients == null || data.ingredients.isEmpty()) {
            LOGGER.warn("[CustomGear] Shapeless recipe for '{}' missing 'ingredients' — skipping", itemId);
            return null;
        }
        if (data.ingredients.size() > 9) {
            LOGGER.warn("[CustomGear] Shapeless recipe for '{}' has {} ingredients — a crafting grid "
                    + "only holds 9. Skipping", itemId, data.ingredients.size());
            return null;
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:crafting_shapeless");

        JsonArray ingredients = new JsonArray();
        for (String id : data.ingredients) {
            JsonObject ing = ingredient(id, itemId, "ingredients");
            if (ing == null) return null;
            ingredients.add(ing);
        }
        obj.add("ingredients", ingredients);

        obj.add("result", buildResult(itemId, data.resultCount));
        return obj;
    }

    // ── Cooking (smelting / blasting) ─────────────────────────────────────────

    private static JsonObject buildCooking(String type, String itemId,
                                           RecipeData data, int defaultTime) {
        if (data.ingredient == null || data.ingredient.isBlank()) {
            LOGGER.warn("[CustomGear] {} recipe for '{}' missing 'ingredient' — skipping", type, itemId);
            return null;
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("type", type);

        JsonObject ing = ingredient(data.ingredient, itemId, "ingredient");
        if (ing == null) return null;
        obj.add("ingredient", ing);

        obj.add("result", buildResult(itemId, 1));
        obj.addProperty("experience", data.experience);
        obj.addProperty("cookingtime", data.cookingTime > 0 ? data.cookingTime : defaultTime);

        return obj;
    }

    // ── Smithing Transform ────────────────────────────────────────────────────

    private static JsonObject buildSmithing(String itemId, RecipeData data) {
        if (data.template == null || data.base == null || data.addition == null) {
            LOGGER.warn("[CustomGear] Smithing recipe for '{}' requires template, base and addition — skipping",
                    itemId);
            return null;
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:smithing_transform");

        JsonObject template = ingredient(data.template, itemId, "template");
        JsonObject base     = ingredient(data.base,     itemId, "base");
        JsonObject addition = ingredient(data.addition, itemId, "addition");
        if (template == null || base == null || addition == null) return null;

        obj.add("template", template);
        obj.add("base", base);
        obj.add("addition", addition);

        obj.add("result", buildResult(itemId, 1));
        return obj;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Builds a vanilla ingredient object from a config value.
     * <ul>
     *   <li>"minecraft:diamond"  → {"item": "minecraft:diamond"}</li>
     *   <li>"#minecraft:planks"  → {"tag": "minecraft:planks"} (any plank)</li>
     * </ul>
     * Returns null (with a clear log naming the item and the position) if the
     * resource location is syntactically invalid — vanilla would reject the
     * whole recipe file at load time with a much less helpful error.
     */
    private static JsonObject ingredient(String value, String itemId, String where) {
        if (value == null || value.isBlank()) {
            LOGGER.warn("[CustomGear] Recipe for '{}': empty ingredient in {} — skipping recipe",
                    itemId, where);
            return null;
        }

        boolean isTag = value.startsWith("#");
        String rl = isTag ? value.substring(1) : value;

        if (ResourceLocation.tryParse(rl) == null) {
            LOGGER.warn("[CustomGear] Recipe for '{}': malformed {} '{}' in {} — skipping recipe. "
                            + "Expected 'namespace:path' (e.g. 'minecraft:diamond') or "
                            + "'#namespace:tag' (e.g. '#minecraft:planks'). Only lowercase letters, "
                            + "numbers, '_', '-', '.' and '/' are allowed",
                    itemId, isTag ? "tag" : "item ID", value, where);
            return null;
        }

        JsonObject obj = new JsonObject();
        obj.addProperty(isTag ? "tag" : "item", rl);
        return obj;
    }

    private static JsonObject buildResult(String itemId, int count) {
        JsonObject result = new JsonObject();
        result.addProperty("id", "customgear:" + itemId);
        if (count > 1) {
            result.addProperty("count", Math.min(count, 64));
        }
        return result;
    }
}