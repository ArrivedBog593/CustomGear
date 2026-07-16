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
        super(tier, buildProps(data, tier));
        this.initialGearData = data;
    }

    private static Properties buildProps(GearData data, CustomTier tier) {
        Properties p = new Properties()
                .durability(data.durability)
                .attributes(HoeItem.createAttributes(
                        tier,
                        data.attackDamage - 1,
                        data.attackSpeed - 4
                ));
        return data.fireResistant ? p.fireResistant() : p;
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
        TooltipHelper.addToolStatsTooltip(tooltipComponents, getGearData());
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, data);
        TooltipHelper.addTillRadiusTooltip(tooltipComponents, data);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        GearData data = getGearData();
        int radius = data.tillRadius;

        if (radius > 0 && !context.getLevel().isClientSide()) {
            BlockPos centerPos = context.getClickedPos();
            ItemStack stack = context.getItemInHand();

            // Till the clicked block FIRST, so if durability runs out
            // mid-area the block the player actually aimed at is always
            // the one that got tilled.
            boolean anySuccess = tillAt(context, centerPos);


            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x == 0 && z == 0) continue; // center already done

                    // Each tilled block costs 1 durability; a large radius can
                    // exceed what's left. When the hoe breaks, the in-hand
                    // stack becomes empty — stop instead of ghost-tilling.
                    if (stack.isEmpty()) {
                        return anySuccess ? InteractionResult.SUCCESS
                                : InteractionResult.PASS;
                    }

                    if (tillAt(context, centerPos.offset(x, 0, z))) {
                        anySuccess = true;
                    }
                }
            }

            return anySuccess ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        return super.useOn(context);
    }

    /** Delegates one till attempt to vanilla HoeItem at the given position. */
    private boolean tillAt(UseOnContext context, BlockPos pos) {
        BlockHitResult hit = new BlockHitResult(
                Vec3.atCenterOf(pos),
                Direction.UP,
                pos,
                false);
        InteractionResult result = super.useOn(new UseOnContext(
                context.getLevel(),
                context.getPlayer(),
                context.getHand(),
                context.getItemInHand(),
                hit));
        return result.consumesAction();
    }
}
