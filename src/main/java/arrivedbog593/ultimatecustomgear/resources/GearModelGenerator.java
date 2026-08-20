package arrivedbog593.ultimatecustomgear.resources;

import arrivedbog593.ultimatecustomgear.data.GearData;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static arrivedbog593.ultimatecustomgear.resources.ModelConstants.*;

/**
 * Generates item models for all gear types: armor sets, tool sets, weapon sets,
 * and individual weapons and tools.
 * <p>
 * NO TEXTURE MODE. What a value is gets deduced from the value itself — a
 * resource location if it contains a colon, a file in the content root if it
 * ends in an extension — so a single set can mix its own textures with another
 * mod's, which one shared 'mode' per object made impossible.
 * <p>
 * WHAT A REFERENCE MEANS DEPENDS ON THE SLOT, and that is not arbitrary:
 * <ul>
 *   <li>A TOOL or WEAPON reference names a MODEL. Inheriting it brings that
 *       item's display transforms along, which is the point of pointing at
 *       one.</li>
 *   <li>An ARMOR PIECE or a BOW's base reference names a TEXTURE, used as
 *       layer0 under the type's own parent.</li>
 *   <li>An ARMOR LAYER reference names a texture the armor renderer reads
 *       directly — nothing is copied and no model is generated.</li>
 * </ul>
 * Every one of them takes the SHORT form: the model system adds 'textures/'
 * and '.png' itself, so a long path there would resolve to nothing.
 */
public final class GearModelGenerator {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    private GearModelGenerator() {}

    private static final String ERROR_LAYER_NOT_FOUND   = "[CustomGear] Layer {} not found: {}";
    private static final String ERROR_LAYER_NOT_DEFINED = "[CustomGear] 'layer_{}' not defined in armor_layers for: {}";

    // ── Public entry point ────────────────────────────────────────────────────

    public static void load(PackSink pack, GearData data) {
        // A .geo.json has no reference form, so the 3D assets are copied
        // whenever they are declared. The old code skipped them in reference
        // mode, which meant an armor could not have vanilla-referenced icons
        // and its own GeckoLib model at the same time.
        if ("armor_set".equals(data.type)
                && data.texture != null
                && data.texture.armor3d != null
                && data.texture.armor3d.isComplete()) {
            loadArmor3D(pack, data);
        }

        if (!hasRefs(data)) {
            generateDefaultModels(pack, data);
            // Layers are independent of refs: an armor may rely on armor_3d or
            // on vanilla's iron layers while its icons fall back.
            loadArmorLayers(pack, data);
            return;
        }

        switch (data.type) {
            case "armor_set"  -> loadArmorSet(pack, data);
            case "tool_set"   -> loadToolSet(pack, data);
            case "weapon_set" -> loadWeaponSet(pack, data);
            case "bow", "crossbow", "shield" -> loadWeapon(pack, data, data.type, data.id);
            default           -> loadToolItem(pack, data, data.type, data.id);
        }
    }

    // ── Resolution ────────────────────────────────────────────────────────────

    /** What a resolved gear value turned out to be, and what to use for it. */
    private record Resolved(TextureRef.Kind kind, String value) {}

    /**
     * Resolves one key, copying the file into the pack when it is one.
     * <p>
     * The KIND comes back with the value because the caller still needs it: a
     * reference and a file produce different JSON, so collapsing them into one
     * string here would lose the distinction two lines later.
     *
     * @param texId        the id a copied file lands under, usually the item id
     * @param expectsModel true where a reference names a model rather than a
     *                     texture, which only changes the wording of the error
     */
    private static Resolved resolveGearValue(PackSink pack, String owner, String slot,
                                             String value, String texId, boolean expectsModel) {
        if (value == null) {
            LOGGER.error("[CustomGear] {}: 'refs' has no '{}' key.", owner, slot);
            return new Resolved(TextureRef.Kind.INVALID, null);
        }

        switch (TextureRef.kindOf(value)) {
            case REFERENCE -> {
                // Both a model and a layer0 texture are addressed in the short
                // form. A long path here resolves to nothing, and silently, so
                // it is worth naming what was expected instead.
                if (value.contains("textures/") || value.endsWith(".png")) {
                    LOGGER.error("[CustomGear] {}: '{}' in '{}' is a full asset path, but this key "
                                    + "takes the short form of {} — write it as '{}'.",
                            owner, value, slot,
                            expectsModel ? "a MODEL" : "a texture",
                            expectsModel ? "minecraft:item/diamond_pickaxe" : "minecraft:item/diamond_helmet");
                    return new Resolved(TextureRef.Kind.INVALID, null);
                }
                return new Resolved(TextureRef.Kind.REFERENCE, value);
            }
            case FILE -> {
                Optional<Path> path = TextureLoader.resolveUserResource(value);
                if (path.isEmpty()) {
                    LOGGER.error("[CustomGear] {}: '{}' file not found in the config folder or any "
                            + "pack zip: {}", owner, slot, value);
                    return new Resolved(TextureRef.Kind.INVALID, null);
                }
                pack.addTextureWithMeta(itemTextureLoc(texId), path.get());
                return new Resolved(TextureRef.Kind.FILE, NAMESPACE + ":item/" + texId);
            }
            default -> {
                TextureRef.reportInvalid(owner, slot, value);
                return new Resolved(TextureRef.Kind.INVALID, null);
            }
        }
    }

    private static String owner(GearData data) {
        return "Gear '" + data.id + "'";
    }

    // ── Armor ─────────────────────────────────────────────────────────────────

    private static void loadArmorSet(PackSink pack, GearData data) {
        loadArmorLayers(pack, data);
        for (String piece : ARMOR_PIECES) {
            if (!hasPiece(data, piece)) continue;
            loadArmorPiece(pack, data, piece);
        }
    }

    private static void loadArmorPiece(PackSink pack, GearData data, String piece) {
        String itemId = data.id + "_" + piece;
        Resolved r = resolveGearValue(pack, owner(data), piece,
                data.texture.refs.get(piece), itemId, false);

        switch (r.kind()) {
            case REFERENCE -> generateItemModelWithRef(pack, itemId, r.value());
            case FILE      -> generateArmorItemModel(pack, itemId);
            case INVALID   -> generateItemModelWithRef(pack, itemId, getDefaultArmorTexture(piece));
        }
    }

    private static void loadArmorLayers(PackSink pack, GearData data) {
        if (!hasArmorLayers(data)) {
            if (data.texture != null && data.texture.armor3d == null) {
                LOGGER.warn("[CustomGear] Armor '{}' has no armor_layers and no armor_3d — "
                        + "the worn armor will use the vanilla iron layers", data.id);
            }
            return;
        }
        loadArmorLayer(pack, data, "layer_1");
        loadArmorLayer(pack, data, "layer_2");
    }

    /**
     * A worn layer, which the armor renderer reads directly rather than through
     * a model — so a reference is left alone and only a file is copied.
     */
    private static void loadArmorLayer(PackSink pack, GearData data, String layerKey) {
        String layerNum = layerKey.split("_")[1];
        String value = data.texture.armorLayers.get(layerKey);
        if (value == null) {
            LOGGER.error(ERROR_LAYER_NOT_DEFINED, layerNum, data.id);
            return;
        }
        // A keyword, not a path: it makes the worn armor invisible.
        if (value.equals("transparent")) {
            pack.addRaw(armorTextureLoc(data.id, layerKey), TRANSPARENT_LAYER_PNG);
            return;
        }

        switch (TextureRef.kindOf(value)) {
            case FILE -> {
                Optional<Path> texPath = TextureLoader.resolveUserResource(value);
                if (texPath.isPresent()) {
                    pack.addTextureWithMeta(armorTextureLoc(data.id, layerKey), texPath.get());
                } else {
                    LOGGER.error(ERROR_LAYER_NOT_FOUND, layerNum, value);
                }
            }
            case REFERENCE -> { /* another mod's layer: nothing to copy */ }
            case INVALID   -> TextureRef.reportInvalid(owner(data), layerKey, value);
        }
    }

    /**
     * Copies the user's GeckoLib assets into the dynamic pack, or leaves them
     * where they are when they point at another mod's.
     */
    private static void loadArmor3D(PackSink pack, GearData data) {
        GearData.Armor3DData armor3d = data.texture.armor3d;
        copy3DAsset(pack, armor3d.model,     "geo/armor/" + data.id + ".geo.json",              "model",     data.id);
        copy3DAsset(pack, armor3d.texture,   "textures/armor/" + data.id + ".png",              "texture",   data.id);
        copy3DAsset(pack, armor3d.animation, "animations/armor/" + data.id + ".animation.json", "animation", data.id);
    }

    /**
     * Copies one 3D asset, unless it is a reference.
     * <p>
     * A REFERENCE points at a file already inside another mod's jar, and
     * GeckoLib loads it by ResourceLocation just the same — so copying it would
     * only duplicate it, and redistributing someone else's model is a licensing
     * question this mod has no business answering. The item reads the reference
     * directly; see GeckoArmorItem.
     * <p>
     * The cost is a hard dependency the JSON does not declare: without that mod
     * installed the armor has no model at all. Worth it against shipping copies
     * of assets that are not yours.
     */
    private static void copy3DAsset(PackSink pack, String value, String targetPath,
                                    String fieldName, String gearId) {
        // animation is optional: a null value is not an error
        if (value == null || value.isBlank()) return;

        switch (TextureRef.kindOf(value)) {
            case REFERENCE -> { /* another mod's asset: GeckoLib reads it in place */ }
            case FILE -> {
                Optional<Path> resolved = TextureLoader.resolveUserResource(value);
                if (resolved.isEmpty()) {
                    LOGGER.error("[CustomGear] armor_3d.{} not found in the config folder or any "
                            + "pack zip for '{}': {}", fieldName, gearId, value);
                    return;
                }
                try {
                    pack.addRaw(ResourceLocation.fromNamespaceAndPath(NAMESPACE, targetPath),
                            Files.readAllBytes(resolved.get()));
                } catch (IOException e) {
                    LOGGER.error("[CustomGear] Could not read armor_3d.{} for '{}': {}",
                            fieldName, gearId, e.getMessage());
                }
            }
            case INVALID -> TextureRef.reportInvalid(
                    "Gear '" + gearId + "'", "armor_3d." + fieldName, value);
        }
    }

    // ── Tools ─────────────────────────────────────────────────────────────────

    private static void loadToolSet(PackSink pack, GearData data) {
        if (data.tools == null) return;
        for (String toolType : TOOL_TYPES) {
            if (!hasTool(data, toolType)) continue;
            loadToolItem(pack, data, toolType, data.id + "_" + toolType);
        }
    }

    /**
     * A tool: a reference is a model to inherit, a file is a texture to hang on
     * layer0 under the handheld parent.
     */
    private static void loadToolItem(PackSink pack, GearData data, String slot, String itemId) {
        Resolved r = resolveGearValue(pack, owner(data), slot,
                data.texture.refs.get(slot), itemId, true);

        switch (r.kind()) {
            case REFERENCE -> generateParentOnlyModel(pack, itemId, r.value());
            case FILE      -> generateToolItemModel(pack, itemId);
            case INVALID   -> generateParentOnlyModel(pack, itemId, getDefaultToolModelPath(slot));
        }
    }

    // ── Weapons ───────────────────────────────────────────────────────────────

    private static void loadWeaponSet(PackSink pack, GearData data) {
        if (data.weapons == null) return;
        for (String weaponType : WEAPON_TYPES) {
            if (!data.weapons.containsKey(weaponType)) continue;
            loadWeapon(pack, data, weaponType, data.id + "_" + weaponType);
        }
    }

    private static void loadWeapon(PackSink pack, GearData data, String slot, String itemId) {
        switch (slot) {
            case "bow"      -> loadBow(pack, data, itemId);
            case "crossbow" -> loadCrossbow(pack, data, itemId);
            // The shield ignores its ref: the BEWLR hardcodes the vanilla atlas
            // materials, so there is nothing a value here could change yet.
            case "shield"   -> generateShieldFlatModel(pack, itemId);
            default         -> loadToolItem(pack, data, slot, itemId);
        }
    }

    /**
     * A bow: the base value plus three pulling frames.
     * <p>
     * The parent is DEFAULT_BOW, never HANDHELD_PARENT. That parent is where
     * the bow's display transforms live — with the handheld one the bow renders
     * at tool scale and fills a quarter of the screen in first person.
     * <p>
     * Frames fall back INDIVIDUALLY. One missing texture dropping all three
     * overrides freezes the animation with no visible cause.
     */
    private static void loadBow(PackSink pack, GearData data, String itemId) {
        Resolved base = resolveGearValue(pack, owner(data), "bow",
                data.texture.refs.get("bow"), itemId, false);
        if (base.kind() == TextureRef.Kind.INVALID) {
            generateParentOnlyModel(pack, itemId, DEFAULT_BOW);
            return;
        }

        Map<String, String> refs = data.texture.refs;
        String p0 = frame(pack, itemId, "_pulling_0", refs, "bow_pulling_0",
                DEFAULT_BOW_PULLING_0, DEFAULT_BOW, owner(data));
        String p1 = frame(pack, itemId, "_pulling_1", refs, "bow_pulling_1",
                DEFAULT_BOW_PULLING_1, DEFAULT_BOW, owner(data));
        String p2 = frame(pack, itemId, "_pulling_2", refs, "bow_pulling_2",
                DEFAULT_BOW_PULLING_2, DEFAULT_BOW, owner(data));

        String json = """
        {
          "parent": "%s",
          "textures": { "layer0": "%s" },
          "overrides": [
            { "predicate": { "pulling": 1 },               "model": "%s" },
            { "predicate": { "pulling": 1, "pull": 0.65 }, "model": "%s" },
            { "predicate": { "pulling": 1, "pull": 0.9 },  "model": "%s" }
          ]
        }
        """.formatted(DEFAULT_BOW, base.value(), p0, p1, p2);

        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * A crossbow. Override ORDER matters — Minecraft takes the LAST match, so
     * charged has to come after the pulling entries and firework after charged,
     * otherwise a loaded crossbow renders as a pulling frame.
     */
    private static void loadCrossbow(PackSink pack, GearData data, String itemId) {
        Resolved base = resolveGearValue(pack, owner(data), "crossbow",
                data.texture.refs.get("crossbow"), itemId, false);
        if (base.kind() == TextureRef.Kind.INVALID) {
            generateParentOnlyModel(pack, itemId, DEFAULT_CROSSBOW);
            return;
        }

        Map<String, String> refs = data.texture.refs;
        String p0 = frame(pack, itemId, "_pulling_0", refs, "crossbow_pulling_0",
                DEFAULT_CROSSBOW_PULLING_0, DEFAULT_CROSSBOW, owner(data));
        String p1 = frame(pack, itemId, "_pulling_1", refs, "crossbow_pulling_1",
                DEFAULT_CROSSBOW_PULLING_1, DEFAULT_CROSSBOW, owner(data));
        String p2 = frame(pack, itemId, "_pulling_2", refs, "crossbow_pulling_2",
                DEFAULT_CROSSBOW_PULLING_2, DEFAULT_CROSSBOW, owner(data));
        String arrow = frame(pack, itemId, "_arrow", refs, "crossbow_arrow",
                DEFAULT_CROSSBOW_ARROW, DEFAULT_CROSSBOW, owner(data));
        String firework = frame(pack, itemId, "_firework", refs, "crossbow_firework",
                DEFAULT_CROSSBOW_FIREWORK, DEFAULT_CROSSBOW, owner(data));

        String json = """
        {
          "parent": "%s",
          "textures": { "layer0": "%s" },
          "overrides": [
            { "predicate": { "pulling": 1 },                "model": "%s" },
            { "predicate": { "pulling": 1, "pull": 0.58 },  "model": "%s" },
            { "predicate": { "pulling": 1, "pull": 1.0 },   "model": "%s" },
            { "predicate": { "charged": 1 },                "model": "%s" },
            { "predicate": { "charged": 1, "firework": 1 }, "model": "%s" }
          ]
        }
        """.formatted(DEFAULT_CROSSBOW, base.value(), p0, p1, p2, arrow, firework);

        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * One animation frame, returning the MODEL reference the overrides array
     * needs.
     * <p>
     * A reference here already names a model and goes straight in. A file is a
     * texture, so it gets copied and wrapped in a generated child model — which
     * is what makes the two forms interchangeable from the author's side.
     * Anything missing falls back to the vanilla frame, so a partial set still
     * animates.
     */
    private static String frame(PackSink pack, String itemId, String suffix,
                                Map<String, String> refs, String refKey,
                                String vanilla, String parent, String owner) {
        String value = refs.get(refKey);
        if (value == null || value.isBlank()) return vanilla;

        switch (TextureRef.kindOf(value)) {
            case REFERENCE -> {
                return value;
            }
            case FILE -> {
                Optional<Path> path = TextureLoader.resolveUserResource(value);
                if (path.isEmpty()) {
                    LOGGER.error("[CustomGear] {}: '{}' file not found in the config folder or any "
                            + "pack zip: {}", owner, refKey, value);
                    return vanilla;
                }
                String childId = itemId + suffix;
                pack.addTextureWithMeta(itemTextureLoc(childId), path.get());

                String childJson = """
                {
                  "parent": "%s",
                  "textures": { "layer0": "%s:item/%s" }
                }
                """.formatted(parent, NAMESPACE, childId);
                pack.addRaw(itemModelLoc(childId), childJson.getBytes(StandardCharsets.UTF_8));
                return NAMESPACE + ":item/" + childId;
            }
            default -> {
                TextureRef.reportInvalid(owner, refKey, value);
                return vanilla;
            }
        }
    }

    // ── Default models ────────────────────────────────────────────────────────

    public static void generateDefaultModels(PackSink pack, GearData data) {
        switch (data.type) {
            case "armor_set" -> {
                for (String piece : ARMOR_PIECES) {
                    if (!hasPiece(data, piece)) continue;
                    generateItemModelWithRef(pack, data.id + "_" + piece, getDefaultArmorTexture(piece));
                }
            }
            case "tool_set" -> {
                if (data.tools == null) return;
                for (String toolType : TOOL_TYPES) {
                    if (!hasTool(data, toolType)) continue;
                    generateParentOnlyModel(pack, data.id + "_" + toolType, getDefaultToolModelPath(toolType));
                }
            }
            case "weapon_set" -> {
                if (data.weapons == null) return;
                for (String weaponType : WEAPON_TYPES) {
                    if (!data.weapons.containsKey(weaponType)) continue;
                    String itemId = data.id + "_" + weaponType;
                    if (weaponType.equals("shield")) {
                        generateShieldFlatModel(pack, itemId);
                    } else {
                        generateParentOnlyModel(pack, itemId, getDefaultWeaponParent(weaponType));
                    }
                }
            }
            case "bow"      -> generateBowModelWithRef(pack, data.id, DEFAULT_BOW, Map.of());
            case "crossbow" -> generateCrossbowModelWithRef(pack, data.id, DEFAULT_CROSSBOW, Map.of());
            case "shield"   -> generateShieldFlatModel(pack, data.id);
            case "sword"    -> generateParentOnlyModel(pack, data.id, DEFAULT_SWORD);
            default         -> generateParentOnlyModel(pack, data.id, getDefaultToolModelPath(data.type));
        }
    }

    /** Fully transparent 64x32 PNG, generated once — used for "transparent" armor layers. */
    private static final byte[] TRANSPARENT_LAYER_PNG = createTransparentLayerPng();

    private static byte[] createTransparentLayerPng() {
        try {
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                    64, 32, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(img, "png", out);
            return out.toByteArray();
        } catch (IOException e) {
            LOGGER.error("[CustomGear] Could not generate transparent layer PNG: {}", e.getMessage());
            return new byte[0];
        }
    }

    // ── JSON model generation ─────────────────────────────────────────────────

    public static void generateToolItemModel(PackSink pack, String itemId) {
        String json = """
            {
              "parent": "%s",
              "textures": { "layer0": "%s:item/%s" }
            }
            """.formatted(HANDHELD_PARENT, NAMESPACE, itemId);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateArmorItemModel(PackSink pack, String itemId) {
        String json = """
            {
              "parent": "%s",
              "textures": { "layer0": "%s:item/%s" }
            }
            """.formatted(GENERATED_PARENT, NAMESPACE, itemId);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateGeneratedItemModel(PackSink pack, String itemId) {
        String json = """
            {
              "parent": "%s",
              "textures": { "layer0": "%s:item/%s" }
            }
            """.formatted(GENERATED_PARENT, NAMESPACE, itemId);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateItemModelWithRef(PackSink pack, String itemId, String ref) {
        String json = """
            {
              "parent": "%s",
              "textures": { "layer0": "%s" }
            }
            """.formatted(GENERATED_PARENT, ref);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateParentOnlyModel(PackSink pack, String itemId, String parent) {
        String json = """
            {
              "parent": "%s"
            }
            """.formatted(parent);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateShieldFlatModel(PackSink pack, String itemId) {
        String blockingModelId = itemId + "_blocking";
        String json = """
        {
          "parent": "builtin/entity",
          "gui_light": "front",
          "textures": { "particle": "block/dark_oak_planks" },
          "display": {
            "thirdperson_righthand": { "rotation": [0,90,0],    "translation": [10,6,-4],     "scale": [1,1,1] },
            "thirdperson_lefthand":  { "rotation": [0,90,0],    "translation": [10,6,12],     "scale": [1,1,1] },
            "firstperson_righthand": { "rotation": [0,180,5],   "translation": [-10,2,-10],   "scale": [1.25,1.25,1.25] },
            "firstperson_lefthand":  { "rotation": [0,180,5],   "translation": [10,0,-10],    "scale": [1.25,1.25,1.25] },
            "gui":                   { "rotation": [15,-25,-5],  "translation": [2,3,0],       "scale": [0.65,0.65,0.65] },
            "fixed":                 { "rotation": [0,180,0],   "translation": [-4.5,4.5,-5], "scale": [0.55,0.55,0.55] },
            "ground":                { "rotation": [0,0,0],     "translation": [2,4,2],        "scale": [0.25,0.25,0.25] }
          },
          "overrides": [
            { "predicate": { "blocking": 1 }, "model": "%s:item/%s" }
          ]
        }
        """.formatted(NAMESPACE, blockingModelId);

        String blockingJson = """
        {
          "parent": "builtin/entity",
          "gui_light": "front",
          "textures": { "particle": "block/dark_oak_planks" },
          "display": {
            "thirdperson_righthand": { "rotation": [45,155,0],  "translation": [-3.49,11,-2],  "scale": [1,1,1] },
            "thirdperson_lefthand":  { "rotation": [45,155,0],  "translation": [11.51,7,2.5],  "scale": [1,1,1] },
            "firstperson_righthand": { "rotation": [0,180,-5],  "translation": [-15,5,-11],    "scale": [1.25,1.25,1.25] },
            "firstperson_lefthand":  { "rotation": [0,180,-5],  "translation": [5,5,-11],      "scale": [1.25,1.25,1.25] },
            "gui":                   { "rotation": [15,-25,-5], "translation": [2,3,0],        "scale": [0.65,0.65,0.65] }
          }
        }
        """;

        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
        pack.addRaw(itemModelLoc(blockingModelId), blockingJson.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateBowModelWithRef(PackSink pack, String itemId,
                                               String ref, Map<String, String> refs) {
        String p0 = refs.getOrDefault("bow_pulling_0", DEFAULT_BOW_PULLING_0);
        String p1 = refs.getOrDefault("bow_pulling_1", DEFAULT_BOW_PULLING_1);
        String p2 = refs.getOrDefault("bow_pulling_2", DEFAULT_BOW_PULLING_2);

        String json;
        if (ref.equals(DEFAULT_BOW)
                && p0.equals(DEFAULT_BOW_PULLING_0)
                && p1.equals(DEFAULT_BOW_PULLING_1)
                && p2.equals(DEFAULT_BOW_PULLING_2)) {
            json = """
            {
              "parent": "minecraft:item/bow",
              "overrides": [
                { "predicate": { "pulling": 1 },               "model": "minecraft:item/bow_pulling_0" },
                { "predicate": { "pulling": 1, "pull": 0.65 }, "model": "minecraft:item/bow_pulling_1" },
                { "predicate": { "pulling": 1, "pull": 0.9 },  "model": "minecraft:item/bow_pulling_2" }
              ]
            }
            """;
        } else {
            json = """
            {
              "parent": "minecraft:item/bow",
              "textures": { "layer0": "%s" },
              "overrides": [
                { "predicate": { "pulling": 1 },               "model": "%s" },
                { "predicate": { "pulling": 1, "pull": 0.65 }, "model": "%s" },
                { "predicate": { "pulling": 1, "pull": 0.9 },  "model": "%s" }
              ]
            }
            """.formatted(ref, p0, p1, p2);
        }
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateCrossbowModelWithRef(PackSink pack, String itemId,
                                                    String ref, Map<String, String> refs) {
        String p0       = refs.getOrDefault("crossbow_pulling_0", DEFAULT_CROSSBOW_PULLING_0);
        String p1       = refs.getOrDefault("crossbow_pulling_1", DEFAULT_CROSSBOW_PULLING_1);
        String p2       = refs.getOrDefault("crossbow_pulling_2", DEFAULT_CROSSBOW_PULLING_2);
        String arrow    = refs.getOrDefault("crossbow_arrow",     DEFAULT_CROSSBOW_ARROW);
        String firework = refs.getOrDefault("crossbow_firework",  DEFAULT_CROSSBOW_FIREWORK);

        String json;
        if (ref.equals(DEFAULT_CROSSBOW)
                && p0.equals(DEFAULT_CROSSBOW_PULLING_0)
                && p1.equals(DEFAULT_CROSSBOW_PULLING_1)
                && p2.equals(DEFAULT_CROSSBOW_PULLING_2)
                && arrow.equals(DEFAULT_CROSSBOW_ARROW)
                && firework.equals(DEFAULT_CROSSBOW_FIREWORK)) {
            json = """
            {
              "parent": "minecraft:item/crossbow",
              "overrides": [
                { "predicate": { "pulling": 1 },                "model": "minecraft:item/crossbow_pulling_0" },
                { "predicate": { "pulling": 1, "pull": 0.58 },  "model": "minecraft:item/crossbow_pulling_1" },
                { "predicate": { "pulling": 1, "pull": 1.0 },   "model": "minecraft:item/crossbow_pulling_2" },
                { "predicate": { "charged": 1 },                "model": "minecraft:item/crossbow_arrow" },
                { "predicate": { "charged": 1, "firework": 1 }, "model": "minecraft:item/crossbow_firework" }
              ]
            }
            """;
        } else {
            json = """
            {
              "parent": "minecraft:item/crossbow",
              "textures": { "layer0": "%s" },
              "overrides": [
                { "predicate": { "pulling": 1 },                "model": "%s" },
                { "predicate": { "pulling": 1, "pull": 0.58 },  "model": "%s" },
                { "predicate": { "pulling": 1, "pull": 1.0 },   "model": "%s" },
                { "predicate": { "charged": 1 },                "model": "%s" },
                { "predicate": { "charged": 1, "firework": 1 }, "model": "%s" }
              ]
            }
            """.formatted(ref, p0, p1, p2, arrow, firework);
        }
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    // ── Validation helpers ────────────────────────────────────────────────────

    private static boolean hasArmorLayers(GearData data) {
        return data.texture != null && data.texture.armorLayers != null && !data.texture.armorLayers.isEmpty();
    }

    private static boolean hasRefs(GearData data) {
        return data.texture != null && data.texture.refs != null && !data.texture.refs.isEmpty();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean hasPiece(GearData data, String piece) {
        return data.pieces != null && data.pieces.containsKey(piece);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean hasTool(GearData data, String toolType) {
        return data.tools != null && data.tools.containsKey(toolType);
    }

    // ── Default name helpers ──────────────────────────────────────────────────

    private static String getDefaultArmorTexture(String piece) {
        return switch (piece) {
            case "chestplate" -> DEFAULT_CHESTPLATE;
            case "leggings"   -> DEFAULT_LEGGINGS;
            case "boots"      -> DEFAULT_BOOTS;
            default           -> DEFAULT_HELMET;
        };
    }

    private static String getDefaultToolModelPath(String type) {
        return switch (type) {
            case "axe"    -> DEFAULT_AXE;
            case "shovel" -> DEFAULT_SHOVEL;
            case "hoe"    -> DEFAULT_HOE;
            default       -> DEFAULT_PICKAXE;
        };
    }

    private static String getDefaultWeaponParent(String type) {
        return switch (type) {
            case "bow"      -> DEFAULT_BOW;
            case "crossbow" -> DEFAULT_CROSSBOW;
            case "shield"   -> DEFAULT_SHIELD;
            default         -> DEFAULT_SWORD;
        };
    }
}