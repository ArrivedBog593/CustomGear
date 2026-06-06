package arrivedbog593.ultimatecustomgear.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class ItemData {

    /** Unique item ID, e.g. "ruby_gem" */
    public String id;

    /**
     * Type: "item" for a simple item, "food" for a consumable item.
     */
    public String type;

    /** Texture path */
    public GearData.TextureData texture;

    /**
     * Translatable names by language code.
     * e.g. {"en_us": "Ruby", "es_mx": "Rubí"}
     */
    public Map<String, String> names;

    public List<RecipeData> recipe;

    // ── Food fields (only used when type = "food") ────────────────────────────

    /**
     * Hunger points restored when eaten. Vanilla reference:
     * bread = 5, cooked beef = 8, golden apple = 4
     */
    public int nutrition = 0;

    /**
     * Saturation modifier. Multiplied with nutrition to determine total saturation.
     * Vanilla reference: bread = 0.6, cooked beef = 0.8, golden apple = 1.2
     */
    public float saturation = 0.6f;

    /**
     * If true, the item can be eaten even when the hunger bar is full.
     * Useful for items that only apply effects (like golden apples).
     */
    @SerializedName("always_edible")
    public boolean alwaysEdible = false;

    /**
     * If true, the item is eaten faster (like dried kelp).
     */
    @SerializedName("fast_food")
    public boolean fastFood = false;

    /**
     * Time in ticks to fully consume this food item.
     * 20 ticks = 1 second. Default: 32 (vanilla normal speed).
     * Set to 0 for instant consumption.
     * fast_food overrides this to 16 if eat_duration is not set.
     * Examples: 16 = fast, 32 = normal, 100 = 5 seconds, 200 = 10 seconds
     */
    @SerializedName("eat_duration")
    public int eatDuration = -1; // -1 = use vanilla default

    /**
     * Effects applied when the item is eaten.
     * Each entry has: effect, amplifier, duration (in seconds), and probability (0.0-1.0).
     */
    @SerializedName("on_eat_effects")
    public List<FoodEffectData> onEatEffects;

    public static class FoodEffectData {
        /** Effect ID, e.g. "minecraft:regeneration" */
        public String effect;

        /** Effect level minus 1. 0 = Level I, 1 = Level II, etc. */
        public int amplifier = 0;

        /** Duration in seconds. Default: 5 */
        public int duration = 5;

        /**
         * Probability of applying the effect (0.0 to 1.0).
         * 1.0 = always applies. Default: 1.0
         */
        public float probability = 1.0f;
    }
}