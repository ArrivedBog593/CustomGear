package arrivedbog593.ultimatecustomgear.loader;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import arrivedbog593.ultimatecustomgear.data.FluidData;
import arrivedbog593.ultimatecustomgear.data.ItemData;

import java.util.List;

/**
 * Feeds the TagFileBuilder from the user-declared "tags" field on items, food,
 * blocks and fluids. This is the inverse of recipe tag ingredients: recipes
 * CONSUME tags (#minecraft:planks), this makes your content BELONG to tags so
 * other mods' recipes (and your own) accept it via #tag.
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
 * Everything added here is required:true — it is content this mod registers,
 * so it always exists. Foreign content (tag_patch) is the case that needs
 * required:false.
 * <p>
 * This class no longer writes to the pack. It only fills the shared builder;
 * the caller emits once, after every tag source has had its turn. See
 * TagFileBuilder for why.
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

    public static void loadAll(TagFileBuilder tags, List<ItemData> items,
                               List<BlockData> blocks, List<FluidData> fluids) {

        // ── Items and food → item registry ──────────────────────────────────
        for (ItemData data : items) {
            addTags(tags, data.tags, "item", "customgear:" + data.id,
                    "item '" + data.id + "'");
        }

        // ── Blocks → block AND item registries (block + its BlockItem) ──────
        for (BlockData data : blocks) {
            String id = "customgear:" + data.id;
            addTags(tags, data.tags, "block", id, "block '" + data.id + "'");
            addTags(tags, data.tags, "item",  id, "block '" + data.id + "'");
        }

        // ── Fluids → fluid registry, and the bucket in the item registry ────
        for (FluidData data : fluids) {
            addTags(tags, data.tags, "fluid", "customgear:" + data.id,
                    "fluid '" + data.id + "'");
            addTags(tags, data.tags, "item", "customgear:" + data.id + "_bucket",
                    "fluid '" + data.id + "' (bucket)");
        }
    }

    /**
     * Adds every tag string declared by one content id. Malformed tags are
     * skipped with a message naming the owner — the builder handles the '#'
     * tolerance and the parse.
     */
    private static void addTags(TagFileBuilder tags, List<String> declared,
                                String registry, String contentId, String owner) {
        if (declared == null || declared.isEmpty()) return;

        for (String raw : declared) {
            tags.addParsed(registry, raw, contentId, true, owner);
        }
    }
}