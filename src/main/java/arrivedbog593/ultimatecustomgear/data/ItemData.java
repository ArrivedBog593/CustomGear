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

    /**
     * Tags this content belongs to, without the '#' prefix
     * (e.g. "c:ingots", "c:ingots/ruby", "minecraft:planks").
     * Lets other mods' recipes — and your own — accept this via #tag.
     */
    @SerializedName("tags")
    public List<String> tags;

    /** If true, the ITEM survives fire and lava when dropped (like netherite).
     *  Does NOT make the wearer fire-immune (use fire_resistance effects for that). */
    @SerializedName("fire_resistant")
    public boolean fireResistant = false;

    /** Texture path */
    public TextureData texture;

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
     * Time in seconds to fully consume this food item.
     * Default: -1 (uses vanilla default: 1.6s normal, 0.8s fast_food).
     * Set to 0 for instant consumption.
     * Examples: 1 = fast, 1.6 = normal vanilla, 5 = slow, 10 = very slow
     */
    @SerializedName("eat_duration")
    public float eatDuration = -1;

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

    // ───── Mob drop fields (available on any item type) ─────

    /** Drops from mobs — the source of an item economy. */
    @SerializedName("mob_drops")
    public MobDropsData mobDrops;

    public static class MobDropsData {
        /** Probability that this item drops at all, 0.0-1.0 */
        public double chance = 0.05;

        /** Minimum amount. 0 is allowed: the roll can come up empty, like
         *  vanilla's 0-2 rotten flesh. Note this compounds with 'chance'. */
        public int min = 1;
        public int max = 1;

        /** Entity type IDs. Empty or absent DISABLES the drop — use "all" if
         *  you really want every mob. Players and armor stands never drop. */
        public List<String> entities;

        /**
         * Whether a player must be involved in the kill. Boxed on purpose:
         * null means "not declared", which is what lets the parser warn that
         * the default flipped to false in 1.6.0.
         * NEVER read this field directly — use isPlayerKillRequired().
         */
        @SerializedName("requires_player_kill")
        public Boolean requiresPlayerKill;

        /** Resolved value. Undeclared = false since 1.6.0 (vanilla parity). */
        public boolean isPlayerKillRequired() {
            return requiresPlayerKill != null && requiresPlayerKill;
        }

        /**
         * How the Looting enchantment affects this drop. Vanilla uses two
         * separate mechanisms and never both at once, so this picks one:
         * <ul>
         *   <li>"count"  — adds 0..level extra items on top of the rolled
         *       amount, the shape of enchanted_count_increase. This is what
         *       common drops use (rotten flesh, string, gunpowder) and the
         *       right choice for an item economy. DEFAULT.</li>
         *   <li>"chance" — raises the drop probability instead, leaving the
         *       amount alone. This is what rare drops use (wither skeleton
         *       skulls, mob equipment): the roll decides IF it drops, never
         *       how many.</li>
         *   <li>"none"   — Looting does nothing for this item.</li>
         * </ul>
         * Unknown values fall back to "count" with a warning.
         */
        @SerializedName("looting_mode")
        public String lootingMode = "count";

        /**
         * Probability added per Looting level in "chance" mode, e.g. 0.01 =
         * +1 percentage point per level.
         * <p>
         * Defaults to vanilla's most familiar case: skull drops add 1 point
         * per level. It is NOT a universal constant, which is why this is
         * exposed — vanilla loot tables declare their own multiplier, and the
         * shulker shell uses a much steeper curve. Ignored (with a warning)
         * in any mode other than "chance".
         */
        @SerializedName("looting_chance_bonus")
        public double lootingChanceBonus = 0.01;

        /** True when Looting adds to the rolled amount. */
        public boolean isLootingCount() {
            return !"chance".equals(lootingMode) && !"none".equals(lootingMode);
        }

        /** True when Looting raises the drop probability instead. */
        public boolean isLootingChance() {
            return "chance".equals(lootingMode);
        }
    }
}