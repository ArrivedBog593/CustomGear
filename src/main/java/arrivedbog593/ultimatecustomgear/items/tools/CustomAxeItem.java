package arrivedbog593.ultimatecustomgear.items.tools;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.items.weapons.CustomSwordItem;
import arrivedbog593.ultimatecustomgear.util.GearLookup;
import arrivedbog593.ultimatecustomgear.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A axe built from JSON.
 * <p>
 * NOT a subclass of the vanilla axe: those classes no longer exist. What made
 * an item a axe is now a set of data components, applied by
 * {@code Item.Properties.axe(...)} — see {@link CustomToolItem}.
 */
public class CustomAxeItem extends Item {

    private final GearData initialGearData;

    public CustomAxeItem(GearData data, Item.Properties props) {
        super(CustomToolItem.applyToolProperties(props, data, "axe"));
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
                CustomSwordItem.buildName(getGearData(), GearLookup.getCurrentLang(), "axe"));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull TooltipDisplay display,
                                @NotNull Consumer<Component> builder,
                                @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, builder, tooltipFlag);
        GearData data = getGearData();
        TooltipHelper.addToolStatsTooltip(builder, data);
        TooltipHelper.addHeldEffectsTooltip(builder, data);
        if (!TooltipHelper.detailsShown() && TooltipHelper.hasDetails(data)) {
            TooltipHelper.addDetailsHint(builder);
        }
    }
}
