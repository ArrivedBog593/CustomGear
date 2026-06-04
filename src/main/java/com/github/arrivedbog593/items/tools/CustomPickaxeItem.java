package com.github.arrivedbog593.items.tools;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.items.gear.CustomTier;
import com.github.arrivedbog593.items.weapons.CustomSwordItem;
import com.github.arrivedbog593.util.GearLookup;
import com.github.arrivedbog593.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomPickaxeItem extends PickaxeItem {

    private final GearData initialGearData;

    public CustomPickaxeItem(GearData data) {
        this(data, new CustomTier(data));
    }

    private CustomPickaxeItem(GearData data, CustomTier tier) {
        super(
                tier,
                new Properties()
                        .durability(data.durability)
                        .attributes(PickaxeItem.createAttributes(
                                tier,
                                data.attackDamage - 1,
                                data.attackSpeed - 4
                        ))
        );
        this.initialGearData = data;
    }

    private GearData getGearData() {
        return GearLookup.getGearData(this, initialGearData);
    }

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        GearData data = getGearData();
        return data.durability > 0 ? data.durability : super.getMaxDamage(stack);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.literal(
                CustomSwordItem.buildName(getGearData(), GearLookup.getCurrentLang(), "pickaxe"));
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
