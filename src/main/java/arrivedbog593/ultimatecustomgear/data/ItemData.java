package arrivedbog593.ultimatecustomgear.data;

import java.util.List;
import java.util.Map;

public class ItemData {

    /** Unique item ID, e.g. "ruby_gem" */
    public String id;

    /** Type: must be "item" */
    public String type;

    /** Texture path, e.g. "customgear:item/ruby_gem" */
    public GearData.TextureData texture;

    /**
     * Translatable names by language code.
     * e.g. {"en_us": "Ruby", "es_mx": "Rubí"}
     */
    public Map<String, String> names;

    public List<RecipeData> recipe;
}