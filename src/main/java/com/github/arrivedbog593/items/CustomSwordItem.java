package com.github.arrivedbog593.items;

import com.github.arrivedbog593.data.GearData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CustomSwordItem extends SwordItem {

    private final GearData gearData;

    public CustomSwordItem(GearData data) {
        super(
                Tiers.IRON,
                new Item.Properties()
                        .durability(data.durability)
                        .attributes(SwordItem.createAttributes(
                                Tiers.IRON,
                                (int) data.attackDamage,
                                data.attackSpeed
                        ))
        );
        this.gearData = data;
    }

    public GearData getGearData() { return gearData; }

    @Override
    public @NotNull net.minecraft.network.chat.Component getName(@NotNull ItemStack stack) {
        String lang = "en_us";
        try {
            lang = net.minecraft.client.Minecraft.getInstance()
                    .getLanguageManager().getSelected();
        } catch (Exception ignored) {}

        return net.minecraft.network.chat.Component.literal(
                buildName(gearData, lang, "sword"));
    }

    public static String buildName(GearData data, String lang, String toolType) {
        String setName = data.name != null
                ? data.name.getOrDefault(lang, data.name.getOrDefault("en_us", "Unknown"))
                : "Unknown";

        // Si tiene toolNameFormat es un tool_set, usa ese sistema
        if (data.toolNameFormat != null || data.toolNames != null) {
            String toolName = getDefaultToolName(toolType, lang);
            if (data.toolNames != null) {
                Map<String, String> namesForLang = data.toolNames.getOrDefault(lang,
                        data.toolNames.get("en_us"));
                if (namesForLang != null && namesForLang.containsKey(toolType)) {
                    toolName = namesForLang.get(toolType);
                }
            }

            String format = "{name} {tool}";
            if (data.toolNameFormat != null) {
                format = data.toolNameFormat.getOrDefault(lang,
                        data.toolNameFormat.getOrDefault("en_us", "{name} {tool}"));
            }

            return format.replace("{name}", setName).replace("{tool}", toolName);
        }

        // Espada individual — nombre directo
        return setName;
    }

    private static String getDefaultToolName(String type, String lang) {
        if (lang.startsWith("es")) {
            return switch (type) {
                case "sword"   -> "Espada";
                case "pickaxe" -> "Pico";
                case "axe"     -> "Hacha";
                case "shovel"  -> "Pala";
                case "hoe"     -> "Azadón";
                default        -> type;
            };
        }
        return switch (type) {
            case "sword"   -> "Sword";
            case "pickaxe" -> "Pickaxe";
            case "axe"     -> "Axe";
            case "shovel"  -> "Shovel";
            case "hoe"     -> "Hoe";
            default        -> type;
        };
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, gearData);
    }
}