package arrivedbog593.ultimatecustomgear.resources;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import arrivedbog593.ultimatecustomgear.data.FluidData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static arrivedbog593.ultimatecustomgear.resources.ModelConstants.*;

/**
 * Generates block models, blockstates, block item models and fluid bucket models.
 */
public final class BlockModelGenerator {

    private static final Logger LOGGER     = LogManager.getLogger("CustomGear");
    private static final Path   GEAR_FOLDER = Paths.get(".", "customgear");

    private BlockModelGenerator() {}

    // ── Public entry points ───────────────────────────────────────────────────

    public static void loadBlock(DynamicResourcePack pack, BlockData data) {
        LOGGER.info("[CustomGear] Loading block texture: {}", data.id);
        if (data.texture == null || data.texture.mode == null) {
            generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
            return;
        }
        switch (data.texture.mode) {
            case "reference" -> {
                String ref = data.texture.refs != null ? data.texture.refs.get("block") : null;
                if (ref != null) {
                    generateBlockWithRef(pack, data.id, ref);
                } else {
                    LOGGER.error("[CustomGear] 'refs.block' missing in reference mode: {}", data.id);
                    generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
                }
            }
            case "custom" -> {
                String path = data.texture.refs != null ? data.texture.refs.get("all") : null;
                if (path != null) {
                    Path texPath = GEAR_FOLDER.resolve(path);
                    if (Files.exists(texPath)) {
                        pack.addTexture(blockTextureLoc(data.id), texPath);
                        generateBlockModel(pack, data.id);
                        generateBlockState(pack, data.id);
                        generateBlockItemModel(pack, data.id);
                    } else {
                        LOGGER.error("[CustomGear] Block texture not found: {}", texPath);
                        generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
                    }
                } else {
                    LOGGER.error("[CustomGear] 'refs.all' missing in custom mode: {}", data.id);
                    generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
                }
            }
            default -> generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
        }
    }

    public static void generateBucketModel(DynamicResourcePack pack, FluidData data) {
        String bucketTexture;
        if (data.texture == null || data.texture.mode == null || data.texture.mode.equals("default")) {
            bucketTexture = "minecraft:item/water_bucket";
        } else if (data.texture.mode.equals("custom")) {
            bucketTexture = NAMESPACE + ":item/" + data.id + "_bucket";
        } else {
            String bucketRef = data.texture.refs != null ? data.texture.refs.get("bucket") : null;
            bucketTexture = bucketRef != null ? bucketRef : "minecraft:item/water_bucket";
        }

        String json = """
            {
              "parent": "minecraft:item/generated",
              "textures": {
                "layer0": "%s"
              }
            }
            """.formatted(bucketTexture);
        pack.addRaw(itemModelLoc(data.id + "_bucket"), json.getBytes(StandardCharsets.UTF_8));
    }

    // ── Block model helpers ───────────────────────────────────────────────────

    public static void generateBlockModel(DynamicResourcePack pack, String blockId) {
        String json = """
            {
              "parent": "minecraft:block/cube_all",
              "textures": {
                "all": "%s:block/%s"
              }
            }
            """.formatted(NAMESPACE, blockId);
        pack.addRaw(blockModelLoc(blockId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateBlockState(DynamicResourcePack pack, String blockId) {
        String json = """
            {
              "variants": {
                "": { "model": "%s:block/%s" }
              }
            }
            """.formatted(NAMESPACE, blockId);
        pack.addRaw(blockStateLoc(blockId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateBlockItemModel(DynamicResourcePack pack, String blockId) {
        String json = """
            {
              "parent": "%s:block/%s"
            }
            """.formatted(NAMESPACE, blockId);
        pack.addRaw(itemModelLoc(blockId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateBlockWithRef(DynamicResourcePack pack, String blockId, String ref) {
        String blockJson = """
            {
              "parent": "minecraft:block/cube_all",
              "textures": {
                "all": "%s"
              }
            }
            """.formatted(ref);
        pack.addRaw(blockModelLoc(blockId), blockJson.getBytes(StandardCharsets.UTF_8));
        generateBlockState(pack, blockId);
        generateBlockItemModel(pack, blockId);
    }
}