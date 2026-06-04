package com.github.arrivedbog593.items.weapons;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.util.GearLookup;
import com.github.arrivedbog593.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomBowItem extends BowItem {

    private final GearData initialGearData;

    public CustomBowItem(GearData data) {
        super(new Properties().durability(data.durability));
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
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        GearData data = getGearData();
        float chargeSpeed = data.chargeSpeed > 0 ? data.chargeSpeed : 1.0f;
        return (int) (72000 / chargeSpeed);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        GearData data = getGearData();
        return Component.literal(CustomSwordItem.buildName(data, GearLookup.getCurrentLang(), "bow"));
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
