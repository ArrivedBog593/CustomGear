package arrivedbog593.ultimatecustomgear.data;

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
     * Supports {fluid_name} placeholder that will be replaced with the fluid name.
     * e.g. {"en_us": "{fluid_name} Bucket", "es_mx": "Cubeta de {fluid_name}"}
     */
    public Map<String, String> bucketNames;

    /**
     * Texture configuration.
     * - Default mode: no texture object needed
     * - Custom mode: refs contain "still", "flowing", "bucket" paths
     * - Reference mode: refs contain resource locations
     */
    public TextureConfig texture;

    /** Tint color in ARGB hex format, e.g. "0xFF3F76E4" for water blue. Default: 0xFFFFFFFF (white/no tint) */
    public String color = "0xFFFFFFFF";

    public static class TextureConfig {
        public String mode; // "default", "custom", "reference"
        public Map<String, String> refs; // still, flowing, bucket
    }
}