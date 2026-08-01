package arrivedbog593.ultimatecustomgear.data;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Represents a single crafting recipe entry inside a gear/item JSON.
 * A recipe can be one of: shaped, shapeless, smelting, blasting, smoking,
 * campfire_cooking, stonecutting, smithing_transform, passthrough.
 * <p>
 * In JSON, the "recipe" field can be either a single object or an array
 * of objects (for items that have multiple ways to be crafted).
 */
public class RecipeData {

    /** Recipe type: shaped, shapeless, smelting, blasting, smoking,
     *  campfire_cooking, stonecutting, smithing_transform, passthrough */
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

    // ── Smelting / Blasting / Smoking / Campfire Cooking / Stonecutting ────────

    /**
     * Single input item ID for furnace-type recipes.
     * e.g. "minecraft:iron_ore"
     */
    public String ingredient;

    /**
     * XP granted when the recipe completes. Default: 0.1
     * <p>
     * Boxed so an undeclared value is distinguishable from a declared 0.1 —
     * that is what lets the parser warn when it appears on a type that has no
     * experience. NEVER read directly, use experience().
     */
    public Float experience;

    /** Resolved XP: 0.1 when undeclared. */
    public float experience() {
        return experience != null ? experience : 0.1f;
    }

    /**
     * Time to cook, in SECONDS. Decimals allowed (2.5 works).
     * <p>
     * CHANGED IN 1.6.0: this used to be in ticks. A recipe written for an older
     * version reads 20x too slow now — divide the old value by 20.
     * <p>
     * Default: -1, meaning "use the type default": 10s smelting, 5s blasting,
     * 5s smoking, 30s campfire. Ignored by stonecutting, which is instant.
     */
    @SerializedName("cooking_time")
    public float cookingTime = -1;

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

    // ── Passthrough ───────────────────────────────────────────────────────────

    /**
     * Raw recipe body for type "passthrough", copied into the datapack as-is.
     * Lets another mod's recipe type produce this mod's content without this
     * mod understanding that schema.
     * <p>
     * The inner "type" decides which mod is required, so
     * {"type": "create:mixing", ...} gets a create mod_loaded condition
     * automatically. Unlike every other type, the RESULT comes from inside
     * this object and is not controlled by the owning item.
     */
    public JsonObject json;

    /**
     * Extra mod IDs this passthrough recipe needs, beyond the one deduced from
     * the inner type's namespace. Only for recipes that span several mods —
     * the normal case needs nothing here.
     */
    public List<String> requires;

    // ── Common ────────────────────────────────────────────────────────────────

    /**
     * Number of items produced by this recipe. Default: 1.
     * Applies to shaped, shapeless, and stonecutting recipes.
     * <p>
     * Boxed for the same reason as experience — see above. Use resultCount().
     */
    @SerializedName("result_count")
    public Integer resultCount;

    /** Resolved count: 1 when undeclared. */
    public int resultCount() {
        return resultCount != null ? resultCount : 1;
    }
}