package arrivedbog593.ultimatecustomgear.loader;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import arrivedbog593.ultimatecustomgear.data.FluidData;
import arrivedbog593.ultimatecustomgear.data.ItemData;
import arrivedbog593.ultimatecustomgear.resources.DynamicResourcePack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates tag JSON files from the user-declared "tags" field on items, food,
 * blocks and fluids, and injects them into the DynamicResourcePack as
 * SERVER_DATA. This is the inverse of recipe tag ingredients: recipes CONSUME
 * tags (#minecraft:planks), this makes your content BELONG to tags so other
 * mods' recipes (and your own) accept it via #tag.
 * <p>
 * A tag is not a predefined list — it is simply the sum of everything that
 * declares it, so users can reference vanilla tags (minecraft:planks),
 * convention tags (c:ingots, c:ingots/ruby) or invent their own
 * (customgear:magic_gems). No '#' prefix in the "tags" field: there you
 * DECLARE membership; the '#' is only for CONSUMING a tag in a recipe.
 * <p>
 * Routing by content type — a tag entry lands in the registry/registries the
 * content actually occupies:
 * <ul>
 *   <li>item / food → item registry only  → tags/item/</li>
 *   <li>block       → BOTH block and item  → tags/block/ AND tags/item/
 *       (so both the block and its BlockItem count; this is what users
 *        expect — "my wood block" should satisfy #minecraft:planks in a
 *        recipe, which consumes the item form)</li>
 *   <li>fluid       → fluid, plus its bucket in the item registry
 *                     → tags/fluid/ AND tags/item/ (id + "_bucket")</li>
 * </ul>
 * Each generated tag file uses "replace": false, so it MERGES with the
 * vanilla/other-mod tag of the same name instead of overwriting it.
 * <p>
 * Gear (armor/tool/weapon sets and individual weapons/tools) is intentionally
 * NOT covered in this version: set IDs are derived per piece (myset_sword),
 * which needs a per-piece tag schema. Planned for a later version.
 * <p>
 * IMPORTANT: like RecipeLoader / BlockTagLoader / BlockLootLoader, this must
 * be called BOTH at startup and in /customgear reload — the reload clears the
 * dynamic pack, and anything not regenerated is wiped on the next reload.
 */
public class ItemTagLoader {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Gson   GSON   = new GsonBuilder().setPrettyPrinting().create();

    /**
     * A tag file to build, keyed by "registry + full tag id".
     * registry is "item" | "block" | "fluid"; tag is the parsed ResourceLocation.
     */
    private record TagKey(String registry, ResourceLocation tag) {}

    public static void loadAll(DynamicResourcePack pack, List<ItemData> items,
                               List<BlockData> blocks, List<FluidData> fluids) {
        // LinkedHashMap → deterministic file order (hash/debug friendliness)
        Map<TagKey, List<String>> tagFiles = new LinkedHashMap<>();

        // ── Items and food → item registry ──────────────────────────────────
        for (ItemData data : items) {
            addTags(tagFiles, data.tags, "item", "customgear:" + data.id,
                    "item '" + data.id + "'");
        }

        // ── Blocks → block AND item registries (block + its BlockItem) ──────
        for (BlockData data : blocks) {
            String id = "customgear:" + data.id;
            addTags(tagFiles, data.tags, "block", id, "block '" + data.id + "'");
            addTags(tagFiles, data.tags, "item",  id, "block '" + data.id + "'");
        }

        // ── Fluids → fluid registry, and the bucket in the item registry ────
        for (FluidData data : fluids) {
            addTags(tagFiles, data.tags, "fluid", "customgear:" + data.id,
                    "fluid '" + data.id + "'");
            addTags(tagFiles, data.tags, "item", "customgear:" + data.id + "_bucket",
                    "fluid '" + data.id + "' (bucket)");
        }

        // ── Emit one JSON per (registry, tag) ───────────────────────────────
        int count = 0;
        for (Map.Entry<TagKey, List<String>> entry : tagFiles.entrySet()) {
            injectTag(pack, entry.getKey(), entry.getValue());
            count++;
        }
        LOGGER.info("[CustomGear] Generated {} item/block/fluid tag files", count);
    }

    /**
     * Parses and adds each tag string for one content id into the right file
     * bucket. Malformed tags are skipped with a clear message naming the owner.
     */
    private static void addTags(Map<TagKey, List<String>> tagFiles, List<String> tags,
                                String registry, String contentId, String owner) {
        if (tags == null || tags.isEmpty()) return;

        for (String raw : tags) {
            if (raw == null || raw.isBlank()) continue;

            // Tolerate a stray '#': it belongs on the CONSUMING side, but a
            // user pasting "#c:ingots" here shouldn't silently fail.
            String cleaned = raw.startsWith("#") ? raw.substring(1) : raw;

            ResourceLocation tagRl = ResourceLocation.tryParse(cleaned);
            if (tagRl == null) {
                LOGGER.warn("[CustomGear] {}: malformed tag '{}' — skipped. Expected "
                        + "'namespace:path' like 'c:ingots' or 'minecraft:planks' "
                        + "(lowercase letters, numbers, '_', '-', '.', '/')", owner, raw);
                continue;
            }

            tagFiles.computeIfAbsent(new TagKey(registry, tagRl), k -> new ArrayList<>())
                    .add(contentId);
        }
    }

    private static void injectTag(DynamicResourcePack pack, TagKey key, List<String> contentIds) {
        JsonObject obj = new JsonObject();
        obj.addProperty("replace", false); // merge with vanilla/other-mod tags

        JsonArray values = new JsonArray();
        for (String id : contentIds) values.add(id);
        obj.add("values", values);

        // The tag's OWN namespace/path decide the file location:
        //   c:ingots  → data/c/tags/item/ingots.json
        // 1.21 uses singular directory names (item/block/fluid).
        ResourceLocation tag = key.tag();
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                tag.getNamespace(),
                "tags/" + key.registry() + "/" + tag.getPath() + ".json");

        pack.addRaw(loc, GSON.toJson(obj).getBytes(StandardCharsets.UTF_8));
        LOGGER.debug("[CustomGear] Injected {} tag: {} ({} entries)",
                key.registry(), tag, contentIds.size());
    }
}