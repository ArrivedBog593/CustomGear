package com.github.arrivedbog593.loader;

import com.github.arrivedbog593.data.AdvancementData;
import com.github.arrivedbog593.data.BlockData;
import com.github.arrivedbog593.data.FluidData;
import com.github.arrivedbog593.data.ItemData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
    private static final Gson GSON = new GsonBuilder().create();

    // Gear types handled by GearParser — ignored here
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

        if (!Files.exists(folder)) {
            try {
                Files.createDirectories(folder);
                LOGGER.info("[CustomGear] Folder created: {}", folder);
            } catch (IOException e) {
                LOGGER.error("[CustomGear] Could not create folder: {}", e.getMessage());
                return result;
            }
        }

        // Separate caches per data type
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

        Set<String> currentKeys = new HashSet<>();
        AtomicBoolean cacheModified = new AtomicBoolean(false);

        try {
            Files.walk(folder)
                    .filter(p -> p.toString().endsWith(".json")
                            && !folder.relativize(p).startsWith(".cache"))
                    .forEach(path -> {
                        String relKey = folder.relativize(path).toString();
                        currentKeys.add(relKey);

                        try {
                            String json = Files.readString(path);
                            String type = extractType(json);

                            if (type == null || GEAR_TYPES.contains(type)) return;

                            FileTime ft = Files.getLastModifiedTime(path);
                            long lastModified = ft.toMillis();

                            switch (type) {
                                case "block" -> {
                                    GenericCache.CacheEntry<BlockData> cached = blockCache.get(relKey);
                                    if (cached != null && cached.lastModified == lastModified && cached.data != null) {
                                        result.blocks.add(cached.data);
                                        LOGGER.debug("[CustomGear] Block cache hit: {}", relKey);
                                    } else {
                                        BlockData data = GSON.fromJson(json, BlockData.class);
                                        if (validateBlock(data, path)) {
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
                                        result.fluids.add(cached.data);
                                        LOGGER.debug("[CustomGear] Fluid cache hit: {}", relKey);
                                    } else {
                                        FluidData data = GSON.fromJson(json, FluidData.class);
                                        if (validateFluid(data, path)) {
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
                                        result.items.add(cached.data);
                                        LOGGER.debug("[CustomGear] Item cache hit: {}", relKey);
                                    } else {
                                        ItemData data = GSON.fromJson(json, ItemData.class);
                                        if (validateItem(data, path)) {
                                            result.items.add(data);
                                            itemCache.put(relKey, lastModified, data);
                                            cacheModified.set(true);
                                            LOGGER.info("[CustomGear] Item loaded: {}", data.id);
                                        }
                                    }
                                }
                                case "advancement" -> {
                                    // Advancements are not cached — incomplete feature
                                    AdvancementData data = GSON.fromJson(json, AdvancementData.class);
                                    if (validateAdvancement(data, path)) {
                                        result.advancements.add(data);
                                        LOGGER.info("[CustomGear] Advancement loaded: {}", data.id);
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

        // Remove stale cache entries
        if (blockCache.removeStale(currentKeys)) cacheModified.set(true);
        if (fluidCache.removeStale(currentKeys)) cacheModified.set(true);
        if (itemCache.removeStale(currentKeys)) cacheModified.set(true);

        if (cacheModified.get()) {
            blockCache.save();
            fluidCache.save();
            itemCache.save();
        }

        LOGGER.info("[CustomGear] Loaded: {} blocks, {} fluids, {} items, {} advancements",
                result.blocks.size(), result.fluids.size(),
                result.items.size(), result.advancements.size());

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
        // texture is optional — defaults to stone if null
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
        // Textures are only required in custom and reference modes
        if (data.texture != null && data.texture.mode != null && !data.texture.mode.equals("default")) {
            if (data.texture.refs == null || data.texture.refs.get("still") == null || data.texture.refs.get("flowing") == null) {
                LOGGER.warn("[CustomGear] Fluid '{}' missing textures in {} mode: {}", data.id, data.texture.mode, path.getFileName());
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
        // texture is optional — defaults to paper if null
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

    // ── Utilities ────────────────────────────────────────────────────────────

    /** Extracts the "type" field from a JSON string without fully parsing it. */
    private static String extractType(String json) {
        JsonElement root = JsonParser.parseString(json);
        if (!root.isJsonObject()) return null;
        JsonElement typeEl = root.getAsJsonObject().get("type");
        if (typeEl == null || !typeEl.isJsonPrimitive()) return null;
        return typeEl.getAsString();
    }
}