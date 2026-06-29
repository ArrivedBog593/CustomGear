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
 * <p>
 * Supports three block modes:
 *  1. Simple (cube_all)     — same texture on all faces
 *  2. Per-face (cube)       — different texture per face via "faces" field
 *  3. Directional (cube)    — rotates when placed, front face defined by "faces.north"
 */
public final class BlockModelGenerator {

    private static final Logger LOGGER      = LogManager.getLogger("CustomGear");
    private static final Path   GEAR_FOLDER = Paths.get(".", "ultimatecustomgear");

    private BlockModelGenerator() {}

    // ── Public entry points ───────────────────────────────────────────────────

    public static void loadBlock(DynamicResourcePack pack, BlockData data) {
        LOGGER.info("[CustomGear] Loading block: {}", data.id);

        // If faces are defined, use per-face or directional model
        if (data.texture != null && data.texture.faces != null) {
            if ("custom".equals(data.texture.mode)) {
                loadCustomFacesBlock(pack, data);
            } else {
                // reference mode or no mode — use resource locations directly
                loadReferenceFacesBlock(pack, data);
            }
            return;
        }

        // No faces defined — simple cube_all
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

    // ── Per-face blocks (custom PNG files) ────────────────────────────────────

    private static void loadCustomFacesBlock(DynamicResourcePack pack, BlockData data) {
        BlockData.BlockFaces f = data.texture.faces;
        String id = data.id;

        // Resolve each face — individual face overrides "side" shortcut
        String top    = resolveFaceCustom(pack, id, "top",    f.top,    null);
        String bottom = resolveFaceCustom(pack, id, "bottom", f.bottom, null);
        String north  = resolveFaceCustom(pack, id, "north",  f.north,  f.side);
        String south  = resolveFaceCustom(pack, id, "south",  f.south,  f.side);
        String east   = resolveFaceCustom(pack, id, "east",   f.east,   f.side);
        String west   = resolveFaceCustom(pack, id, "west",   f.west,   f.side);

        // Fall back to DEFAULT_BLOCK for any face not defined
        if (top    == null) top    = DEFAULT_BLOCK;
        if (bottom == null) bottom = DEFAULT_BLOCK;
        if (north  == null) north  = DEFAULT_BLOCK;
        if (south  == null) south  = DEFAULT_BLOCK;
        if (east   == null) east   = DEFAULT_BLOCK;
        if (west   == null) west   = DEFAULT_BLOCK;

        generateCubeModel(pack, id, top, bottom, north, south, east, west);

        if (data.directional) {
            generateDirectionalBlockState(pack, id);
        } else {
            generateBlockState(pack, id);
        }
        generateBlockItemModel(pack, id);
    }

    /**
     * Loads a custom face texture from disk and returns its resource location string.
     * Falls back to the side shortcut if the individual face path is null.
     * Returns null if neither is defined.
     */
    private static String resolveFaceCustom(DynamicResourcePack pack, String blockId,
                                            String faceName, String facePath, String sidePath) {
        String path = facePath != null ? facePath : sidePath;
        if (path == null) return null;

        // Use shared texture if same path was already loaded for another face
        // Key: blockId_faceName (e.g. "my_block_top")
        String texId = blockId + "_" + faceName;
        Path texPath = GEAR_FOLDER.resolve(path);
        if (Files.exists(texPath)) {
            pack.addTexture(blockTextureLoc(texId), texPath);
            return NAMESPACE + ":block/" + texId;
        } else {
            LOGGER.error("[CustomGear] Face texture '{}' not found for block '{}': {}",
                    faceName, blockId, texPath);
            return null;
        }
    }

    // ── Per-face blocks (reference resource locations) ────────────────────────

    private static void loadReferenceFacesBlock(DynamicResourcePack pack, BlockData data) {
        BlockData.BlockFaces f = data.texture.faces;
        String id = data.id;

        String top    = f.top    != null ? f.top    : (f.side != null ? f.side : DEFAULT_BLOCK);
        String bottom = f.bottom != null ? f.bottom : (f.side != null ? f.side : DEFAULT_BLOCK);
        String north  = f.north  != null ? f.north  : (f.side != null ? f.side : DEFAULT_BLOCK);
        String south  = f.south  != null ? f.south  : (f.side != null ? f.side : DEFAULT_BLOCK);
        String east   = f.east   != null ? f.east   : (f.side != null ? f.side : DEFAULT_BLOCK);
        String west   = f.west   != null ? f.west   : (f.side != null ? f.side : DEFAULT_BLOCK);

        generateCubeModel(pack, id, top, bottom, north, south, east, west);

        if (data.directional) {
            generateDirectionalBlockState(pack, id);
        } else {
            generateBlockState(pack, id);
        }
        generateBlockItemModel(pack, id);
    }

    // ── JSON model generation ─────────────────────────────────────────────────

    /**
     * Generates a cube model with different textures per face.
     * Uses minecraft:block/cube parent which allows per-face texture assignment.
     */
    public static void generateCubeModel(DynamicResourcePack pack, String blockId,
                                         String top, String bottom,
                                         String north, String south,
                                         String east, String west) {
        String json = """
            {
              "parent": "minecraft:block/cube",
              "textures": {
                "particle": "%s",
                "up":    "%s",
                "down":  "%s",
                "north": "%s",
                "south": "%s",
                "east":  "%s",
                "west":  "%s"
              }
            }
            """.formatted(north, top, bottom, north, south, east, west);
        pack.addRaw(blockModelLoc(blockId), json.getBytes(StandardCharsets.UTF_8));
    }

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

    /** Simple blockstate — no rotation */
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

    /**
     * Directional blockstate — 4 horizontal variants with Y rotation.
     * "north" = 0°, "east" = 90°, "south" = 180°, "west" = 270°.
     * The front face defined in the model points toward the player when placed.
     */
    public static void generateDirectionalBlockState(DynamicResourcePack pack, String blockId) {
        String json = """
            {
              "variants": {
                "facing=north": { "model": "%s:block/%s" },
                "facing=east":  { "model": "%s:block/%s", "y": 90 },
                "facing=south": { "model": "%s:block/%s", "y": 180 },
                "facing=west":  { "model": "%s:block/%s", "y": 270 }
              }
            }
            """.formatted(
                NAMESPACE, blockId,
                NAMESPACE, blockId,
                NAMESPACE, blockId,
                NAMESPACE, blockId);
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