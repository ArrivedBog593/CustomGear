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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        try {
            Files.walk(folder)
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(path -> {
                        try {
                            String json = Files.readString(path);
                            GearData data = GSON.fromJson(json, GearData.class);
                            if (validate(data, path)) {
                                result.add(data);
                                LOGGER.info("[CustomGear] Loaded: {} ({})", data.id,
                                        folder.relativize(path));
                            }
                        } catch (Exception e) {
                            LOGGER.error("[CustomGear] Error reading {}: {}",
                                    path.getFileName(), e.getMessage());
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("[CustomGear] Error scanning folder: {}", e.getMessage());
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