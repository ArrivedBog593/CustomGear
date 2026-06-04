package com.github.arrivedbog593.loader;

import com.github.arrivedbog593.data.BlockData;
import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.data.ItemData;
import com.github.arrivedbog593.data.RecipeData;
import com.github.arrivedbog593.resources.DynamicResourcePack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Generates recipe JSON files and injects them into the DynamicResourcePack
 * as SERVER_DATA, so Minecraft loads them through the normal data pack system.
 * <p>
 * This approach makes recipes visible to JEI and works without reflection.
 * Item existence is NOT validated here — Minecraft validates recipes when loading
 * the data pack, and custom items are not yet registered at mod init time.
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
        for (Map.Entry<String, String> entry : data.key.entrySet()) {
            if (entry.getKey().length() != 1) {
                LOGGER.warn("[CustomGear] Shaped recipe for '{}': key '{}' must be 1 char — skipping",
                        itemId, entry.getKey());
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
            JsonObject ingredient = new JsonObject();
            ingredient.addProperty("item", entry.getValue());
            key.add(entry.getKey(), ingredient);
        }
        obj.add("key", key);

        obj.add("result", buildResult("customgear:" + itemId, data.resultCount));
        return obj;
    }

    // ── Shapeless ─────────────────────────────────────────────────────────────

    private static JsonObject buildShapeless(String itemId, RecipeData data) {
        if (data.ingredients == null || data.ingredients.isEmpty()) {
            LOGGER.warn("[CustomGear] Shapeless recipe for '{}' missing 'ingredients' — skipping", itemId);
            return null;
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:crafting_shapeless");

        JsonArray ingredients = new JsonArray();
        for (String id : data.ingredients) {
            JsonObject ingredient = new JsonObject();
            ingredient.addProperty("item", id);
            ingredients.add(ingredient);
        }
        obj.add("ingredients", ingredients);

        obj.add("result", buildResult("customgear:" + itemId, data.resultCount));
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

        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("item", data.ingredient);
        obj.add("ingredient", ingredient);

        obj.add("result", buildResult("customgear:" + itemId, 1));
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

        JsonObject template = new JsonObject();
        template.addProperty("item", data.template);
        obj.add("template", template);

        JsonObject base = new JsonObject();
        base.addProperty("item", data.base);
        obj.add("base", base);

        JsonObject addition = new JsonObject();
        addition.addProperty("item", data.addition);
        obj.add("addition", addition);

        obj.add("result", buildResult("customgear:" + itemId, 1));
        return obj;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static JsonObject buildResult(String itemId, int count) {
        JsonObject result = new JsonObject();
        result.addProperty("id", itemId);
        if (count > 1) result.addProperty("count", count);
        return result;
    }
}