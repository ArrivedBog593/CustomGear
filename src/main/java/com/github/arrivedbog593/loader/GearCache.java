package com.github.arrivedbog593.loader;

import com.github.arrivedbog593.data.GearData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
 * Persistent cache for parsed {@link GearData} objects.
 *
 * <p>The cache lives at {@code customgear/.cache/gear_cache.json} and maps each
 * JSON file's path (relative to the gear folder) to a {@link CacheEntry} that
 * stores the file's last-modified timestamp and the already-parsed
 * {@link GearData}.  On subsequent game starts only files whose timestamp has
 * changed are re-parsed; everything else is loaded straight from the cache.</p>
 */
public class GearCache {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Gson GSON = new GsonBuilder().create();
    private static final String CACHE_DIR  = ".cache";
    private static final String CACHE_FILE = "gear_cache.json";

    // Map<relative-path-string, CacheEntry>
    private final Map<String, CacheEntry> entries;
    private final Path cacheFilePath;

    // -------------------------------------------------------------------------
    // Inner data class
    // -------------------------------------------------------------------------

    public static class CacheEntry {
        public long lastModified;
        public GearData data;

        public CacheEntry(long lastModified, GearData data) {
            this.lastModified = lastModified;
            this.data = data;
        }
    }

    // -------------------------------------------------------------------------
    // Constructor (private – use load())
    // -------------------------------------------------------------------------

    private GearCache(Path cacheFilePath, Map<String, CacheEntry> entries) {
        this.cacheFilePath = cacheFilePath;
        this.entries = entries;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Loads the cache from {@code <gearFolder>/.cache/gear_cache.json}.
     * Returns an empty cache if the file does not exist or cannot be read.
     */
    public static GearCache load(Path gearFolder) {
        Path cacheDir  = gearFolder.resolve(CACHE_DIR);
        Path cacheFile = cacheDir.resolve(CACHE_FILE);

        if (!Files.exists(cacheFile)) {
            return new GearCache(cacheFile, new HashMap<>());
        }

        try {
            String json = Files.readString(cacheFile);
            Type type = new TypeToken<Map<String, CacheEntry>>() {}.getType();
            Map<String, CacheEntry> loaded = GSON.fromJson(json, type);
            if (loaded == null) loaded = new HashMap<>();
            LOGGER.debug("[CustomGear] Cache loaded: {} entries from {}", loaded.size(), cacheFile);
            return new GearCache(cacheFile, loaded);
        } catch (Exception e) {
            LOGGER.warn("[CustomGear] Could not read cache (will re-parse all files): {}", e.getMessage());
            return new GearCache(cacheFile, new HashMap<>());
        }
    }

    /** Returns the cached entry for a relative-path key, or {@code null} if not present. */
    public CacheEntry get(String relativePathKey) {
        return entries.get(relativePathKey);
    }

    /** Adds or updates an entry. */
    public void put(String relativePathKey, long lastModified, GearData data) {
        entries.put(relativePathKey, new CacheEntry(lastModified, data));
    }

    /**
     * Removes entries whose keys are not contained in {@code currentKeys}.
     *
     * @return {@code true} if any entry was removed (cache was modified)
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

    /**
     * Persists the current cache to disk.  Creates parent directories if needed.
     */
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
