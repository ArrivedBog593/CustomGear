package arrivedbog593.ultimatecustomgear.util;

import net.minecraft.world.level.block.SoundType;

/**
 * Resolves a string sound name from a block JSON to a Minecraft SoundType.
 * Used by CustomBlock, CustomDirectionalBlock, and CustomFallingBlock.
 * <p>
 * If the sound name is not recognized, falls back to SoundType.STONE.
 * <p>
 * Usage in JSON: "sound": "gravel"
 */
public final class BlockSoundResolver {

    private BlockSoundResolver() {}

    public static SoundType resolve(String sound) {
        if (sound == null) return SoundType.STONE;
        return switch (sound.toLowerCase()) {
            case "wood"                     -> SoundType.WOOD;
            case "gravel"                   -> SoundType.GRAVEL;
            case "grass"                    -> SoundType.GRASS;
            case "lily_pad"                 -> SoundType.LILY_PAD;
            case "metal"                    -> SoundType.METAL;
            case "glass"                    -> SoundType.GLASS;
            case "wool"                     -> SoundType.WOOL;
            case "sand"                     -> SoundType.SAND;
            case "snow"                     -> SoundType.SNOW;
            case "powder_snow"              -> SoundType.POWDER_SNOW;
            case "ladder"                   -> SoundType.LADDER;
            case "anvil"                    -> SoundType.ANVIL;
            case "slime"                    -> SoundType.SLIME_BLOCK;
            case "honey"                    -> SoundType.HONEY_BLOCK;
            case "wet_grass"                -> SoundType.WET_GRASS;
            case "coral"                    -> SoundType.CORAL_BLOCK;
            case "bamboo"                   -> SoundType.BAMBOO;
            case "bamboo_sapling"           -> SoundType.BAMBOO_SAPLING;
            case "scaffolding"              -> SoundType.SCAFFOLDING;
            case "sweet_berry_bush"         -> SoundType.SWEET_BERRY_BUSH;
            case "crop"                     -> SoundType.CROP;
            case "hard_crop"                -> SoundType.HARD_CROP;
            case "vine"                     -> SoundType.VINE;
            case "nether_wart"              -> SoundType.NETHER_WART;
            case "lantern"                  -> SoundType.LANTERN;
            case "stem"                     -> SoundType.STEM;
            case "nylium"                   -> SoundType.NYLIUM;
            case "fungus"                   -> SoundType.FUNGUS;
            case "roots"                    -> SoundType.ROOTS;
            case "shroomlight"              -> SoundType.SHROOMLIGHT;
            case "weeping_vines"            -> SoundType.WEEPING_VINES;
            case "twisting_vines"           -> SoundType.TWISTING_VINES;
            case "soul_sand"                -> SoundType.SOUL_SAND;
            case "soul_soil"                -> SoundType.SOUL_SOIL;
            case "basalt"                   -> SoundType.BASALT;
            case "wart_block"               -> SoundType.WART_BLOCK;
            case "netherrack"               -> SoundType.NETHERRACK;
            case "nether_bricks"            -> SoundType.NETHER_BRICKS;
            case "nether_sprouts"           -> SoundType.NETHER_SPROUTS;
            case "nether_ore"               -> SoundType.NETHER_ORE;
            case "bone_block"               -> SoundType.BONE_BLOCK;
            case "netherite"                -> SoundType.NETHERITE_BLOCK;
            case "ancient_debris"           -> SoundType.ANCIENT_DEBRIS;
            case "lodestone"                -> SoundType.LODESTONE;
            case "chain"                    -> SoundType.CHAIN;
            case "nether_gold_ore"          -> SoundType.NETHER_GOLD_ORE;
            case "gilded_blackstone"        -> SoundType.GILDED_BLACKSTONE;
            case "candle"                   -> SoundType.CANDLE;
            case "amethyst"                 -> SoundType.AMETHYST;
            case "amethyst_cluster"         -> SoundType.AMETHYST_CLUSTER;
            case "small_amethyst"           -> SoundType.SMALL_AMETHYST_BUD;
            case "medium_amethyst"          -> SoundType.MEDIUM_AMETHYST_BUD;
            case "large_amethyst"           -> SoundType.LARGE_AMETHYST_BUD;
            case "tuff"                     -> SoundType.TUFF;
            case "tuff_bricks"              -> SoundType.TUFF_BRICKS;
            case "polished_tuff"            -> SoundType.POLISHED_TUFF;
            case "calcite"                  -> SoundType.CALCITE;
            case "dripstone"                -> SoundType.DRIPSTONE_BLOCK;
            case "pointed_dripstone"        -> SoundType.POINTED_DRIPSTONE;
            case "copper"                   -> SoundType.COPPER;
            case "copper_bulb"              -> SoundType.COPPER_BULB;
            case "copper_grate"             -> SoundType.COPPER_GRATE;
            case "cave_vines"               -> SoundType.CAVE_VINES;
            case "spore_blossom"            -> SoundType.SPORE_BLOSSOM;
            case "azalea"                   -> SoundType.AZALEA;
            case "flowering_azalea"         -> SoundType.FLOWERING_AZALEA;
            case "moss_carpet"              -> SoundType.MOSS_CARPET;
            case "pink_petals"              -> SoundType.PINK_PETALS;
            case "moss"                     -> SoundType.MOSS;
            case "big_dripleaf"             -> SoundType.BIG_DRIPLEAF;
            case "small_dripleaf"           -> SoundType.SMALL_DRIPLEAF;
            case "rooted_dirt"              -> SoundType.ROOTED_DIRT;
            case "hanging_roots"            -> SoundType.HANGING_ROOTS;
            case "azalea_leaves"            -> SoundType.AZALEA_LEAVES;
            case "sculk"                    -> SoundType.SCULK;
            case "sculk_catalyst"           -> SoundType.SCULK_CATALYST;
            case "sculk_sensor"             -> SoundType.SCULK_SENSOR;
            case "sculk_shrieker"           -> SoundType.SCULK_SHRIEKER;
            case "sculk_vein"               -> SoundType.SCULK_VEIN;
            case "glow_lichen"              -> SoundType.GLOW_LICHEN;
            case "deepslate"                -> SoundType.DEEPSLATE;
            case "deepslate_bricks"         -> SoundType.DEEPSLATE_BRICKS;
            case "deepslate_tiles"          -> SoundType.DEEPSLATE_TILES;
            case "polished_deepslate"       -> SoundType.POLISHED_DEEPSLATE;
            case "froglight"                -> SoundType.FROGLIGHT;
            case "frogspawn"                -> SoundType.FROGSPAWN;
            case "mangrove_roots"           -> SoundType.MANGROVE_ROOTS;
            case "muddy_mangrove"           -> SoundType.MUDDY_MANGROVE_ROOTS;
            case "mud"                      -> SoundType.MUD;
            case "mud_bricks"               -> SoundType.MUD_BRICKS;
            case "packed_mud"               -> SoundType.PACKED_MUD;
            case "hanging_sign"             -> SoundType.HANGING_SIGN;
            case "nether_wood_hanging_sign" -> SoundType.NETHER_WOOD_HANGING_SIGN;
            case "bamboo_wood_hanging_sign" -> SoundType.BAMBOO_WOOD_HANGING_SIGN;
            case "bamboo_wood"              -> SoundType.BAMBOO_WOOD;
            case "nether_wood"              -> SoundType.NETHER_WOOD;
            case "cherry_wood"              -> SoundType.CHERRY_WOOD;
            case "cherry_sapling"           -> SoundType.CHERRY_SAPLING;
            case "cherry_leaves"            -> SoundType.CHERRY_LEAVES;
            case "cherry_wood_hanging_sign" -> SoundType.CHERRY_WOOD_HANGING_SIGN;
            case "chiseled_bookshelf"       -> SoundType.CHISELED_BOOKSHELF;
            case "suspicious_sand"          -> SoundType.SUSPICIOUS_SAND;
            case "suspicious_gravel"        -> SoundType.SUSPICIOUS_GRAVEL;
            case "decorated_pot"            -> SoundType.DECORATED_POT;
            case "decorated_pot_cracked"    -> SoundType.DECORATED_POT_CRACKED;
            case "trial_spawner"            -> SoundType.TRIAL_SPAWNER;
            case "sponge"                   -> SoundType.SPONGE;
            case "wet_sponge"               -> SoundType.WET_SPONGE;
            case "vault"                    -> SoundType.VAULT;
            case "heavy_core"               -> SoundType.HEAVY_CORE;
            case "cobweb"                   -> SoundType.COBWEB;
            default                         -> SoundType.STONE;
        };
    }
}