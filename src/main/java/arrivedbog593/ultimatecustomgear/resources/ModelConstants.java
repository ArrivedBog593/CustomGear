package arrivedbog593.ultimatecustomgear.resources;

import net.minecraft.resources.Identifier;

/**
 * Shared constants and path helper methods used by all model generators.
 */
public final class ModelConstants {

    private ModelConstants() {}

    // ── Namespace ─────────────────────────────────────────────────────────────
    public static final String NAMESPACE = "customgear";

    // ── Path constants ────────────────────────────────────────────────────────
    /**
     * Where a worn armour layer lives now. It used to be one folder with a
     * _layer_1 / _layer_2 suffix; the renderer no longer builds that name, so
     * the two layers are two folders, each holding a file named after the gear.
     */
    public static final String HUMANOID_LAYER_PATH          = "textures/entity/equipment/humanoid/";
    public static final String HUMANOID_LEGGINGS_LAYER_PATH = "textures/entity/equipment/humanoid_leggings/";
    public static final String ITEM_TEXTURE_PATH  = "textures/item/";
    public static final String BLOCK_TEXTURE_PATH = "textures/block/";
    public static final String MODELS_ITEM_PATH   = "models/item/";
    /**
     * The item DEFINITION folder, which did not exist before 1.21.4. A model
     * under models/item is still a model — geometry and textures — but what an
     * item renders is now chosen a level above, here, and an item with no file
     * in this folder renders as missing however good its model is.
     */
    public static final String ITEMS_PATH         = "items/";
    /** Equipment assets: which textures a worn piece draws, per body layer. */
    public static final String EQUIPMENT_PATH     = "equipment/";
    public static final String BLOCK_MODEL_PATH   = "models/block/";
    public static final String LANG_PATH          = "lang/";

    // ── JSON model parents ────────────────────────────────────────────────────
    public static final String HANDHELD_PARENT  = "minecraft:item/handheld";
    public static final String GENERATED_PARENT = "minecraft:item/generated";

    // ── Default item textures ─────────────────────────────────────────────────
    public static final String DEFAULT_HELMET     = "minecraft:item/iron_helmet";
    public static final String DEFAULT_CHESTPLATE = "minecraft:item/iron_chestplate";
    public static final String DEFAULT_LEGGINGS   = "minecraft:item/iron_leggings";
    public static final String DEFAULT_BOOTS      = "minecraft:item/iron_boots";
    public static final String DEFAULT_SWORD      = "minecraft:item/iron_sword";
    public static final String DEFAULT_PICKAXE    = "minecraft:item/iron_pickaxe";
    public static final String DEFAULT_AXE        = "minecraft:item/iron_axe";
    public static final String DEFAULT_SHOVEL     = "minecraft:item/iron_shovel";
    public static final String DEFAULT_HOE        = "minecraft:item/iron_hoe";
    public static final String DEFAULT_ITEM       = "minecraft:item/paper";
    public static final String DEFAULT_BLOCK      = "minecraft:block/stone";
    public static final String DEFAULT_BOW        = "minecraft:item/bow";
    public static final String DEFAULT_CROSSBOW   = "minecraft:item/crossbow";
    public static final String DEFAULT_SHIELD     = "minecraft:item/shield";
    /** Default texture for a backpack: vanilla's bundle, the closest thing it ships. */
    public static final String DEFAULT_BACKPACK = "minecraft:item/bundle";

    // ── Default pulling/charged model constants ───────────────────────────────
    public static final String DEFAULT_BOW_PULLING_0      = "minecraft:item/bow_pulling_0";
    public static final String DEFAULT_BOW_PULLING_1      = "minecraft:item/bow_pulling_1";
    public static final String DEFAULT_BOW_PULLING_2      = "minecraft:item/bow_pulling_2";
    public static final String DEFAULT_CROSSBOW_PULLING_0 = "minecraft:item/crossbow_pulling_0";
    public static final String DEFAULT_CROSSBOW_PULLING_1 = "minecraft:item/crossbow_pulling_1";
    public static final String DEFAULT_CROSSBOW_PULLING_2 = "minecraft:item/crossbow_pulling_2";
    public static final String DEFAULT_CROSSBOW_ARROW     = "minecraft:item/crossbow_arrow";
    public static final String DEFAULT_CROSSBOW_FIREWORK  = "minecraft:item/crossbow_firework";

    // ── Array constants ───────────────────────────────────────────────────────
    public static final String[] ARMOR_PIECES = {"helmet", "chestplate", "leggings", "boots"};
    public static final String[] TOOL_TYPES   = {"pickaxe", "axe", "shovel", "hoe"};
    public static final String[] WEAPON_TYPES = {"sword", "bow", "crossbow", "shield"};
    public static final String[] LANGS        = {"en_us", "es_mx", "es_es"};

    // ── Path helper methods ───────────────────────────────────────────────────

    /**
     * Where one worn layer of a gear's armour goes.
     *
     * layer_1 is the humanoid body, layer_2 the leggings — the same split the
     * old two suffixes meant, expressed as two folders because that is what the
     * equipment asset addresses.
     */
    public static Identifier armorLayerLoc(String gearId, String layerKey) {
        String folder = "layer_2".equals(layerKey)
                ? HUMANOID_LEGGINGS_LAYER_PATH : HUMANOID_LAYER_PATH;
        return Identifier.fromNamespaceAndPath(NAMESPACE, folder + gearId + ".png");
    }

    /** The item definition that decides what this item renders. */
    public static Identifier itemDefinitionLoc(String itemId) {
        return Identifier.fromNamespaceAndPath(NAMESPACE, ITEMS_PATH + itemId + ".json");
    }

    /** The equipment asset listing a gear's worn layers. */
    public static Identifier equipmentLoc(String gearId) {
        return Identifier.fromNamespaceAndPath(NAMESPACE, EQUIPMENT_PATH + gearId + ".json");
    }

    public static Identifier itemTextureLoc(String itemId) {
        return Identifier.fromNamespaceAndPath(
                NAMESPACE, ITEM_TEXTURE_PATH + itemId + ".png");
    }

    public static Identifier blockTextureLoc(String blockId) {
        return Identifier.fromNamespaceAndPath(
                NAMESPACE, BLOCK_TEXTURE_PATH + blockId + ".png");
    }

    public static Identifier itemModelLoc(String itemId) {
        return Identifier.fromNamespaceAndPath(
                NAMESPACE, MODELS_ITEM_PATH + itemId + ".json");
    }

    public static Identifier blockModelLoc(String blockId) {
        return Identifier.fromNamespaceAndPath(
                NAMESPACE, BLOCK_MODEL_PATH + blockId + ".json");
    }

    public static Identifier blockStateLoc(String blockId) {
        return Identifier.fromNamespaceAndPath(
                NAMESPACE, "blockstates/" + blockId + ".json");
    }

    public static Identifier langLoc(String lang) {
        return Identifier.fromNamespaceAndPath(
                NAMESPACE, LANG_PATH + lang + ".json");
    }

    // ── Default texture locs ───────────────────────────────────────────────────

    /** Vanilla's single chest unwrap, used when a chest declares no texture. */
    public static final String DEFAULT_CHEST_TEXTURE =
            "minecraft:textures/entity/chest/normal.png";
}