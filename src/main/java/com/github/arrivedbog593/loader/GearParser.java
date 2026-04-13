package com.github.arrivedbog593.loader;

import com.github.arrivedbog593.data.GearData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GearParser {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");
    private static final Gson GSON = new GsonBuilder().create();

    public static List<GearData> loadAll(Path folder) {
        List<GearData> result = new ArrayList<>();

        if (!Files.exists(folder)) {
            try {
                Files.createDirectories(folder);
                LOGGER.info("[CustomGear] Carpeta creada: {}", folder);
            } catch (IOException e) {
                LOGGER.error("[CustomGear] No se pudo crear la carpeta: {}", e.getMessage());
                return result;
            }
        }

        try (var stream = Files.list(folder)) {
            stream.filter(p -> p.toString().endsWith(".json"))
                    .forEach(path -> {
                        try {
                            String json = Files.readString(path);
                            GearData data = GSON.fromJson(json, GearData.class);
                            if (validate(data, path)) {
                                result.add(data);
                                LOGGER.info("[CustomGear] Cargado: {}", data.id);
                            }
                        } catch (Exception e) {
                            LOGGER.error("[CustomGear] Error leyendo {}: {}", path.getFileName(), e.getMessage());
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("[CustomGear] Error escaneando carpeta: {}", e.getMessage());
        }

        return result;
    }

    private static boolean validate(GearData data, Path path) {
        if (data.id == null || data.id.isBlank()) {
            LOGGER.warn("[CustomGear] JSON sin 'id': {}", path.getFileName());
            return false;
        }
        if (data.type == null || data.type.isBlank()) {
            LOGGER.warn("[CustomGear] JSON sin 'type': {}", path.getFileName());
            return false;
        }
        return true;
    }
}