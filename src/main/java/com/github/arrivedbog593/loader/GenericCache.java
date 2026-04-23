package com.github.arrivedbog593.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Generic persistent cache for any parsed data object.
 * <p>
 * Lives at {@code <folder>/.cache/<cacheFileName>} and maps each JSON file's
 * relative path to a {@link CacheEntry} storing the last-modified timestamp
 * and the already-parsed data object. Only files whose timestamp has changed
 * are re-parsed on subsequent loads.
 *
 * @param <T> The type of data object being cached (e.g. GearData, BlockData)
 */
public class GenericCache<T> {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Gson GSON = new GsonBuilder().create();
    private static final String CACHE_DIR = ".cache";

    private final Map<String, CacheEntry<T>> entries;
    private final Path cacheFilePath;

    // -------------------------------------------------------------------------
    // Inner data class
    // -------------------------------------------------------------------------

    public static class CacheEntry<T> {
        public long lastModified;
        public T data;

        public CacheEntry(long lastModified, T data) {
            this.lastModified = lastModified;
            this.data = data;
        }
    }

    // -------------------------------------------------------------------------
    // Constructor (private – use load())
    // -------------------------------------------------------------------------

    private GenericCache(Path cacheFilePath, Map<String, CacheEntry<T>> entries) {
        this.cacheFilePath = cacheFilePath;
        this.entries = entries;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Loads the cache from {@code <folder>/.cache/<cacheFileName>}.
     * Returns an empty cache if the file does not exist or cannot be read.
     *
     * @param folder        The root folder being scanned
     * @param cacheFileName The cache file name (e.g. "gear_cache.json")
     * @param entryMapType  The Gson TypeToken for Map<String, CacheEntry<T>>
     */
    public static <T> GenericCache<T> load(Path folder, String cacheFileName, Type entryMapType) {
        Path cacheFile = folder.resolve(CACHE_DIR).resolve(cacheFileName);

        if (!Files.exists(cacheFile)) {
            return new GenericCache<>(cacheFile, new HashMap<>());
        }

        try {
            String json = Files.readString(cacheFile);
            Map<String, CacheEntry<T>> loaded = GSON.fromJson(json, entryMapType);
            if (loaded == null) loaded = new HashMap<>();
            LOGGER.debug("[CustomGear] Cache loaded: {} entries from {}", loaded.size(), cacheFile);
            return new GenericCache<>(cacheFile, loaded);
        } catch (Exception e) {
            LOGGER.warn("[CustomGear] Could not read cache, will re-parse all files: {}", e.getMessage());
            return new GenericCache<>(cacheFile, new HashMap<>());
        }
    }

    /** Returns the cached entry for a relative-path key, or {@code null} if not present. */
    public CacheEntry<T> get(String relativePathKey) {
        return entries.get(relativePathKey);
    }

    /** Adds or updates an entry. */
    public void put(String relativePathKey, long lastModified, T data) {
        entries.put(relativePathKey, new CacheEntry<>(lastModified, data));
    }

    /**
     * Removes entries whose keys are not in {@code currentKeys}.
     * @return {@code true} if any entry was removed
     */
    public boolean removeStale(Set<String> currentKeys) {
        int before = entries.size();
        entries.keySet().retainAll(currentKeys);
        int removed = before - entries.size();
        if (removed > 0) {
            LOGGER.info("[CustomGear] Cache: removed {} stale entries", removed);
        }
        return removed > 0;
    }

    /** Persists the current cache to disk. Creates parent directories if needed. */
    public void save() {
        try {
            Files.createDirectories(cacheFilePath.getParent());
            Files.writeString(cacheFilePath, GSON.toJson(entries));
            LOGGER.debug("[CustomGear] Cache saved: {} entries to {}", entries.size(), cacheFilePath);
        } catch (IOException e) {
            LOGGER.error("[CustomGear] Could not write cache: {}", e.getMessage());
        }
    }
}