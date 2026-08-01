package arrivedbog593.ultimatecustomgear.loader;

import arrivedbog593.ultimatecustomgear.data.*;
import arrivedbog593.ultimatecustomgear.util.ParserUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class UniversalParser {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(
                    new com.google.gson.reflect.TypeToken<List<RecipeData>>(){}.getType(),
                    new RecipeListDeserializer()
            )
            .create();

    private static final Set<String> GEAR_TYPES = Set.of(
            "armor_set", "tool_set", "weapon_set",
            "sword", "bow", "crossbow", "shield",
            "pickaxe", "axe", "shovel", "hoe"
    );

    public static class LoadResult {
        public final List<BlockData>       blocks       = new ArrayList<>();
        public final List<FluidData>       fluids       = new ArrayList<>();
        public final List<ItemData>        items        = new ArrayList<>();
        public final List<AdvancementData> advancements = new ArrayList<>();
        public final List<TagPatchData>    tagPatches   = new ArrayList<>();
    }

    /**
     * Loads blocks/fluids/items from every content root: the loose folder
     * plus every zip mounted from packs/. Cache keys are prefixed with the
     * root label so zip entries never collide with loose files.
     */
    public static LoadResult loadAll(ContentRoots contentRoots) {
        LoadResult result = new LoadResult();

        Path configFolder = contentRoots.configFolder();
        if (!ParserUtils.ensureFolderExists(configFolder)) return result;

        GenericCache<BlockData> blockCache = GenericCache.load(
                configFolder, "block_cache.json",
                new TypeToken<Map<String, GenericCache.CacheEntry<BlockData>>>() {}.getType()
        );
        GenericCache<FluidData> fluidCache = GenericCache.load(
                configFolder, "fluid_cache.json",
                new TypeToken<Map<String, GenericCache.CacheEntry<FluidData>>>() {}.getType()
        );
        GenericCache<ItemData> itemCache = GenericCache.load(
                configFolder, "item_cache.json",
                new TypeToken<Map<String, GenericCache.CacheEntry<ItemData>>>() {}.getType()
        );

        Set<String> seenBlocks = new HashSet<>();
        Set<String> seenFluids = new HashSet<>();
        Set<String> seenItems  = new HashSet<>();

        Set<String>   currentKeys   = new HashSet<>();
        AtomicBoolean cacheModified = new AtomicBoolean(false);

        for (ContentRoots.Root root : contentRoots.roots()) {
            // Symlink escapes are only possible on the real filesystem;
            // zip virtual filesystems cannot reference paths outside the zip.
            Path canonicalRoot = root.isLoose() ? ParserUtils.resolveCanonical(root.path()) : null;
            if (root.isLoose() && canonicalRoot == null) continue;

            try {
                Files.walk(root.path())
                        .filter(p -> p.toString().endsWith(".json")
                                && !root.path().relativize(p).startsWith(".cache")
                                && !root.path().relativize(p).startsWith("packs"))
                        .forEach(path -> {
                            if (root.isLoose() && !ParserUtils.isSafeChild(path, canonicalRoot)) {
                                LOGGER.warn("[CustomGear] Skipping file outside customgear/ dir: {}", path);
                                return;
                            }

                            String relKey = root.label() + root.path().relativize(path);
                            currentKeys.add(relKey);

                            try {
                                String json = Files.readString(path);
                                String type = ParserUtils.extractType(json);

                                if (type == null || GEAR_TYPES.contains(type)) return;

                                FileTime ft           = Files.getLastModifiedTime(path);
                                long     lastModified = ft.toMillis();

                                switch (type) {
                                    case "block" -> {
                                        GenericCache.CacheEntry<BlockData> cached = blockCache.get(relKey);
                                        if (cached != null && cached.lastModified == lastModified && cached.data != null) {
                                            if (!seenBlocks.add(cached.data.id)) {
                                                LOGGER.error("[CustomGear] Duplicate block ID '{}' — skipping: {}", cached.data.id, relKey);
                                                return;
                                            }
                                            result.blocks.add(cached.data);
                                            LOGGER.debug("[CustomGear] Block cache hit: {}", relKey);
                                        } else {
                                            BlockData data = GSON.fromJson(json, BlockData.class);
                                            if (validateBlock(data, path)) {
                                                if (!seenBlocks.add(data.id)) {
                                                    LOGGER.error("[CustomGear] Duplicate block ID '{}' — skipping: {}", data.id, relKey);
                                                    return;
                                                }
                                                result.blocks.add(data);
                                                blockCache.put(relKey, lastModified, data);
                                                cacheModified.set(true);
                                                LOGGER.info("[CustomGear] Block loaded: {}", data.id);
                                            }
                                        }
                                    }
                                    case "fluid" -> {
                                        GenericCache.CacheEntry<FluidData> cached = fluidCache.get(relKey);
                                        if (cached != null && cached.lastModified == lastModified && cached.data != null) {
                                            if (!seenFluids.add(cached.data.id)) {
                                                LOGGER.error("[CustomGear] Duplicate fluid ID '{}' — skipping: {}", cached.data.id, relKey);
                                                return;
                                            }
                                            result.fluids.add(cached.data);
                                            LOGGER.debug("[CustomGear] Fluid cache hit: {}", relKey);
                                        } else {
                                            FluidData data = GSON.fromJson(json, FluidData.class);
                                            if (validateFluid(data, path)) {
                                                if (!seenFluids.add(data.id)) {
                                                    LOGGER.error("[CustomGear] Duplicate fluid ID '{}' — skipping: {}", data.id, relKey);
                                                    return;
                                                }
                                                result.fluids.add(data);
                                                fluidCache.put(relKey, lastModified, data);
                                                cacheModified.set(true);
                                                LOGGER.info("[CustomGear] Fluid loaded: {}", data.id);
                                            }
                                        }
                                    }
                                    case "item", "food" -> {
                                        GenericCache.CacheEntry<ItemData> cached = itemCache.get(relKey);
                                        if (cached != null && cached.lastModified == lastModified && cached.data != null) {
                                            if (!seenItems.add(cached.data.id)) {
                                                LOGGER.error("[CustomGear] Duplicate item ID '{}' — skipping: {}", cached.data.id, relKey);
                                                return;
                                            }
                                            result.items.add(cached.data);
                                            LOGGER.debug("[CustomGear] Item cache hit: {}", relKey);
                                        } else {
                                            ItemData data = GSON.fromJson(json, ItemData.class);
                                            if (validateItem(data, path)) {
                                                if (!seenItems.add(data.id)) {
                                                    LOGGER.error("[CustomGear] Duplicate item ID '{}' — skipping: {}", data.id, relKey);
                                                    return;
                                                }
                                                result.items.add(data);
                                                itemCache.put(relKey, lastModified, data);
                                                cacheModified.set(true);
                                                LOGGER.info("[CustomGear] Item loaded: {}", data.id);
                                            }
                                        }
                                    }
                                    case "advancement" -> {
                                        AdvancementData data = GSON.fromJson(json, AdvancementData.class);
                                        if (validateAdvancement(data, path)) {
                                            result.advancements.add(data);
                                            LOGGER.debug("[CustomGear] Advancement parsed (not yet active): {}", data.id);
                                        }
                                    }
                                    case "tag_patch" -> {
                                        TagPatchData data = GSON.fromJson(json, TagPatchData.class);
                                        if (validateTagPatch(data, path)) {
                                            result.tagPatches.add(data);
                                            LOGGER.info("[CustomGear] Tag patch loaded: {} -> {}",
                                                    data.registry, data.tag);
                                        }
                                    }
                                    default -> LOGGER.warn("[CustomGear] Unknown type '{}' in: {}", type, path.getFileName());
                                }

                            } catch (Exception e) {
                                LOGGER.error("[CustomGear] Error reading {}: {}", path.getFileName(), e.getMessage());
                            }
                        });
            } catch (IOException e) {
                LOGGER.error("[CustomGear] Error scanning {}: {}",
                        root.isLoose() ? "folder" : root.label(), e.getMessage());
            }
        }

        if (blockCache.removeStale(currentKeys)) cacheModified.set(true);
        if (fluidCache.removeStale(currentKeys)) cacheModified.set(true);
        if (itemCache.removeStale(currentKeys))  cacheModified.set(true);

        if (cacheModified.get()) {
            blockCache.save();
            fluidCache.save();
            itemCache.save();
        }

        LOGGER.info("[CustomGear] Loaded: {} blocks, {} fluids, {} items",
                result.blocks.size(), result.fluids.size(), result.items.size());

        return result;
    }

    // ── Validations ──────────────────────────────────────────────────────────

    private static boolean validateBlock(BlockData data, Path path) {
        if (data.id == null || data.id.isBlank()) {
            LOGGER.warn("[CustomGear] Block missing 'id': {}", path.getFileName());
            return false;
        }
        if (!data.id.matches("^[a-z][a-z0-9_]{1,63}$")) {
            LOGGER.warn("[CustomGear] Block ID does not match format: {}", data.id);
            return false;
        }
        return true;
    }

    private static boolean validateFluid(FluidData data, Path path) {
        if (data.id == null || data.id.isBlank()) {
            LOGGER.warn("[CustomGear] Fluid missing 'id': {}", path.getFileName());
            return false;
        }
        if (!data.id.matches("^[a-z][a-z0-9_]{1,63}$")) {
            LOGGER.warn("[CustomGear] Fluid ID does not match format: {}", data.id);
            return false;
        }
        if (data.texture != null && data.texture.mode != null && !data.texture.mode.equals("default")) {
            if (data.texture.refs == null
                    || data.texture.refs.get("still") == null
                    || data.texture.refs.get("flowing") == null) {
                LOGGER.warn("[CustomGear] Fluid '{}' missing textures in {} mode: {}",
                        data.id, data.texture.mode, path.getFileName());
                return false;
            }
        }
        return true;
    }

    private static boolean validateItem(ItemData data, Path path) {
        if (data.id == null || data.id.isBlank()) {
            LOGGER.warn("[CustomGear] Item missing 'id': {}", path.getFileName());
            return false;
        }
        if (!data.id.matches("^[a-z][a-z0-9_]{1,63}$")) {
            LOGGER.warn("[CustomGear] Item ID does not match format: {}", data.id);
            return false;
        }
        if (data.mobDrops != null) {
            if (data.mobDrops.entities == null || data.mobDrops.entities.isEmpty()) {
                LOGGER.warn("[CustomGear] Item '{}': mob_drops has no 'entities' — the drop is "
                        + "DISABLED. Use \"entities\": [\"all\"] for every mob, or list specific "
                        + "ids/#tags/mod:* wildcards", data.id);
                // Not a hard error: the item is still valid, its drop is just off.
            }
            if (data.mobDrops.chance < 0 || data.mobDrops.chance > 1) {
                LOGGER.warn("[CustomGear] Item '{}': mob_drops.chance must be 0.0-1.0 (got {})",
                        data.id, data.mobDrops.chance);
                return false;
            }
            if (data.mobDrops.min < 0) {
                LOGGER.warn("[CustomGear] Item '{}': mob_drops.min cannot be negative (got {})",
                        data.id, data.mobDrops.min);
                return false;
            }
            if (data.mobDrops.max < data.mobDrops.min) {
                LOGGER.warn("[CustomGear] Item '{}': mob_drops.max ({}) is lower than min ({})",
                        data.id, data.mobDrops.max, data.mobDrops.min);
                return false;
            }
            if (data.mobDrops.min == 0 && data.mobDrops.max == 0) {
                LOGGER.warn("[CustomGear] Item '{}': mob_drops min and max are both 0 — the drop "
                        + "will never produce anything", data.id);
            }
            if (data.mobDrops.requiresPlayerKill == null) {
                LOGGER.warn("[CustomGear] Item '{}': mob_drops.requires_player_kill is not "
                                + "declared. The default CHANGED to false in 1.6.0 for vanilla parity — "
                                + "mobs killed by the environment now drop this item, so automated farms "
                                + "can produce it. Declare it explicitly to silence this warning.",
                        data.id);
            }
            String mode = data.mobDrops.lootingMode;
            if (mode != null && !mode.equals("count") && !mode.equals("chance")
                    && !mode.equals("none")) {
                LOGGER.warn("[CustomGear] Item '{}': unknown looting_mode '{}' — expected "
                                + "'count', 'chance' or 'none'. Falling back to 'count'.",
                        data.id, mode);
            }
            if (!data.mobDrops.isLootingChance() && data.mobDrops.lootingChanceBonus != 0.01) {
                LOGGER.warn("[CustomGear] Item '{}': looting_chance_bonus only applies with "
                        + "looting_mode 'chance' — ignored.", data.id);
            }
            if (data.mobDrops.isLootingChance() && data.mobDrops.max <= 0) {
                LOGGER.warn("[CustomGear] Item '{}': looting_mode 'chance' with max 0 can never "
                        + "drop anything — 'chance' mode raises the probability but leaves the "
                        + "amount alone, so the roll still comes up empty. Use 'count' if you "
                        + "want Looting to create items from a 0 base.", data.id);
            }
        }
        return true;
    }

    private static boolean validateAdvancement(AdvancementData data, Path path) {
        if (data.id == null || data.id.isBlank()) {
            LOGGER.warn("[CustomGear] Advancement missing 'id': {}", path.getFileName());
            return false;
        }
        if (data.targetId == null || data.targetId.isBlank()) {
            LOGGER.warn("[CustomGear] Advancement '{}' missing 'targetId': {}", data.id, path.getFileName());
            return false;
        }
        return true;
    }

    private static boolean validateTagPatch(TagPatchData data, Path path) {
        if (data.tag == null || data.tag.isBlank()) {
            LOGGER.warn("[CustomGear] tag_patch missing 'tag': {}", path.getFileName());
            return false;
        }
        if (data.normalizedRegistry() == null) {
            LOGGER.warn("[CustomGear] tag_patch '{}' missing or invalid 'registry': {}",
                    data.tag, path.getFileName());
            return false;
        }
        boolean hasValues = data.values != null && !data.values.isEmpty();
        boolean hasRemove = data.remove != null && !data.remove.isEmpty();
        if (!hasValues && !hasRemove) {
            LOGGER.warn("[CustomGear] tag_patch '{}' has neither 'values' nor 'remove' — "
                    + "nothing to do: {}", data.tag, path.getFileName());
            return false;
        }
        return true;
    }


}