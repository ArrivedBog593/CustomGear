package arrivedbog593.ultimatecustomgear.datapack;

import arrivedbog593.ultimatecustomgear.resources.PackSink;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Shared accumulator for every tag file the mod injects into the dynamic pack.
 * <p>
 * WHY THIS EXISTS: DynamicResourcePack.addRaw is keyed by ResourceLocation, so
 * two loaders writing the same tag path silently overwrite each other — the last
 * one wins, no warning, entries just vanish. That was already reachable before
 * this class existed: a block declaring "tags": ["minecraft:mineable/pickaxe"]
 * lands on exactly the path BlockTagLoader generates from required_tool.
 * <p>
 * So no loader emits tag files on its own any more. They all feed one builder,
 * which merges everything per (registry, tag) and emits ONE file each at the
 * end. Adding a new tag source is now safe by construction.
 * <p>
 * Usage — build, feed, emit once:
 * <pre>
 *   TagFileBuilder tags = new TagFileBuilder();
 *   BlockTagLoader.loadAll(tags, blocks);
 *   ItemTagLoader.loadAll(tags, items, blocks, fluids);
 *   tags.emit(DYNAMIC_PACK);
 * </pre>
 * A builder is single-use: make a fresh one on every load and on every
 * /customgear reload, alongside the DYNAMIC_PACK.clear().
 * <p>
 * "replace" is always false and is NOT configurable. A tag file with
 * replace:true drops every other member of that tag — on a vanilla tag like
 * minecraft:planks that means no wood is valid in any recipe, game-wide. There
 * is no use case worth that failure mode.
 * <p>
 * "remove" is a NeoForge extension and the only way to opt OUT of a tag another
 * pack fills. It matters for inherited tags: vanilla's trimmable_armor is the
 * union of the four slot tags, so armor lands in it just by being armor. Not
 * adding an id does nothing there — removal has to be explicit.
 */
public class TagFileBuilder {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Gson   GSON   = new GsonBuilder().setPrettyPrinting().create();

    /** One output file: a registry ("item" | "block" | "fluid" | ...) plus the tag id. */
    private record TagKey(String registry, ResourceLocation tag) {}

    /**
     * (registry, tag) → ordered map of contentId → required.
     * <p>
     * LinkedHashMap at both levels keeps file and entry order deterministic,
     * which keeps the content hash stable across restarts — the handshake
     * compares hashes, so nondeterministic ordering would desync clients.
     */
    private final Map<TagKey, Map<String, Boolean>> files = new LinkedHashMap<>();

    /**
     * (registry, tag) → ids to REMOVE from that tag.
     * <p>
     * Separate map because remove is not the opposite of add within this
     * builder: add fills the "values" array of OUR file, while remove emits a
     * "remove" array telling the game to drop entries OTHER packs put there.
     * Taking an id out of `files` would only mean "we never added it" — the
     * other pack's entry would survive untouched.
     */
    private final Map<TagKey, Set<String>> removals = new LinkedHashMap<>();

    /**
     * Adds one content id to one tag.
     *
     * @param required true for content this mod registers (it always exists);
     *                 false for foreign content, which is emitted as
     *                 {"id": ..., "required": false} so a missing mod does not
     *                 blow up the whole tag at datapack load.
     */
    public void add(String registry, ResourceLocation tag, String contentId, boolean required) {
        files.computeIfAbsent(new TagKey(registry, tag), k -> new LinkedHashMap<>())
                // Same id added twice from different sources: the LEAST strict
                // wins. If any source says it may be absent, treating it as
                // required could hard-fail the tag.
                .merge(contentId, required, (a, b) -> a && b);
    }

    /**
     * Removes one content id from a tag, even when another pack added it.
     * <p>
     * NEOFORGE extension to the tag format, not vanilla. It is the only way to
     * opt out of an inherited tag: vanilla's trimmable_armor is the union of
     * the four slot tags, so anything in #minecraft:chest_armor is trimmable by
     * inheritance and not adding it changes nothing. Mekanism does exactly this
     * for its MekaSuit.
     */
    public void remove(String registry, ResourceLocation tag, String contentId) {
        removals.computeIfAbsent(new TagKey(registry, tag), k -> new LinkedHashSet<>())
                .add(contentId);
    }

    /**
     * Same as {@link #add} but takes the tag as a raw user string, handling the
     * '#' tolerance and parse failure. Returns false if the tag was malformed
     * and skipped.
     *
     * @param owner human-readable source for the log, e.g. "item 'ruby_gem'"
     */
    public void addParsed(String registry, String rawTag, String contentId,
                          boolean required, String owner) {
        if (rawTag == null || rawTag.isBlank()) return;

        // Tolerate a stray '#': it belongs on the CONSUMING side, but a user
        // pasting "#c:ingots" here shouldn't silently fail.
        String cleaned = rawTag.startsWith("#") ? rawTag.substring(1) : rawTag;

        ResourceLocation tagRl = ResourceLocation.tryParse(cleaned);
        if (tagRl == null) {
            LOGGER.warn("[CustomGear] {}: malformed tag '{}' — skipped. Expected "
                    + "'namespace:path' like 'c:ingots' or 'minecraft:planks' "
                    + "(lowercase letters, numbers, '_', '-', '.', '/')", owner, rawTag);
            return;
        }

        add(registry, tagRl, contentId, required);
    }

    /** Emits one JSON per (registry, tag), including removal-only files. */
    public void emit(PackSink pack) {
        int count = 0;

        // Every tag needing a file: with entries, with removals, or both.
        // A tag that only removes still needs its own file.
        Set<TagKey> allKeys = new LinkedHashSet<>(files.keySet());
        allKeys.addAll(removals.keySet());

        for (TagKey key : allKeys) {
            Map<String, Boolean> entries = files.getOrDefault(key, Map.of());
            Set<String> toRemove = removals.getOrDefault(key, Set.of());

            JsonObject obj = new JsonObject();
            obj.addProperty("replace", false); // never configurable — see class doc

            JsonArray values = new JsonArray();
            for (Map.Entry<String, Boolean> entry : entries.entrySet()) {
                // REMOVE WINS over add when both are declared for the same id.
                // Adding is the ordinary operation and can come from a broad
                // rule — GearTagLoader adds #minecraft:swords to every sword
                // there is. Removing is always deliberate: nobody writes a
                // removal by accident. So the narrower intent wins, the same
                // way a per-piece resistance beats the set-level one.
                if (toRemove.contains(entry.getKey())) {
                    LOGGER.warn("[CustomGear] Tag '{}': '{}' is both added and removed — the "
                                    + "removal wins and the entry is left out.",
                            key.tag(), entry.getKey());
                    continue;
                }

                if (entry.getValue()) {
                    // Our own content: plain string, keeps generated files readable
                    values.add(entry.getKey());
                } else {
                    JsonObject optional = new JsonObject();
                    optional.addProperty("id", entry.getKey());
                    optional.addProperty("required", false);
                    values.add(optional);
                }
            }
            obj.add("values", values);

            // Emitted for every removal, including ids this pack also added:
            // the entry could have come from somewhere else too, and "remove"
            // is what actually takes it out of the merged tag.
            if (!toRemove.isEmpty()) {
                JsonArray removeArray = new JsonArray();
                for (String id : toRemove) removeArray.add(id);
                obj.add("remove", removeArray);
            }

                // The tag's OWN namespace/path decide the file location:
                //   c:ingots → data/c/tags/item/ingots.json
                // 1.21 uses singular directory names (item/block/fluid/damage_type).
            ResourceLocation tag = key.tag();
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                    tag.getNamespace(),
                    "tags/" + key.registry() + "/" + tag.getPath() + ".json");

            pack.addRaw(loc, GSON.toJson(obj).getBytes(StandardCharsets.UTF_8));
            LOGGER.debug("[CustomGear] Injected {} tag: {} ({} entries, {} removals)",
                    key.registry(), tag, values.size(), toRemove.size());
            count++;
        }

        LOGGER.info("[CustomGear] Generated {} tag files", count);
    }
}