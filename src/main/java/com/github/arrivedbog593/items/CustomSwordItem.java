package com.github.arrivedbog593.items;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.loader.GearRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CustomSwordItem extends SwordItem {

    private final GearData initialGearData;

    public CustomSwordItem(GearData data) {
        super(
                new CustomTier(data),
                new Item.Properties()
                        .durability(data.durability)
                        .attributes(SwordItem.createAttributes(
                                new CustomTier(data),
                                (int) data.attackDamage-1,
                                data.attackSpeed-4
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
        // Primero intenta con toolNames (para tool_set)
        if (data.toolNames != null) {
            Map<String, String> namesForLang = data.toolNames.getOrDefault(lang,
                    data.toolNames.get("en_us"));

            if (namesForLang != null && namesForLang.containsKey(toolType)) {
                return namesForLang.get(toolType);
            }
        }

        // Si no encuentra, intenta con name (para herramientas individuales)
        if (data.name != null) {
            return data.name.getOrDefault(lang, data.name.get("en_us"));
        }

        return "Unknown";
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
