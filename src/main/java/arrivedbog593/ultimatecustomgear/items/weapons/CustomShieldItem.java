package arrivedbog593.ultimatecustomgear.items.weapons;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.util.GearLookup;
import arrivedbog593.ultimatecustomgear.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class CustomShieldItem extends ShieldItem {

    private final GearData initialGearData;

    public CustomShieldItem(GearData data, Item.Properties props) {
        super(buildProps(props, data));
        this.initialGearData = data;
    }

    private static Item.Properties buildProps(Item.Properties props, GearData data) {
        Item.Properties p = props.durability(data.durability);
        return data.fireResistant ? p.fireResistant() : p;
    }

    private GearData getGearData() {
        return GearLookup.getGearData(this, initialGearData);
    }

    // NO CLIENT RENDERER. Up to 1.21.1 a shield had to hand rendering over to a
    // BlockEntityWithoutLevelRenderer, because its model parent was
    // builtin/entity and nothing else could draw it. That class is gone: the
    // shape is now declared in the item MODEL as a "minecraft:special" entry
    // naming the vanilla shield renderer — see GearModelGenerator.

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        GearData data = getGearData();
        return data.durability > 0 ? data.durability : super.getMaxDamage(stack);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        GearData data = getGearData();
        return Component.literal(CustomSwordItem.buildName(data, GearLookup.getCurrentLang(), "shield"));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull net.minecraft.world.item.component.TooltipDisplay display,
                                @NotNull java.util.function.Consumer<Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipComponents, flag);
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, getGearData());
        if (!TooltipHelper.detailsShown() && TooltipHelper.hasDetails(getGearData())) {
            TooltipHelper.addDetailsHint(tooltipComponents);
        }
    }
}