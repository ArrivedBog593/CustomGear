package com.github.arrivedbog593.data;

import java.util.Map;

public class ItemData {

    /** Unique item ID, e.g. "ruby_gem" */
    public String id;

    /** Type: must be "item" */
    public String type;

    /** Texture path, e.g. "customgear:item/ruby_gem" */
    public String texture;

    /** Model path, e.g. "customgear:item/ruby_gem" */
    public String model;

    /**
     * Translatable names by language code.
     * e.g. {"en_us": "Ruby", "es_mx": "Rubí"}
     */
    public Map<String, String> names;
}