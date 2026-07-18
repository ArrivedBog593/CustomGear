package arrivedbog593.ultimatecustomgear.resources;

import arrivedbog593.ultimatecustomgear.data.GearData;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static arrivedbog593.ultimatecustomgear.resources.ModelConstants.*;


public final class GearModelGenerator {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");


    private GearModelGenerator() {}

    // ── Error messages ────────────────────────────────────────────────────────
    private static final String ERROR_ARMOR_LAYERS_REQUIRED   = "[CustomGear] 'armor_layers' is required for armor in custom mode: {}";
    private static final String ERROR_LAYER_NOT_FOUND         = "[CustomGear] Layer {} not found: {}";
    private static final String ERROR_LAYER_NOT_DEFINED       = "[CustomGear] 'layer_{}' not defined in armor_layers for: {}";
    private static final String ERROR_REFS_REQUIRED           = "[CustomGear] 'refs' is required for {} in {} mode: {}";
    private static final String ERROR_PIECE_TEXTURE_NOT_FOUND = "[CustomGear] Piece texture not found: {}";
    private static final String ERROR_TOOL_TEXTURE_NOT_FOUND  = "[CustomGear] Tool texture not found: {}";
    private static final String ERROR_REFS_MISSING_KEY        = "[CustomGear] 'refs' missing key '{}' for: {}";
    private static final String ERROR_REFS_MISSING_KEY_IN     = "[CustomGear] 'refs' missing key '{}' in: {}";
    private static final String ERROR_REFS_REQUIRED_SIMPLE    = "[CustomGear] 'refs' is required in reference mode: {}";
    private static final String ERROR_MODEL3D_MISSING_FIELD   = "[CustomGear] render_mode 'ender_mode 'model_3d' but '{}' is missing in texture for: {}";
    private static final String ERROR_MODEL3D_FILE_NOT_FOUND  = "[CustomGear] 3D file not found ({}): {}";

    // ── Public entry point ────────────────────────────────────────────────────

    public static void loadVanillaShieldTexture(DynamicResourcePack pack) {
        try (InputStream is = GearModelGenerator.class.getResourceAsStream(
                "/assets/minecraft/textures/entity/shield_base_nopattern.png")) {
            if (is != null) {
                pack.addRaw(SHIELD_TEXTURE_LOC, is.readAllBytes());
            } else {
                LOGGER.error("[CustomGear] Could not find vanilla shield texture in classpath");
            }
        } catch (IOException e) {
            LOGGER.error("[CustomGear] Failed to load vanilla shield texture: {}", e.getMessage());
        }
    }

    public static void load(DynamicResourcePack pack, GearData data) {
        if (data.texture == null || data.texture.mode == null) {
            generateDefaultModels(pack, data);
            return;
        }
        if ("armor_set".equals(data.type) && "model_3d".equals(data.texture.renderMode)) {
            loadArmorModel3D(pack, data);
        }
        switch (data.texture.mode) {
            case "custom"    -> loadCustom(pack, data);
            case "reference" -> loadReference(pack, data);
            case "default"   -> generateDefaultModels(pack, data);
            default -> LOGGER.warn("[CustomGear] Invalid texture mode: {}", data.texture.mode);
        }
    }

    // ── Default models ────────────────────────────────────────────────────────

    public static void generateDefaultModels(DynamicResourcePack pack, GearData data) {
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

    // ── Custom loading ────────────────────────────────────────────────────────

    private static void loadCustom(DynamicResourcePack pack, GearData data) {
        switch (data.type) {
            case "armor_set"  -> loadCustomArmor(pack, data);
            case "tool_set"   -> loadCustomToolSet(pack, data);
            case "weapon_set" -> loadCustomWeaponSet(pack, data);
            default           -> loadCustomTool(pack, data);
        }
    }

    private static void loadCustomArmor(DynamicResourcePack pack, GearData data) {
        if (!hasArmorLayers(data)) {
            LOGGER.error(ERROR_ARMOR_LAYERS_REQUIRED, data.id);
            return;
        }
        loadArmorLayer(pack, data, "layer_1");
        loadArmorLayer(pack, data, "layer_2");
        if (!hasRefs(data)) {
            LOGGER.error(ERROR_REFS_REQUIRED, "armor pieces", "custom", data.id);
            return;
        }
        for (String piece : ARMOR_PIECES) {
            if (!hasPiece(data, piece)) continue;
            loadArmorPiece(pack, data, piece);
        }
    }

    private static void loadArmorLayer(DynamicResourcePack pack, GearData data, String layerKey) {
        String layerNum  = layerKey.split("_")[1];
        String layerPath = data.texture.armorLayers.get(layerKey);
        if (layerPath != null) {
            if (layerPath.equals("transparent")) {
                pack.addRaw(armorTextureLoc(data.id, layerKey), TRANSPARENT_LAYER_PNG);
                return;
            }
            Optional<Path> texPath = TextureLoader.resolveUserResource(layerPath);
            if (texPath.isPresent()) {
                pack.addTexture(armorTextureLoc(data.id, layerKey), texPath.get());
            } else {
                LOGGER.error(ERROR_LAYER_NOT_FOUND, layerNum, layerPath);
            }
        } else {
            LOGGER.error(ERROR_LAYER_NOT_DEFINED, layerNum, data.id);
        }
    }

    private static void loadArmorModel3D(DynamicResourcePack pack, GearData data) {
        GearData.TextureData tex = data.texture;
        copyModel3DAsset(pack, tex.armorModel, "geo/armor/" + data.id + ".geo.json", "armor_model", data.id);
        copyModel3DAsset(pack, tex.armorAnimation, "animations/armor/" + data.id + ".animation.json", "armor_animation", data.id);
        copyModel3DAsset(pack, tex.armorTexture, "textures/armor/" + data.id + ".png", "armor_texture", data.id);
    }

    private static void copyModel3DAsset(DynamicResourcePack pack, String ref, String targetPath, String fieldName, String gearId) {
        if (ref == null || ref.isBlank()) {
            LOGGER.error(ERROR_MODEL3D_MISSING_FIELD, fieldName, gearId);
            return;
        }
        Optional<Path> resolved = TextureLoader.resolveUserResource(ref);
        if (resolved.isEmpty()) {
            LOGGER.error(ERROR_MODEL3D_FILE_NOT_FOUND, fieldName, ref);
            return;
        }
        try {
            pack.addRaw(ResourceLocation.fromNamespaceAndPath(NAMESPACE, targetPath), Files.readAllBytes(resolved.get()));
        } catch (IOException e) {
            LOGGER.error(ERROR_MODEL3D_FILE_NOT_FOUND, fieldName, resolved.get());
        }
    }

    private static void loadArmorPiece(DynamicResourcePack pack, GearData data, String piece) {
        String ref = data.texture.refs.get(piece);
        if (ref != null) {
            Optional<Path> texPath = TextureLoader.resolveUserResource(ref);
            if (texPath.isPresent()) {
                String itemId = data.id + "_" + piece;
                pack.addTexture(itemTextureLoc(itemId), texPath.get());
                generateArmorItemModel(pack, itemId);
            } else {
                LOGGER.error(ERROR_PIECE_TEXTURE_NOT_FOUND, ref);
            }
        } else {
            LOGGER.error(ERROR_REFS_MISSING_KEY, piece, data.id);
        }
    }

    private static void loadCustomToolSet(DynamicResourcePack pack, GearData data) {
        if (data.tools == null) return;
        if (!hasRefs(data)) {
            LOGGER.error(ERROR_REFS_REQUIRED, "tools", "custom", data.id);
            return;
        }
        for (String toolType : TOOL_TYPES) {
            if (!hasTool(data, toolType)) continue;
            loadCustomToolItem(pack, data, toolType);
        }
    }

    private static void loadCustomTool(DynamicResourcePack pack, GearData data) {
        if (!hasRefs(data)) {
            LOGGER.error(ERROR_REFS_REQUIRED, "tool", "custom", data.id);
            return;
        }
        String ref = data.texture.refs.get(data.type);
        if (ref != null) {
            loadToolTexture(pack, data.id, ref);
            generateToolItemModel(pack, data.id);
        }
    }

    private static void loadCustomToolItem(DynamicResourcePack pack, GearData data, String toolType) {
        String ref = data.texture.refs.get(toolType);
        if (ref != null) {
            String itemId = data.id + "_" + toolType;
            loadToolTexture(pack, itemId, ref);
            generateToolItemModel(pack, itemId);
        } else {
            LOGGER.error(ERROR_REFS_MISSING_KEY, toolType, data.id);
        }
    }

    private static void loadToolTexture(DynamicResourcePack pack, String itemId, String ref) {
        Optional<Path> texPath = TextureLoader.resolveUserResource(ref);
        if (texPath.isPresent()) {
            pack.addTexture(itemTextureLoc(itemId), texPath.get());
        } else {
            LOGGER.error(ERROR_TOOL_TEXTURE_NOT_FOUND, ref);
        }
    }

    private static void loadCustomWeaponSet(DynamicResourcePack pack, GearData data) {
        if (data.weapons == null) return;
        if (!hasRefs(data)) {
            LOGGER.error(ERROR_REFS_REQUIRED, "weapons", "custom", data.id);
            return;
        }
        for (String weaponType : WEAPON_TYPES) {
            if (!data.weapons.containsKey(weaponType)) continue;
            String ref = data.texture.refs.get(weaponType);
            if (ref == null) {
                LOGGER.error(ERROR_REFS_MISSING_KEY, weaponType, data.id);
                continue;
            }
            String itemId = data.id + "_" + weaponType;
            if (weaponType.equals("bow")) {
                loadCustomBowTextures(pack, data, itemId, ref);
            } else {
                loadToolTexture(pack, itemId, ref);
                generateToolItemModel(pack, itemId);
            }
        }
    }

    private static void loadCustomBowTextures(DynamicResourcePack pack, GearData data,
                                              String itemId, String baseRef) {
        Optional<Path> basePath = TextureLoader.resolveUserResource(baseRef);
        if (basePath.isEmpty()) {
            LOGGER.error(ERROR_TOOL_TEXTURE_NOT_FOUND, baseRef);
            generateParentOnlyModel(pack, itemId, DEFAULT_BOW);
            return;
        }
        pack.addTexture(itemTextureLoc(itemId), basePath.get());
        String[] frames = {"pulling_0", "pulling_1", "pulling_2"};
        boolean hasAllFrames = true;
        for (String frame : frames) {
            String frameRef = data.texture.refs.get("bow_" + frame);
            if (frameRef != null) {
                Optional<Path> framePath = TextureLoader.resolveUserResource(frameRef);
                if (framePath.isPresent()) {
                    pack.addTexture(itemTextureLoc(itemId + "_" + frame), framePath.get());
                } else {
                    LOGGER.error(ERROR_TOOL_TEXTURE_NOT_FOUND, frameRef);
                    hasAllFrames = false;
                }
            } else {
                hasAllFrames = false;
            }
        }
        generateBowItemModel(pack, itemId, hasAllFrames);
    }

    // ── Reference loading ─────────────────────────────────────────────────────

    private static void loadReference(DynamicResourcePack pack, GearData data) {
        if (!hasRefs(data)) {
            LOGGER.error(ERROR_REFS_REQUIRED_SIMPLE, data.id);
            return;
        }
        switch (data.type) {
            case "armor_set"  -> loadReferenceArmor(pack, data);
            case "tool_set"   -> loadReferenceToolSet(pack, data);
            case "weapon_set" -> loadReferenceWeaponSet(pack, data);
            case "bow", "crossbow", "shield" -> loadReferenceWeapon(pack, data);
            default           -> loadReferenceTool(pack, data);
        }
    }

    private static void loadReferenceArmor(DynamicResourcePack pack, GearData data) {
        for (String piece : ARMOR_PIECES) {
            if (!hasPiece(data, piece)) continue;
            String ref = data.texture.refs.get(piece);
            if (ref != null) {
                generateItemModelWithRef(pack, data.id + "_" + piece, ref);
            } else {
                LOGGER.error(ERROR_REFS_MISSING_KEY_IN, piece, data.id);
            }
        }
        if (hasArmorLayers(data)) {
            loadReferenceLayer(pack, data, "layer_1");
            loadReferenceLayer(pack, data, "layer_2");
        }
    }

    /** Reference-mode armor layer: a resource location, or "transparent" for invisibility. */
    private static void loadReferenceLayer(DynamicResourcePack pack, GearData data, String layerKey) {
        String ref = data.texture.armorLayers.get(layerKey);
        if (ref == null) return;
        if (ref.equals("transparent")) {
            pack.addRaw(armorTextureLoc(data.id, layerKey), TRANSPARENT_LAYER_PNG);
            return;
        }
        generateItemModelWithRef(pack, data.id + "_" + layerKey, ref);
    }

    private static void loadReferenceToolSet(DynamicResourcePack pack, GearData data) {
        for (String toolType : TOOL_TYPES) {
            if (!hasTool(data, toolType)) continue;
            String ref = data.texture.refs.get(toolType);
            if (ref != null) {
                generateParentOnlyModel(pack, data.id + "_" + toolType, ref);
            } else {
                LOGGER.error(ERROR_REFS_MISSING_KEY_IN, toolType, data.id);
            }
        }
    }

    private static void loadReferenceTool(DynamicResourcePack pack, GearData data) {
        String ref = data.texture.refs.get(data.type);
        if (ref != null) {
            generateParentOnlyModel(pack, data.id, ref);
        } else {
            LOGGER.error(ERROR_REFS_MISSING_KEY_IN, data.type, data.id);
        }
    }

    private static void loadReferenceWeaponSet(DynamicResourcePack pack, GearData data) {
        for (String weaponType : WEAPON_TYPES) {
            if (data.weapons == null || !data.weapons.containsKey(weaponType)) continue;
            String ref    = data.texture.refs.get(weaponType);
            String itemId = data.id + "_" + weaponType;
            if (ref != null) {
                switch (weaponType) {
                    case "bow"      -> generateBowModelWithRef(pack, itemId, ref, data.texture.refs);
                    case "crossbow" -> generateCrossbowModelWithRef(pack, itemId, ref, data.texture.refs);
                    case "shield"   -> generateShieldFlatModel(pack, itemId);
                    default         -> generateParentOnlyModel(pack, itemId, ref);
                }
            } else {
                LOGGER.error(ERROR_REFS_MISSING_KEY_IN, weaponType, data.id);
                generateParentOnlyModel(pack, itemId, getDefaultWeaponParent(weaponType));
            }
        }
    }

    private static void loadReferenceWeapon(DynamicResourcePack pack, GearData data) {
        String ref = data.texture.refs.get(data.type);
        if (ref != null) {
            switch (data.type) {
                case "bow"      -> generateBowModelWithRef(pack, data.id, ref, data.texture.refs);
                case "crossbow" -> generateCrossbowModelWithRef(pack, data.id, ref, data.texture.refs);
                default         -> generateShieldFlatModel(pack, data.id);
            }
        } else {
            LOGGER.error(ERROR_REFS_MISSING_KEY_IN, data.type, data.id);
            generateParentOnlyModel(pack, data.id, getDefaultWeaponParent(data.type));
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

    public static void generateToolItemModel(DynamicResourcePack pack, String itemId) {
        String json = """
            {
              "parent": "%s",
              "textures": { "layer0": "%s:item/%s" }
            }
            """.formatted(HANDHELD_PARENT, NAMESPACE, itemId);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateArmorItemModel(DynamicResourcePack pack, String itemId) {
        String json = """
            {
              "parent": "%s",
              "textures": { "layer0": "%s:item/%s" }
            }
            """.formatted(GENERATED_PARENT, NAMESPACE, itemId);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateGeneratedItemModel(DynamicResourcePack pack, String itemId) {
        String json = """
            {
              "parent": "%s",
              "textures": { "layer0": "%s:item/%s" }
            }
            """.formatted(GENERATED_PARENT, NAMESPACE, itemId);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateItemModelWithRef(DynamicResourcePack pack, String itemId, String ref) {
        String json = """
            {
              "parent": "%s",
              "textures": { "layer0": "%s" }
            }
            """.formatted(GENERATED_PARENT, ref);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateParentOnlyModel(DynamicResourcePack pack, String itemId, String parent) {
        String json = """
            {
              "parent": "%s"
            }
            """.formatted(parent);
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    public static void generateShieldFlatModel(DynamicResourcePack pack, String itemId) {
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

    public static void generateBowModelWithRef(DynamicResourcePack pack, String itemId,
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

    public static void generateCrossbowModelWithRef(DynamicResourcePack pack, String itemId,
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

    private static void generateBowItemModel(DynamicResourcePack pack,
                                             String itemId, boolean withAnimation) {
        String json;
        if (withAnimation) {
            json = """
            {
              "parent": "%s",
              "textures": { "layer0": "%s:item/%s" },
              "overrides": [
                { "predicate": { "pulling": 1 },               "model": "%s:item/%s_pulling_0" },
                { "predicate": { "pulling": 1, "pull": 0.65 }, "model": "%s:item/%s_pulling_1" },
                { "predicate": { "pulling": 1, "pull": 0.9 },  "model": "%s:item/%s_pulling_2" }
              ]
            }
            """.formatted(HANDHELD_PARENT, NAMESPACE, itemId,
                    NAMESPACE, itemId, NAMESPACE, itemId, NAMESPACE, itemId);
        } else {
            json = """
            {
              "parent": "%s",
              "textures": { "layer0": "%s:item/%s" }
            }
            """.formatted(HANDHELD_PARENT, NAMESPACE, itemId);
        }
        pack.addRaw(itemModelLoc(itemId), json.getBytes(StandardCharsets.UTF_8));
    }

    // ── Validation helpers ────────────────────────────────────────────────────

    private static boolean hasArmorLayers(GearData data) {
        return data.texture != null && data.texture.armorLayers != null && !data.texture.armorLayers.isEmpty();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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