package com.github.arrivedbog593.loader;

import com.github.arrivedbog593.data.GearData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

public class GearParser {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Gson GSON = new GsonBuilder().create();

    public static List<GearData> loadAll(Path folder) {
        List<GearData> result = new ArrayList<>();

        if (!Files.exists(folder)) {
            try {
                Files.createDirectories(folder);
                LOGGER.info("[CustomGear] Folder created: {}", folder);
            } catch (IOException e) {
                LOGGER.error("[CustomGear] Could not create folder: {}", e.getMessage());
                return result;
            }
        }

        GearCache cache = GearCache.load(folder);
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
                            FileTime ft = Files.getLastModifiedTime(path);
                            long lastModified = ft.toMillis();
                            GearCache.CacheEntry cached = cache.get(relKey);

                            if (cached != null && cached.lastModified == lastModified
                                    && cached.data != null) {
                                // File unchanged – use cached GearData
                                result.add(cached.data);
                                LOGGER.debug("[CustomGear] Cache hit: {}", relKey);
                            } else {
                                // New or modified file – parse and (re)validate
                                String json = Files.readString(path);
                                GearData data = GSON.fromJson(json, GearData.class);
                                if (validate(data, path)) {
                                    result.add(data);
                                    cache.put(relKey, lastModified, data);
                                    cacheModified.set(true);
                                    LOGGER.info("[CustomGear] Loaded: {} ({})", data.id, relKey);
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("[CustomGear] Error reading {}: {}",
                                    path.getFileName(), e.getMessage());
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("[CustomGear] Error scanning folder: {}", e.getMessage());
        }

        // Remove cache entries for files that were deleted
        if (cache.removeStale(currentKeys)) {
            cacheModified.set(true);
        }

        if (cacheModified.get()) {
            cache.save();
        }

        return result;
    }

    private static boolean validate(GearData data, Path path) {
        if (data.id == null || data.id.isBlank()) {
            LOGGER.warn("[CustomGear] JSON missing 'id': {}", path.getFileName());
            return false;
        }
        if (!data.id.matches("^[a-z][a-z0-9_]{1,63}$")) {
            LOGGER.warn("[CustomGear] ID does not match format: {}", data.id);
            return false;
        }
        if (data.type == null || data.type.isBlank()) {
            LOGGER.warn("[CustomGear] JSON missing 'type': {}", path.getFileName());
            return false;
        }

        if (data.pieceEffects != null) {
            for (Map.Entry<String, List<GearData.EffectData>> entry : data.pieceEffects.entrySet()) {
                if (entry.getValue() != null) {
                    for (GearData.EffectData effectData : entry.getValue()) {
                        if (!validateEffect(effectData.effect)) {
                            LOGGER.warn("[CustomGear] Invalid effect in pieceEffects: {} ({})",
                                    effectData.effect, data.id);
                            return false;
                        }
                    }
                }
            }
        }

        if (data.heldEffects != null) {
            for (GearData.EffectData effectData : data.heldEffects) {
                if (!validateEffect(effectData.effect)) {
                    LOGGER.warn("[CustomGear] Invalid effect in heldEffects: {} ({})",
                            effectData.effect, data.id);
                    return false;
                }
            }
        }

        if (data.setBonus != null && data.setBonus.effects != null) {
            for (GearData.EffectData effectData : data.setBonus.effects) {
                if (!validateEffect(effectData.effect)) {
                    LOGGER.warn("[CustomGear] Invalid effect in setBonus: {} ({})",
                            effectData.effect, data.id);
                    return false;
                }
            }
        }

        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean validateEffect(String effectId) {
        try {
            ResourceLocation rl = ResourceLocation.parse(effectId);
            return BuiltInRegistries.MOB_EFFECT.getHolder(rl).isPresent();
        } catch (Exception e) {
            return false;
        }
    }
}