package com.github.arrivedbog593.data;

import java.util.Map;

public class FluidData {

    /** Unique fluid ID, e.g. "liquid_ruby" */
    public String id;

    /** Type: must be "fluid" */
    public String type;

    /** Light emission of the fluid block (0-15) */
    public int lightLevel = 0;

    /** Still texture path, e.g. "customgear:fluid/liquid_ruby_still" */
    public String textureStill;

    /** Flowing texture path, e.g. "customgear:fluid/liquid_ruby_flow" */
    public String textureFlowing;

    /** Fluid block model path, e.g. "customgear:fluid/liquid_ruby" */
    public String model;

    /**
     * Translatable names by language code.
     * e.g. {"en_us": "Liquid Ruby", "es_mx": "Rubí Líquido"}
     */
    public Map<String, String> names;
}