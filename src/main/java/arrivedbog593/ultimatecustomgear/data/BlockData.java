package arrivedbog593.ultimatecustomgear.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class BlockData {

    /** Unique block ID, e.g. "ruby_ore" */
    public String id;

    /** Type: must be "block" */
    public String type;

    /**
     * Light emitted by this block (0-15).
     * Default: 0 (no light)
     */
    @SerializedName("light_level")
    public int lightLevel = 0;

    /**
     * Time to destroy the block with the correct tool, in seconds.
     * Vanilla reference: stone=1.5, iron_ore=3.0, obsidian=9.5, bedrock=-1 (unbreakable)
     * Default: 3.0
     */
    @SerializedName("destroy_time")
    public float destroyTime = 3.0f;

    /**
     * Resistance to explosions. Higher = more resistant.
     * Vanilla reference: stone=6.0, obsidian=1200.0, bedrock=3600000.0
     * Default: 3.0
     */
    @SerializedName("explosion_resistance")
    public float explosionResistance = 3.0f;

    /**
     * Sound type when placing, breaking or walking on the block.
     * Vanilla values: stone, wood, gravel, grass, sand, metal, glass,
     *   wool, leaves, deepslate, amethyst, netherite, ancient_debris, etc.
     * Default: "stone"
     */
    public String sound = "stone";

    /**
     * If true, the block rotates to face the player when placed,
     * like a furnace or dispenser. Requires texture.faces.north to be defined
     * as the face that will point toward the player.
     * Only the 4 horizontal directions are supported (north, south, east, west).
     */
    public boolean directional = false;

    /**
     * If true, the block falls when it has no support below, like sand or gravel.
     * Cannot be combined with directional.
     */
    public boolean gravity = false;

    @SerializedName("map_color")
    public String mapColor;

    @SerializedName("required_tool")
    public String requiredTool;

    @SerializedName("harvest_level")
    public int harvestLevel = 0;

    /** Texture configuration */
    public BlockTextureData texture;

    public static class BlockFaces {
        public String top;
        public String bottom;
        public String north;
        public String south;
        public String east;
        public String west;
        /** Shortcut: applies to north/south/east/west if not individually defined */
        public String side;
    }

    /**
     * Translatable names by language code.
     * e.g. {"en_us": "Ruby Ore", "es_mx": "Mineral de Rubí"}
     */
    public Map<String, String> names;

    public List<RecipeData> recipe;

    public static class BlockTextureData {
        public String mode;
        public Map<String, String> refs;
        public BlockFaces faces;
    }
}