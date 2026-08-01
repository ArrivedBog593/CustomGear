package arrivedbog593.ultimatecustomgear.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * A tag_patch declares that FOREIGN content belongs to a tag — the inverse
 * direction of the "tags" field.
 * <p>
 * The "tags" field on an item/block/fluid says, "MY content belongs to tag X":
 * the mod controls the left side, and the value written into the tag file is
 * always customgear:&lt;id&gt;. That makes some things structurally impossible.
 * Damage types are the clearest case: they are not items, so there is no
 * ItemData to hang a "tags" field on, and a damage type from another mod could
 * never be referenced at all.
 * <p>
 * tag_patch flips it — the mod controls the right side:
 * <pre>
 * {
 *   "type": "tag_patch",
 *   "registry": "damage_type",
 *   "tag": "customgear:dragon_breath",
 *   "values": [
 *     "iceandfire:dragon_fire",
 *     "iceandfire:dragon_ice"
 *   ]
 * }
 * </pre>
 * which lets armor reference one tag instead of listing every id:
 * <pre>
 * "damage_resistances": { "#customgear:dragon_breath": 0.30 }
 * </pre>
 * <p>
 * A patch can also take content OUT of a tag:
 * <pre>
 * {
 *   "type": "tag_patch",
 *   "registry": "item",
 *   "tag": "minecraft:trimmable_armor",
 *   "remove": ["othermod:weird_chestplate"]
 * }
 * </pre>
 * "values" and "remove" are both optional, but a patch with neither does
 * nothing and is rejected.
 * <p>
 * This is doable by hand with a datapack (see DAMAGE_TYPES.md). What the mod
 * buys you is that the patch lives in the content root, so it enters the
 * content hash and travels inside the pack zip — a player joining the server
 * gets the tag along with the armor instead of installing a datapack too.
 * <p>
 * There is no "id": a patch registers nothing. It is identified by
 * registry + tag, and two patches naming the same pair simply merge, the same
 * way tags merge everywhere else.
 * <p>
 * Note: "replace" is not exposed, and every entry is emitted with
 * required:false. See TagFileBuilder for the reasoning on both.
 */
public class TagPatchData {

    /**
     * Registry the tag belongs to, without namespace: "item", "block",
     * "fluid", "entity_type", "damage_type", "enchantment"...
     * <p>
     * Singular, matching 1.21's tag directory names. A leading "minecraft:"
     * is tolerated and stripped.
     */
    public String registry;

    /** Full tag id WITHOUT '#', e.g. "customgear:dragon_breath". */
    public String tag;

    /** Content ids to add. Foreign ids are expected — that is the point. */
    public List<String> values;

    /**
     * Content ids to REMOVE from the tag, even when another pack added them.
     * <p>
     * The counterpart of "values". Needed for tags that are filled by
     * inheritance rather than by explicit entries: vanilla's trimmable_armor is
     * the union of the four slot tags, so armor lands in it just by being
     * armor, and no amount of not-adding takes it out.
     * <p>
     * NeoForge extension, not vanilla. And unlike "values", entries here cannot
     * be marked optional — the array is plain ids — so removing content from a
     * mod that is not installed is not something this can express safely.
     */
    public List<String> remove;

    /**
     * Optional note for whoever reads the file later. Never parsed; it exists
     * because a patch has no name or id, so without this the file gives no
     * clue why it exists.
     */
    @SuppressWarnings("unused")
    @SerializedName("comment")
    public String comment;

    /** Registry name with any "minecraft:" prefix stripped, or null. */
    public String normalizedRegistry() {
        if (registry == null || registry.isBlank()) return null;
        String r = registry.trim();
        int colon = r.indexOf(':');
        if (colon >= 0) {
            String ns = r.substring(0, colon);
            if (!ns.equals("minecraft")) return null; // foreign registry ns: not supported
            r = r.substring(colon + 1);
        }
        return r.isBlank() ? null : r;
    }
}