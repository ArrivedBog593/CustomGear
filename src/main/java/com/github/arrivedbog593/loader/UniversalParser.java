package com.github.arrivedbog593.loader;

import com.github.arrivedbog593.data.*;
import com.github.arrivedbog593.util.ParserUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    }

    public static LoadResult loadAll(Path folder) {
        LoadResult result = new LoadResult();

        if (!ParserUtils.ensureFolderExists(folder)) return result;

        Path canonicalFolder = ParserUtils.resolveCanonical(folder);
        if (canonicalFolder == null) return result;

        GenericCache<BlockData> blockCache = GenericCache.load(
                folder, "block_cache.json",
                new TypeToken<Map<String, GenericCache.CacheEntry<BlockData>>>() {}.getType()
        );
        GenericCache<FluidData> fluidCache = GenericCache.load(
                folder, "fluid_cache.json",
                new TypeToken<Map<String, GenericCache.CacheEntry<FluidData>>>() {}.getType()
        );
        GenericCache<ItemData> itemCache = GenericCache.load(
                folder, "item_cache.json",
                new TypeToken<Map<String, GenericCache.CacheEntry<ItemData>>>() {}.getType()
        );

        Set<String> seenBlocks = new HashSet<>();
        Set<String> seenFluids = new HashSet<>();
        Set<String> seenItems  = new HashSet<>();

        Set<String>   currentKeys   = new HashSet<>();
        AtomicBoolean cacheModified = new AtomicBoolean(false);

        try {
            Files.walk(folder)
                    .filter(p -> p.toString().endsWith(".json")
                            && !folder.relativize(p).startsWith(".cache"))
                    .forEach(path -> {
                        if (!ParserUtils.isSafeChild(path, canonicalFolder)) {
                            LOGGER.warn("[CustomGear] Skipping file outside customgear/ dir: {}", path);
                            return;
                        }

                        String relKey = folder.relativize(path).toString();
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
                                case "item" -> {
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
                                default -> LOGGER.warn("[CustomGear] Unknown type '{}' in: {}", type, path.getFileName());
                            }

                        } catch (Exception e) {
                            LOGGER.error("[CustomGear] Error reading {}: {}", path.getFileName(), e.getMessage());
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("[CustomGear] Error scanning folder: {}", e.getMessage());
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

}