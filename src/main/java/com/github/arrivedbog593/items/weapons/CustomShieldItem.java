package com.github.arrivedbog593.items.weapons;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.loader.GearRegistry;
import com.github.arrivedbog593.util.TooltipHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomShieldItem extends ShieldItem {

    private final GearData initialGearData;

    public CustomShieldItem(GearData data) {
        super(new Item.Properties().durability(data.durability));
        this.initialGearData = data;
    }

    private GearData getGearData() {
        ResourceLocation loc = BuiltInRegistries.ITEM.getKey(this);
        return GearRegistry.GEAR_MAP.getOrDefault(loc, initialGearData);
    }

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        GearData data = getGearData();
        return data.durability > 0 ? data.durability : super.getMaxDamage(stack);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        GearData data = getGearData();
        return Component.literal(
                CustomSwordItem.buildName(data, CustomSwordItem.getCurrentLang(), "shield"));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipComponents, flag);
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, getGearData());
    }
}