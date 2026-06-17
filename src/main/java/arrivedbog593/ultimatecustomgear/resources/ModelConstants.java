package arrivedbog593.ultimatecustomgear.resources;

import net.minecraft.resources.ResourceLocation;

/**
 * Shared constants and path helper methods used by all model generators.
 */
public final class ModelConstants {

    private ModelConstants() {}

    // ── Namespace ─────────────────────────────────────────────────────────────
    public static final String NAMESPACE = "customgear";

    // ── Path constants ────────────────────────────────────────────────────────
    public static final String ARMOR_TEXTURE_PATH = "textures/models/armor/";
    public static final String ITEM_TEXTURE_PATH  = "textures/item/";
    public static final String BLOCK_TEXTURE_PATH = "textures/block/";
    public static final String MODELS_ITEM_PATH   = "models/item/";
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

    // ── Default pulling/charged model constants ───────────────────────────────
    public static final String DEFAULT_BOW_PULLING_0      = "minecraft:item/bow_pulling_0";
    public static final String DEFAULT_BOW_PULLING_1      = "minecraft:item/bow_pulling_1";
    public static final String DEFAULT_BOW_PULLING_2      = "minecraft:item/bow_pulling_2";
    public static final String DEFAULT_CROSSBOW_PULLING_0 = "minecraft:item/crossbow_pulling_0";
    public static final String DEFAULT_CROSSBOW_PULLING_1 = "minecraft:item/crossbow_pulling_1";
    public static final String DEFAULT_CROSSBOW_PULLING_2 = "minecraft:item/crossbow_pulling_2";
    public static final String DEFAULT_CROSSBOW_ARROW     = "minecraft:item/crossbow_arrow";
    public static final String DEFAULT_CROSSBOW_FIREWORK  = "minecraft:item/crossbow_firework";

    // ── Shield texture ────────────────────────────────────────────────────────
    public static final ResourceLocation SHIELD_TEXTURE_LOC =
            ResourceLocation.fromNamespaceAndPath(NAMESPACE, "textures/item/shield_base_nopattern.png");

    // ── Array constants ───────────────────────────────────────────────────────
    public static final String[] ARMOR_PIECES = {"helmet", "chestplate", "leggings", "boots"};
    public static final String[] TOOL_TYPES   = {"pickaxe", "axe", "shovel", "hoe"};
    public static final String[] WEAPON_TYPES = {"sword", "bow", "crossbow", "shield"};
    public static final String[] LANGS        = {"en_us", "es_mx", "es_es"};

    // ── Path helper methods ───────────────────────────────────────────────────

    public static ResourceLocation armorTextureLoc(String gearId, String layer) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, ARMOR_TEXTURE_PATH + gearId + "_" + layer + ".png");
    }

    public static ResourceLocation itemTextureLoc(String itemId) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, ITEM_TEXTURE_PATH + itemId + ".png");
    }

    public static ResourceLocation blockTextureLoc(String blockId) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, BLOCK_TEXTURE_PATH + blockId + ".png");
    }

    public static ResourceLocation itemModelLoc(String itemId) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, MODELS_ITEM_PATH + itemId + ".json");
    }

    public static ResourceLocation blockModelLoc(String blockId) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, BLOCK_MODEL_PATH + blockId + ".json");
    }

    public static ResourceLocation blockStateLoc(String blockId) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, "blockstates/" + blockId + ".json");
    }

    public static ResourceLocation langLoc(String lang) {
        return ResourceLocation.fromNamespaceAndPath(
                NAMESPACE, LANG_PATH + lang + ".json");
    }
}