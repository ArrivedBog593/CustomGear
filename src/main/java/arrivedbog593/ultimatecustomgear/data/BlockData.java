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

    /** Texture path, e.g. "customgear:block/ruby_ore" */
    public GearData.TextureData texture;

    public BlockFaces faces;

    public static class BlockFaces {
        public String top;
        public String bottom;
        public String north;
        public String south;
        public String east;
        public String west;
        // Optional "side" field for blocks that use the same texture on all faces
        public String side;
    }

    /**
     * Translatable names by language code.
     * e.g. {"en_us": "Ruby Ore", "es_mx": "Mineral de Rubí"}
     */
    public Map<String, String> names;

    public List<RecipeData> recipe;
}