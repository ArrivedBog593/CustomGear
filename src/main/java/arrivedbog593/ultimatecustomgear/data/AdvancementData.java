package arrivedbog593.ultimatecustomgear.data;

import java.util.List;
import java.util.Map;

public class AdvancementData {

    /** Unique advancement ID, e.g. "obtain_ruby" */
    public String id;

    /** Type: must be "advancement" */
    public String type;

    /**
     * Criterion type.
     * "inventory_changed" = obtain an item
     * "placed_block" = place a block
     */
    public String criterionType = "inventory_changed";

    /** ID of the item or block that triggers the advancement, e.g. "customgear:ruby_gem" */
    public String targetId;

    /** Parent advancement ID. Null or blank = root. e.g. "customgear:root" */
    public String parent;

    /** Advancement icon (item ID), e.g. "customgear:ruby_gem" */
    public String icon;

    /** Frame type: "task", "goal", "challenge" */
    public String frame = "task";

    /** If true, the advancement is announced in chat when unlocked */
    public boolean announceToChat = true;

    /** If true, the advancement appears in the advancements screen */
    public boolean showToast = true;

    /** If true, the advancement is hidden until unlocked */
    public boolean hidden = false;

    /**
     * Translatable titles by language code.
     * e.g. {"en_us": "Ruby Collector", "es_mx": "Coleccionista de Rubíes"}
     */
    public Map<String, String> titles;

    /**
     * Translatable descriptions by language code.
     * e.g. {"en_us": "Obtain a ruby", "es_mx": "Obtén un rubí"}
     */
    public Map<String, String> descriptions;

    /** Optional advancement rewards */
    public Rewards rewards;

    public static class Rewards {
        /** Recipe IDs unlocked by this advancement, e.g. ["customgear:ruby_sword"] */
        public List<String> recipes;
        /** Experience points awarded */
        public int experience = 0;
    }
}