package com.github.arrivedbog593.items.tools;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.items.weapons.CustomSwordItem;
import com.github.arrivedbog593.items.gear.CustomTier;
import com.github.arrivedbog593.util.TooltipHelper;
import com.github.arrivedbog593.loader.GearRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomShovelItem extends ShovelItem {

    private final GearData initialGearData;

    public CustomShovelItem(GearData data) {
        this(data, new CustomTier(data));
    }

    private CustomShovelItem(GearData data, CustomTier tier) {
        super(
                tier,
                new Properties()
                        .durability(data.durability)
                        .attributes(ShovelItem.createAttributes(
                                tier,
                                data.attackDamage-1,
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
        return net.minecraft.network.chat.Component.literal(
                CustomSwordItem.buildName(getGearData(), CustomSwordItem.getCurrentLang(), "shovel"));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, getGearData());
    }
}
