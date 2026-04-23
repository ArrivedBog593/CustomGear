package com.github.arrivedbog593.items.gear.tools;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.items.gear.CustomSwordItem;
import com.github.arrivedbog593.items.gear.CustomTier;
import com.github.arrivedbog593.util.TooltipHelper;
import com.github.arrivedbog593.loader.GearRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomHoeItem extends HoeItem {

    private final GearData initialGearData;

    public CustomHoeItem(GearData data) {
        super(
                new CustomTier(data),
                new Properties()
                        .durability(data.durability)
                        .attributes(HoeItem.createAttributes(
                                new CustomTier(data),
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
                CustomSwordItem.buildName(getGearData(), CustomSwordItem.getCurrentLang(), "hoe"));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        GearData data = getGearData();
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, data);
        TooltipHelper.addTillRadiusTooltip(tooltipComponents, data);
    }

    @Override
    public net.minecraft.world.@NotNull InteractionResult useOn(
            net.minecraft.world.item.context.@NotNull UseOnContext context) {

        GearData data = getGearData();
        int radius = data.tillRadius > 0 ? data.tillRadius : 0;

        if (radius > 0 && !context.getLevel().isClientSide) {
            net.minecraft.core.BlockPos centerPos = context.getClickedPos();

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    net.minecraft.core.BlockPos pos = centerPos.offset(x, 0, z);
                    net.minecraft.world.phys.BlockHitResult hit =
                            new net.minecraft.world.phys.BlockHitResult(
                                    net.minecraft.world.phys.Vec3.atCenterOf(pos),
                                    net.minecraft.core.Direction.UP,
                                    pos,
                                    false);
                    super.useOn(new net.minecraft.world.item.context.UseOnContext(
                            context.getLevel(), context.getPlayer(),
                            context.getHand(), context.getItemInHand(), hit));
                }
            }

            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }
}
