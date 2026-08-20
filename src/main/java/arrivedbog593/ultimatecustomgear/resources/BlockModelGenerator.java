package arrivedbog593.ultimatecustomgear.resources;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import arrivedbog593.ultimatecustomgear.data.ContainerContentData;
import arrivedbog593.ultimatecustomgear.data.ContainerData;
import arrivedbog593.ultimatecustomgear.data.FluidData;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static arrivedbog593.ultimatecustomgear.resources.ModelConstants.*;

/**
 * Generates block models, blockstates, block item models and fluid bucket models.
 * <p>
 * Supports three block modes:
 *  1. Simple (cube_all)     — same texture on all faces
 *  2. Per-face (cube)       — different texture per face via "faces" field
 *  3. Directional (cube)    — rotates when placed, front face defined by "faces.north"
 * <p>
 * Key naming: "all" is the CANONICAL refs key for single-texture blocks in
 * BOTH modes (this texture on ALL faces — contrast with "faces"); "block" is
 * accepted as a legacy alias for backward compatibility. See allFacesRef().
 * <p>
 * Custom texture paths are resolved through TextureLoader.resolveUserResource,
 * which searches the loose config folder AND every mounted content zip —
 * never a hardcoded working-directory path.
 */
public final class BlockModelGenerator {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    private BlockModelGenerator() {}

    // ── Public entry points ───────────────────────────────────────────────────

    public static void loadBlock(PackSink pack, BlockData data) {
        LOGGER.info("[CustomGear] Loading block: {}", data.id);

        // Containers have their own model shapes and blockstate properties, so
        // they never go through the plain cube path below.
        if (data instanceof ContainerContentData c && c.container != null) {
            loadContainer(pack, c);
            return;
        }

        // Per-face mode if faces are declared — either via the "faces" object
        // or via face keys inside "refs". resolveFaces() unifies both.
        // Per-face if faces are declared — either via the "faces" object or via
        // face keys inside "refs". resolveFaces() unifies both.
        BlockData.BlockFaces faces = data.texture != null ? data.texture.resolveFaces() : null;
        if (faces != null) {
            loadFacesBlock(pack, data, faces);
            return;
        }

        // Single-texture block. No mode is consulted: the value says what it is.
        // This is also where the old inconsistency lived — a block with 'faces'
        // and no 'mode' painted, while the same block WITHOUT 'faces' fell to
        // stone even with a perfectly good refs.all. Nobody chose that.
        String all = data.texture != null ? data.texture.allRef() : null;
        if (all == null) {
            generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
            return;
        }

        switch (TextureRef.kindOf(all)) {
            case REFERENCE -> generateBlockWithRef(pack, data.id, all);
            case FILE -> {
                java.util.Optional<Path> texPath = TextureLoader.resolveUserResource(all);
                if (texPath.isPresent()) {
                    pack.addTextureWithMeta(blockTextureLoc(data.id), texPath.get());
                    generateBlockModel(pack, data.id);
                    generateBlockState(pack, data.id);
                    generateBlockItemModel(pack, data.id);
                } else {
                    LOGGER.error("[CustomGear] Block '{}': texture file not found in the config "
                            + "folder or any pack zip: {}", data.id, all);
                    generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
                }
            }
            case INVALID -> {
                TextureRef.reportInvalid("Block '" + data.id + "'", "all", all);
                generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
            }
            default -> generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
        }
    }

    public static void generateBucketModel(PackSink pack, FluidData data) {
        String value = data.texture != null && data.texture.refs != null
                ? data.texture.refs.get("bucket") : null;

        String bucketTexture;
        if (value == null) {
            bucketTexture = "minecraft:item/water_bucket";
        } else {
            switch (TextureRef.kindOf(value)) {
                case REFERENCE -> bucketTexture = value;
                case FILE -> {
                    java.util.Optional<Path> texPath = TextureLoader.resolveUserResource(value);
                    if (texPath.isPresent()) {
                        pack.addTextureWithMeta(itemTextureLoc(data.id + "_bucket"), texPath.get());
                        bucketTexture = NAMESPACE + ":item/" + data.id + "_bucket";
                    } else {
                        LOGGER.error("[CustomGear] Fluid '{}': bucket texture file not found: {}",
                                data.id, value);
                        bucketTexture = "minecraft:item/water_bucket";
                    }
                }
                default -> {
                    TextureRef.reportInvalid("Fluid '" + data.id + "'", "bucket", value);
                    bucketTexture = "minecraft:item/water_bucket";
                }
            }
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

    /**
     * Copies a fluid's own texture files into the pack.
     * <p>
     * NEEDED BECAUSE NOBODY DID IT. FluidRegistry has always pointed custom
     * fluids at customgear:fluid/&lt;id&gt;_still, but nothing ever put a file
     * there — the mode was wired up on the reading side only, so a fluid with
     * its own PNG rendered as a missing texture. This is the missing half.
     * <p>
     * The sprite path and the file path are NOT the same string: a fluid sprite
     * is addressed without 'textures/' and without '.png', while the file has to
     * land at assets/customgear/textures/fluid/&lt;id&gt;_still.png for the atlas
     * to pick it up.
     */
    public static void loadFluidTextures(PackSink pack, FluidData data) {
        if (data.texture == null || data.texture.refs == null) return;
        copyFluidTexture(pack, data.id, "still",   data.texture.refs.get("still"));
        copyFluidTexture(pack, data.id, "flowing", data.texture.refs.get("flowing"));
    }

    private static void copyFluidTexture(PackSink pack, String fluidId, String slot, String value) {
        // A reference points at a sprite already in the atlas; only a file needs
        // copying, and an unusable value is reported by FluidRegistry when it
        // resolves the sprite.
        if (value == null || TextureRef.kindOf(value) != TextureRef.Kind.FILE) return;

        java.util.Optional<Path> texPath = TextureLoader.resolveUserResource(value);
        if (texPath.isEmpty()) {
            LOGGER.error("[CustomGear] Fluid '{}': '{}' texture file not found in the config "
                    + "folder or any pack zip: {}", fluidId, slot, value);
            return;
        }

        pack.addTextureWithMeta(ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, "textures/block/" + fluidId + "_" + slot + ".png"), texPath.get());
    }

    /**
     * ONE path for both kinds, and mixing them is the point: a barrel with its
     * own top and vanilla sides was impossible while a single 'mode' decided for
     * the whole object.
     */
    private static void loadFacesBlock(PackSink pack, BlockData data, BlockData.BlockFaces f) {
        String id = data.id;

        String top    = face(pack, id, "top",    f.top,    null,   data.id);
        String bottom = face(pack, id, "bottom", f.bottom, null,   data.id);
        String north  = face(pack, id, "north",  f.north,  f.side, data.id);
        String south  = face(pack, id, "south",  f.south,  f.side, data.id);
        String east   = face(pack, id, "east",   f.east,   f.side, data.id);
        String west   = face(pack, id, "west",   f.west,   f.side, data.id);

        generateCubeModel(pack, id, top, bottom, north, south, east, west);

        if (data.directional) {
            generateDirectionalBlockState(pack, id);
        } else {
            generateBlockState(pack, id);
        }
        generateBlockItemModel(pack, id);
    }

    /**
     * Resolves one face, falling back to the 'side' shortcut and then to the
     * default block texture.
     * <p>
     * A face that is simply ABSENT falls back quietly; one that is present but
     * unusable says so. Those two used to look the same from the outside, which
     * is how a typo could turn into a stone face with nothing in the log.
     */
    private static String face(PackSink pack, String blockId, String faceName,
                               String facePath, String sidePath, String owner) {
        String value = facePath != null ? facePath : sidePath;
        if (value == null) return DEFAULT_BLOCK;

        return switch (TextureRef.kindOf(value)) {
            case REFERENCE -> value;
            case FILE -> {
                String texId = blockId + "_" + faceName;
                java.util.Optional<Path> texPath = TextureLoader.resolveUserResource(value);
                if (texPath.isPresent()) {
                    pack.addTextureWithMeta(blockTextureLoc(texId), texPath.get());
                    yield NAMESPACE + ":block/" + texId;
                }
                LOGGER.error("[CustomGear] Block '{}': face '{}' file not found in the config "
                        + "folder or any pack zip: {}", owner, faceName, value);
                yield DEFAULT_BLOCK;
            }
            case INVALID -> {
                TextureRef.reportInvalid("Block '" + owner + "'", faceName, value);
                yield DEFAULT_BLOCK;
            }
        };
    }

    /**
     * Generates a cube model with different textures per face.
     * Uses minecraft:block/cube parent which allows per-face texture assignment.
     */
    public static void generateCubeModel(PackSink pack, String blockId,
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

    public static void generateBlockModel(PackSink pack, String blockId) {
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
    public static void generateBlockState(PackSink pack, String blockId) {
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
    public static void generateDirectionalBlockState(PackSink pack, String blockId) {
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

    public static void generateBlockItemModel(PackSink pack, String blockId) {
        String json = """
            {
              "parent": "%s:block/%s"
            }
            """.formatted(NAMESPACE, blockId);
        pack.addRaw(itemModelLoc(blockId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateBlockWithRef(PackSink pack, String blockId, String ref) {
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

    // ── Containers ────────────────────────────────────────────────────────────

    /**
     * Only the barrel exists so far; the parser rejects the other three, so
     * anything reaching here is one. The switch is written out anyway so adding
     * a subtype means adding a case, not finding this method.
     */
    private static void loadContainer(PackSink pack, ContainerContentData data) {
        if (ContainerData.KIND_BARREL.equals(data.container.kind())) {
            loadBarrel(pack, data);
        } else if (ContainerData.KIND_CHEST.equals(data.container.kind())) {
            loadChest(pack, data);
        } else if (ContainerData.KIND_SHULKER.equals(data.container.kind())) {
            loadShulker(pack, data);
        } else {
            LOGGER.error("[CustomGear] Container '{}': no model generator for type '{}' — "
                    + "falling back to a plain cube.", data.id, data.container.kind());
            generateBlockWithRef(pack, data.id, DEFAULT_BLOCK);
        }
    }

    /**
     * A chest generates almost nothing: the block model is never drawn, since
     * the block reports ENTITYBLOCK_ANIMATED. The blockstate still has to exist
     * and point somewhere, or the game logs a missing-model error for every
     * variant, and the item model is a placeholder until the BEWLR lands.
     */
    private static void loadChest(PackSink pack, ContainerContentData data) {
        String id = data.id;
        ContainerTextures.inject(pack, data);
        String json = """
            {
              "variants": {
                "facing=north": { "model": "%s:block/%s" },
                "facing=east":  { "model": "%s:block/%s" },
                "facing=south": { "model": "%s:block/%s" },
                "facing=west":  { "model": "%s:block/%s" }
              }
            }
            """.formatted(NAMESPACE, id, NAMESPACE, id, NAMESPACE, id, NAMESPACE, id);
        pack.addRaw(blockStateLoc(id), json.getBytes(StandardCharsets.UTF_8));

        // Particles when the chest breaks come from this model, so it points at
        // a real texture even though its geometry is never drawn.
        String model = """
            {
              "parent": "minecraft:block/block",
              "textures": { "particle": "minecraft:block/oak_planks" }
            }
            """;
        pack.addRaw(blockModelLoc(id), model.getBytes(StandardCharsets.UTF_8));

        // builtin/entity hands rendering over to the BEWLR. Parenting to the
        // block model instead would draw the invisible placeholder above.
        String itemModel = """
            {
              "parent": "builtin/entity",
              "display": {
                "gui":            { "rotation": [30, 45, 0],  "translation": [0, 0, 0],    "scale": [0.625, 0.625, 0.625] },
                "ground":         { "rotation": [0, 0, 0],    "translation": [0, 3, 0],    "scale": [0.25, 0.25, 0.25] },
                "head":           { "rotation": [0, 180, 0],  "translation": [0, 0, 0],    "scale": [1, 1, 1] },
                "fixed":          { "rotation": [0, 180, 0],  "translation": [0, 0, 0],    "scale": [0.5, 0.5, 0.5] },
                "thirdperson_righthand": { "rotation": [75, 315, 0], "translation": [0, 2.5, 0], "scale": [0.375, 0.375, 0.375] },
                "firstperson_righthand": { "rotation": [0, 315, 0],  "translation": [0, 0, 0],   "scale": [0.4, 0.4, 0.4] },
                "firstperson_lefthand":  { "rotation": [0, 315, 0],  "translation": [0, 0, 0],   "scale": [0.4, 0.4, 0.4] }
              }
            }
            """;
        pack.addRaw(itemModelLoc(id), itemModel.getBytes(StandardCharsets.UTF_8));
    }

    private static void loadBarrel(PackSink pack, ContainerContentData data) {
        String id = data.id;
        BlockData.BlockFaces f = data.texture != null ? data.texture.resolveFaces() : null;

        if (f != null && (f.north != null || f.south != null || f.east != null || f.west != null)) {
            LOGGER.warn("[CustomGear] Container '{}' is a barrel: only 'top', 'bottom', 'side' "
                    + "and 'top_open' are used. The per-direction faces are ignored.", id);
        }

        String top    = barrelTexture(pack, id, "top",    f == null ? null : f.top,
                "minecraft:block/barrel_top");
        String bottom = barrelTexture(pack, id, "bottom", f == null ? null : f.bottom,
                "minecraft:block/barrel_bottom");
        String side   = barrelTexture(pack, id, "side",   f == null ? null : f.side,
                "minecraft:block/barrel_side");
        // Three steps, and the middle one is the reason this is not just `top`:
        // an author who supplied their own top but no open variant gets THEIR
        // top back, because dropping vanilla's open lid onto a custom barrel
        // would look wrong. An author who supplied neither gets vanilla's open
        // lid, so a barrel with no texture block at all still opens visibly.
        String topOpenFallback = (f != null && f.top != null)
                ? top
                : "minecraft:block/barrel_top_open";
        String topOpen = barrelTexture(pack, id, "top_open", f == null ? null : f.topOpen,
                topOpenFallback);

        generateBarrelModel(pack, id,           top,     bottom, side);
        generateBarrelModel(pack, id + "_open", topOpen, bottom, side);
        generateBarrelBlockState(pack, id);
        generateBlockItemModel(pack, id);
    }

    /**
     * One barrel texture slot. A reference is used as written; a file is copied
     * into the pack under a name derived from the slot, so top and top_open
     * never collide.
     */
    private static String barrelTexture(PackSink pack, String blockId, String slot,
                                        String value, String fallback) {
        if (value == null) return fallback;

        return switch (TextureRef.kindOf(value)) {
            case REFERENCE -> value;
            case FILE -> {
                String texId = blockId + "_" + slot;
                java.util.Optional<Path> texPath = TextureLoader.resolveUserResource(value);
                if (texPath.isPresent()) {
                    pack.addTextureWithMeta(blockTextureLoc(texId), texPath.get());
                    yield NAMESPACE + ":block/" + texId;
                }
                LOGGER.error("[CustomGear] Container '{}': barrel texture '{}' file not found: {}",
                        blockId, slot, value);
                yield fallback;
            }
            case INVALID -> {
                TextureRef.reportInvalid("Container '" + blockId + "'", slot, value);
                yield fallback;
            }
        };
    }

    public static void generateBarrelModel(PackSink pack, String modelId,
                                           String top, String bottom, String side) {
        String json = """
            {
              "parent": "minecraft:block/cube_bottom_top",
              "textures": {
                "top":    "%s",
                "bottom": "%s",
                "side":   "%s"
              }
            }
            """.formatted(top, bottom, side);
        pack.addRaw(blockModelLoc(modelId), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Twelve variants: six facings times open/closed. The rotations are vanilla's
     * own, taken from minecraft:blockstates/barrel.json — the model's top points
     * at +Y, so facing=up needs no rotation at all.
     */
    public static void generateBarrelBlockState(PackSink pack, String blockId) {
        // facing, x rotation, y rotation
        String[][] facings = {
                {"down",  "180", "0"},
                {"up",    "0",   "0"},
                {"north", "90",  "0"},
                {"south", "90",  "180"},
                {"east",  "90",  "90"},
                {"west",  "90",  "270"},
        };

        StringBuilder sb = new StringBuilder("{\n  \"variants\": {\n");
        boolean first = true;
        for (String[] fa : facings) {
            for (boolean open : new boolean[]{false, true}) {
                if (!first) sb.append(",\n");
                first = false;
                sb.append("    \"facing=").append(fa[0])
                        .append(",open=").append(open).append("\": { \"model\": \"")
                        .append(NAMESPACE).append(":block/").append(blockId)
                        .append(open ? "_open" : "").append('"');
                if (!"0".equals(fa[1])) sb.append(", \"x\": ").append(fa[1]);
                if (!"0".equals(fa[2])) sb.append(", \"y\": ").append(fa[2]);
                sb.append(" }");
            }
        }
        sb.append("\n  }\n}\n");
        pack.addRaw(blockStateLoc(blockId), sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Like the chest: the block model is never drawn, but the blockstate has to
     * exist and point somewhere or every variant logs a missing-model error.
     * Six facings, all the same model — the renderer does the rotating.
     */
    private static void loadShulker(PackSink pack, ContainerContentData data) {
        String id = data.id;
        ContainerTextures.inject(pack, data);

        StringBuilder sb = new StringBuilder("{\n  \"variants\": {\n");
        String[] facings = {"north", "east", "south", "west", "up", "down"};
        for (int i = 0; i < facings.length; i++) {
            if (i > 0) sb.append(",\n");
            sb.append("    \"facing=").append(facings[i])
                    .append("\": { \"model\": \"").append(NAMESPACE).append(":block/").append(id).append("\" }");
        }
        sb.append("\n  }\n}\n");
        pack.addRaw(blockStateLoc(id), sb.toString().getBytes(StandardCharsets.UTF_8));

        String model = """
            {
              "parent": "minecraft:block/block",
              "textures": { "particle": "minecraft:block/shulker_box" }
            }
            """;
        pack.addRaw(blockModelLoc(id), model.getBytes(StandardCharsets.UTF_8));
        // builtin/entity hands rendering over to the BEWLR. Parenting to the
        // block model instead would draw the invisible placeholder above.
        String itemModel = """
            {
              "parent": "builtin/entity",
              "display": {
                "gui":            { "rotation": [30, 45, 0],  "translation": [0, 0, 0],    "scale": [0.625, 0.625, 0.625] },
                "ground":         { "rotation": [0, 0, 0],    "translation": [0, 3, 0],    "scale": [0.25, 0.25, 0.25] },
                "head":           { "rotation": [0, 180, 0],  "translation": [0, 0, 0],    "scale": [1, 1, 1] },
                "fixed":          { "rotation": [0, 180, 0],  "translation": [0, 0, 0],    "scale": [0.5, 0.5, 0.5] },
                "thirdperson_righthand": { "rotation": [75, 315, 0], "translation": [0, 2.5, 0], "scale": [0.375, 0.375, 0.375] },
                "firstperson_righthand": { "rotation": [0, 315, 0],  "translation": [0, 0, 0],   "scale": [0.4, 0.4, 0.4] },
                "firstperson_lefthand":  { "rotation": [0, 315, 0],  "translation": [0, 0, 0],   "scale": [0.4, 0.4, 0.4] }
              }
            }
            """;
        pack.addRaw(itemModelLoc(id), itemModel.getBytes(StandardCharsets.UTF_8));
    }
}