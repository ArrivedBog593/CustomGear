package com.github.arrivedbog593.items;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.loader.GearRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CustomSwordItem extends SwordItem {

    private final GearData initialGearData;

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
        this.initialGearData = data;
    }

    private GearData getGearData() {
        ResourceLocation itemLocation = BuiltInRegistries.ITEM.getKey(this);
        if (GearRegistry.GEAR_MAP.containsKey(itemLocation)) {
            return GearRegistry.GEAR_MAP.get(itemLocation);
        }
        return initialGearData;
    }

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        GearData data = getGearData();
        return data.durability > 0 ? data.durability : super.getMaxDamage(stack);
    }

    @Override
    public @NotNull net.minecraft.network.chat.Component getName(@NotNull ItemStack stack) {
        String lang = "en_us";
        try {
            lang = net.minecraft.client.Minecraft.getInstance()
                    .getLanguageManager().getSelected();
        } catch (Exception ignored) {}

        return net.minecraft.network.chat.Component.literal(
                buildName(getGearData(), lang, "sword"));
    }

    public static String buildName(GearData data, String lang, String toolType) {
        String setName = data.name != null
                ? data.name.getOrDefault(lang, data.name.getOrDefault("en_us", "Unknown"))
                : "Unknown";

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
                                @NotNull List<net.minecraft.network.chat.Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, getGearData());
    }
}
