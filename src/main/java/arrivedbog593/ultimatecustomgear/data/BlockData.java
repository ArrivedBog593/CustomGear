package arrivedbog593.ultimatecustomgear.data;

import java.util.List;
import java.util.Map;

public class BlockData {

    /** Unique block ID, e.g. "ruby_ore" */
    public String id;

    /** Type: must be "block" */
    public String type;

    /** Light emission (0-15) */
    public int lightLevel = 0;

    /**
     * If true, the block rotates to face the player when placed,
     * like a furnace or dispenser. Requires faces.north (or front) to be defined
     * as the face that will point toward the player.
     * Only the 4 horizontal directions are supported (north, south, east, west).
     */
    public boolean directional = false;

    /** Texture path, e.g. "customgear:block/ruby_ore" */
    public BlockTextureData texture;

    /**
     * Per-face texture configuration.
     * In custom mode: each value is a path to a PNG file inside the customgear folder.
     * In reference mode: each value is a resource location (e.g. "minecraft:block/stone").
     * <p>
     * Shortcuts:
     *   - "side" applies to north, south, east, west if they are not individually defined
     *   - "all"  applies to all 6 faces if none are individually defined (same as cube_all)
     * <p>
     * For directional blocks, "north" is treated as the front face.
     */

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