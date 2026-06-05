package arrivedbog593.ultimatecustomgear.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Represents a single crafting recipe entry inside a gear/item JSON.
 * A recipe can be one of: shaped, shapeless, smelting, blasting, smoking,
 * smithing_transform.
 * <p>
 * In JSON, the "recipe" field can be either a single object or an array
 * of objects (for items that have multiple ways to be crafted).
 */
public class RecipeData {

    /** Recipe type: shaped, shapeless, smelting, blasting, smoking, smithing_transform */
    public String type;

    // ── Shaped ───────────────────────────────────────────────────────────────

    /**
     * 3-element array of strings defining the crafting pattern.
     * Use spaces for empty slots. e.g. ["DDD", "D D", "DDD"]
     */
    public List<String> pattern;

    /**
     * Maps each character in the pattern to an item ID.
     * e.g. {"D": "minecraft:diamond", "S": "minecraft:stick"}
     */
    public Map<String, String> key;

    // ── Shapeless ─────────────────────────────────────────────────────────────

    /**
     * List of item IDs for shapeless crafting (order does not matter).
     * e.g. ["minecraft:diamond", "minecraft:stick"]
     */
    public List<String> ingredients;

    // ── Smelting / Blasting / Smoking ─────────────────────────────────────────

    /**
     * Single input item ID for furnace-type recipes.
     * e.g. "minecraft:iron_ore"
     */
    public String ingredient;

    /** XP granted when the recipe completes. Default: 0.1 */
    public float experience = 0.1f;

    /**
     * Ticks to cook. Default: 200 (smelting), 100 (blasting), 100 (smoking).
     * Use -1 to keep the type default.
     */
    @SerializedName("cooking_time")
    public int cookingTime = -1;

    // ── Smithing Transform ────────────────────────────────────────────────────

    /**
     * Smithing template item ID.
     * e.g. "minecraft:netherite_upgrade_smithing_template"
     * Can be any item including ones created by this mod.
     */
    public String template;

    /**
     * Base item ID to upgrade in the smithing table.
     * e.g. "minecraft:diamond_sword"
     */
    public String base;

    /**
     * Addition item ID (the upgrade material).
     * e.g. "minecraft:netherite_ingot"
     */
    public String addition;

    // ── Common ────────────────────────────────────────────────────────────────

    /**
     * Number of items produced by this recipe. Default: 1.
     * Only applies to shaped and shapeless recipes.
     */
    @SerializedName("result_count")
    public int resultCount = 1;
}