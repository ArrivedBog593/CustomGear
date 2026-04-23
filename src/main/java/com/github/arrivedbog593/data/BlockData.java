package com.github.arrivedbog593.data;

import java.util.Map;

public class BlockData {

    /** Unique block ID, e.g. "ruby_ore" */
    public String id;

    /** Type: must be "block" */
    public String type;

    /** Light emission (0-15) */
    public int lightLevel = 0;

    /** Texture path, e.g. "customgear:block/ruby_ore" */
    public String texture;

    /** Model path, e.g. "customgear:block/ruby_ore" */
    public String model;

    /**
     * Translatable names by language code.
     * e.g. {"en_us": "Ruby Ore", "es_mx": "Mineral de Rubí"}
     */
    public Map<String, String> names;
}