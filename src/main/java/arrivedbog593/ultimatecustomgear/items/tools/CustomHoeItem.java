package arrivedbog593.ultimatecustomgear.items.tools;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.items.gear.CustomTier;
import arrivedbog593.ultimatecustomgear.items.weapons.CustomSwordItem;
import arrivedbog593.ultimatecustomgear.util.GearLookup;
import arrivedbog593.ultimatecustomgear.util.TooltipHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomHoeItem extends HoeItem {

    private final GearData initialGearData;

    public CustomHoeItem(GearData data) {
        this(data, new CustomTier(data));
    }

    private CustomHoeItem(GearData data, CustomTier tier) {
        super(
                tier,
                new Properties()
                        .durability(data.durability)
                        .attributes(HoeItem.createAttributes(
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
                CustomSwordItem.buildName(getGearData(), GearLookup.getCurrentLang(), "hoe"));
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
        TooltipHelper.addToolStatsTooltip(tooltipComponents, getGearData());
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        GearData data = getGearData();
        int radius = data.tillRadius;

        if (radius > 0 && !context.getLevel().isClientSide()) {
            BlockPos centerPos = context.getClickedPos();

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = centerPos.offset(x, 0, z);
                    BlockHitResult hit = new BlockHitResult(
                            Vec3.atCenterOf(pos),
                            Direction.UP,
                            pos,
                            false);
                    super.useOn(new UseOnContext(
                            context.getLevel(),
                            context.getPlayer(),
                            context.getHand(),
                            context.getItemInHand(),
                            hit));
                }
            }

            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }
}
