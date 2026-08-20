package arrivedbog593.ultimatecustomgear.items.tools;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.items.gear.CustomTier;
import arrivedbog593.ultimatecustomgear.items.weapons.CustomSwordItem;
import arrivedbog593.ultimatecustomgear.util.GearLookup;
import arrivedbog593.ultimatecustomgear.util.TooltipHelper;
import net.minecraft.network.chat.Component;
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
        super(tier, buildProps(data, tier));
        this.initialGearData = data;
    }

    private static Properties buildProps(GearData data, CustomTier tier) {
        Properties p = new Properties()
                .durability(data.durability)
                .attributes(ShovelItem.createAttributes(
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
                CustomSwordItem.buildName(getGearData(), GearLookup.getCurrentLang(), "shovel"));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        TooltipHelper.addToolStatsTooltip(tooltipComponents, getGearData());
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, getGearData());
        if (!TooltipHelper.detailsShown() && TooltipHelper.hasDetails(getGearData())) {
            TooltipHelper.addDetailsHint(tooltipComponents);
        }
    }
}
