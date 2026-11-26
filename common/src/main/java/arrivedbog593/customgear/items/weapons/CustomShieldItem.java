package arrivedbog593.customgear.items.weapons;

import arrivedbog593.customgear.items.DynamicDurability;
import arrivedbog593.customgear.data.GearData;
import arrivedbog593.customgear.util.GearLookup;
import arrivedbog593.customgear.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomShieldItem extends ShieldItem implements DynamicDurability {

    private final GearData initialGearData;

    public CustomShieldItem(GearData data) {
        super(buildProps(data));
        this.initialGearData = data;
    }

    private static Properties buildProps(GearData data) {
        Properties p = new Properties().durability(data.durability);
        return data.fireResistant ? p.fireResistant() : p;
    }

    private GearData getGearData() {
        return GearLookup.getGearData(this, initialGearData);
    }

    @Override
    public int dynamicMaxDamage(@NotNull ItemStack stack) {
        GearData data = getGearData();
        return Math.max(data.durability, 0);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        GearData data = getGearData();
        return Component.literal(CustomSwordItem.buildName(data, GearLookup.getCurrentLang(), "shield"));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipComponents, flag);
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, getGearData());
        if (!TooltipHelper.detailsShown() && TooltipHelper.hasDetails(getGearData())) {
            TooltipHelper.addDetailsHint(tooltipComponents);
        }
    }
}