package arrivedbog593.ultimatecustomgear.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * Tags this content belongs to, without the '#' prefix
     * (e.g. "c:ingots", "c:ingots/ruby", "minecraft:planks").
     * Lets other mods' recipes — and your own — accept this via #tag.
     */
    @SerializedName("tags")
    public List<String> tags;

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

        /** Face keys recognized inside refs (per-face mode). */
        private static final Set<String> FACE_KEYS = Set.of(
                "top", "bottom", "north", "south", "east", "west", "side");

        /**
         * Resolves the per-face config, unifying the two ways to declare it:
         *   1. The typed "faces" object (legacy).
         *   2. Face keys ("top"/"side"/...) placed directly in "refs".
         * Returns null if neither declares any face → single-texture block
         * (refs.all / refs.block), handled elsewhere.
         * If both exist, "faces" wins and a caller may warn.
         */
        public BlockFaces resolveFaces() {
            if (faces != null) return faces;
            if (refs == null) return null;

            boolean hasFaceKey = refs.keySet().stream().anyMatch(FACE_KEYS::contains);
            if (!hasFaceKey) return null;

            BlockFaces f = new BlockFaces();
            f.top    = refs.get("top");
            f.bottom = refs.get("bottom");
            f.north  = refs.get("north");
            f.south  = refs.get("south");
            f.east   = refs.get("east");
            f.west   = refs.get("west");
            f.side   = refs.get("side");
            return f;
        }

        /** Single-texture reference: "all" canonical, "block" legacy alias. */
        public String allRef() {
            if (refs == null) return null;
            String r = refs.get("all");
            return r != null ? r : refs.get("block");
        }
    }
}