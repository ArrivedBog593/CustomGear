package arrivedbog593.ultimatecustomgear.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class FluidData {

    /** Unique fluid ID, e.g. "liquid_ruby" */
    public String id;

    /** Type: must be "fluid" */
    public String type;

    /** Light emission of the fluid block (0-15) */
    public int lightLevel = 0;

    /**
     * Translatable names by language code.
     * e.g. {"en_us": "Liquid Ruby", "es_mx": "Rubí Líquido"}
     */
    public Map<String, String> names;

    /**
     * Translatable bucket names by language code.
     * Supports {fluid_name} placeholder.
     * e.g. {"en_us": "{fluid_name} Bucket", "es_mx": "Cubeta de {fluid_name}"}
     */
    public Map<String, String> bucketNames;

    /**
     * If true, the ITEM survives fire and lava when dropped (like netherite).
     *  Does NOT make the wearer fire-immune (use fire_resistance effects for that). */
    @SerializedName("fire_resistant")
    public boolean fireResistant = false;

    /**
     * Tags this content belongs to, without the '#' prefix
     * (e.g. "c:ingots", "c:ingots/ruby", "minecraft:planks").
     * Lets other mods' recipes — and your own — accept this via #tag.
     */
    @SerializedName("tags")
    public List<String> tags;

    /** Tint color in ARGB hex format, e.g. "0xFF3F76E4" for water blue. Default: 0xFFFFFFFF */
    public String color = "0xFFFFFFFF";

    /**
     * Fluid textures: "still", "flowing" and "bucket".
     * <p>
     * Declaring neither still nor flowing makes this a water reskin, which is
     * also what gives it water's blue tint. Declaring one and not the other is
     * rejected: the fluid would draw its own still texture and water's flowing
     * one.
     * <p>
     * Values take the SHORT form — minecraft:block/lava_still — because a fluid
     * sprite is addressed that way by the atlas.
     */
    public TextureData texture;

    // ── Fluid behavior ────────────────────────────────────────────────────────

    /**
     * How many ticks between each spread step.
     * Lower = faster spreading. Water = 5, Lava (overworld) = 30, Lava (nether) = 10.
     * Default: 5 (water speed)
     */
    @SerializedName("tick_rate")
    public int tickRate = 5;

    /**
     * Maximum horizontal spread distance in blocks.
     * Water = 8, Lava = 4.
     * Default: 8
     */
    @SerializedName("spread_distance")
    public int spreadDistance = 8;

    /**
     * If true, the fluid sets entities on fire when they touch it, like lava.
     * Default: false
     */
    @SerializedName("burns_entities")
    public boolean burnsEntities = false;

    // ── Contact effects ───────────────────────────────────────────────────────

    /**
     * Effects applied while an entity is submerged in this fluid.
     * Applied every contact_effect_interval ticks.
     * e.g. poison, night_vision, slowness
     */
    @SerializedName("contact_effects")
    public List<ContactEffectData> contactEffects;

    /**
     * How often (in seconds) the contact effects are reapplied while in the fluid.
     * Default: 1 (every second)
     */
    @SerializedName("contact_effect_interval")
    public float contactEffectInterval = 1.0f;

    /**
     * Seconds the entity burns after touching the fluid.
     * Only used if burns_entities is true. Default: 5
     */
    @SerializedName("burn_duration")
    public int burnDuration = 5;

    public static class ContactEffectData {
        /** Effect ID, e.g. "minecraft:poison" */
        public String effect;

        /** Effect level minus 1. 0 = Level I, 1 = Level II, etc. */
        public int amplifier = 0;

        /**
         * Duration in seconds for each application.
         * Should be slightly longer than contact_effect_interval / 20
         * to avoid flickering. Default: 3
         */
        public int duration = 3;
    }

}