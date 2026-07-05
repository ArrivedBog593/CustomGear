package arrivedbog593.ultimatecustomgear.util;

import net.minecraft.world.level.material.MapColor;

/**
 * Resolves a string map color name from a block JSON to a Minecraft MapColor.
 * Used by CustomBlock, CustomDirectionalBlock, and CustomFallingBlock.
 * <p>
 * - If the color name is not recognized, falls back to MapColor.NONE.
 * <p>
 * Usage in JSON: "map_color": "grass"
 */
public final class MapColorResolver {

    private MapColorResolver() {}

    public static MapColor resolve(String color) {
        if (color == null) return MapColor.STONE;

        return switch (color.toLowerCase()) {
            case "none" -> MapColor.NONE;
            case "grass" -> MapColor.GRASS;
            case "sand" -> MapColor.SAND;
            case "wool" -> MapColor.WOOL;
            case "fire" -> MapColor.FIRE;
            case "ice" -> MapColor.ICE;
            case "metal" -> MapColor.METAL;
            case "plant" -> MapColor.PLANT;
            case "snow" -> MapColor.SNOW;
            case "clay" -> MapColor.CLAY;
            case "dirt" -> MapColor.DIRT;
            case "water" -> MapColor.WATER;
            case "wood" -> MapColor.WOOD;
            case "quartz" -> MapColor.QUARTZ;

            case "orange" -> MapColor.COLOR_ORANGE;
            case "magenta" -> MapColor.COLOR_MAGENTA;
            case "light_blue" -> MapColor.COLOR_LIGHT_BLUE;
            case "yellow" -> MapColor.COLOR_YELLOW;
            case "light_green" -> MapColor.COLOR_LIGHT_GREEN;
            case "pink" -> MapColor.COLOR_PINK;
            case "gray" -> MapColor.COLOR_GRAY;
            case "light_gray" -> MapColor.COLOR_LIGHT_GRAY;
            case "cyan" -> MapColor.COLOR_CYAN;
            case "purple" -> MapColor.COLOR_PURPLE;
            case "blue" -> MapColor.COLOR_BLUE;
            case "brown" -> MapColor.COLOR_BROWN;
            case "green" -> MapColor.COLOR_GREEN;
            case "red" -> MapColor.COLOR_RED;
            case "black" -> MapColor.COLOR_BLACK;

            case "gold" -> MapColor.GOLD;
            case "diamond" -> MapColor.DIAMOND;
            case "lapis" -> MapColor.LAPIS;
            case "emerald" -> MapColor.EMERALD;
            case "podzol" -> MapColor.PODZOL;
            case "nether" -> MapColor.NETHER;

            case "terracotta_white" -> MapColor.TERRACOTTA_WHITE;
            case "terracotta_orange" -> MapColor.TERRACOTTA_ORANGE;
            case "terracotta_magenta" -> MapColor.TERRACOTTA_MAGENTA;
            case "terracotta_light_blue" -> MapColor.TERRACOTTA_LIGHT_BLUE;
            case "terracotta_yellow" -> MapColor.TERRACOTTA_YELLOW;
            case "terracotta_light_green" -> MapColor.TERRACOTTA_LIGHT_GREEN;
            case "terracotta_pink" -> MapColor.TERRACOTTA_PINK;
            case "terracotta_gray" -> MapColor.TERRACOTTA_GRAY;
            case "terracotta_light_gray" -> MapColor.TERRACOTTA_LIGHT_GRAY;
            case "terracotta_cyan" -> MapColor.TERRACOTTA_CYAN;
            case "terracotta_purple" -> MapColor.TERRACOTTA_PURPLE;
            case "terracotta_blue" -> MapColor.TERRACOTTA_BLUE;
            case "terracotta_brown" -> MapColor.TERRACOTTA_BROWN;
            case "terracotta_green" -> MapColor.TERRACOTTA_GREEN;
            case "terracotta_red" -> MapColor.TERRACOTTA_RED;
            case "terracotta_black" -> MapColor.TERRACOTTA_BLACK;

            case "crimson_nylium" -> MapColor.CRIMSON_NYLIUM;
            case "crimson_stem" -> MapColor.CRIMSON_STEM;
            case "crimson_hyphae" -> MapColor.CRIMSON_HYPHAE;
            case "warped_nylium" -> MapColor.WARPED_NYLIUM;
            case "warped_stem" -> MapColor.WARPED_STEM;
            case "warped_hyphae" -> MapColor.WARPED_HYPHAE;
            case "warped_wart_block" -> MapColor.WARPED_WART_BLOCK;
            case "deepslate" -> MapColor.DEEPSLATE;
            case "raw_iron" -> MapColor.RAW_IRON;
            case "glow_lichen" -> MapColor.GLOW_LICHEN;

            default -> MapColor.STONE;
        };
    }
}