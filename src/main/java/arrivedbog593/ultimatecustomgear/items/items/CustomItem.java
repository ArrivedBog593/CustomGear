package arrivedbog593.ultimatecustomgear.items.items;

import arrivedbog593.ultimatecustomgear.data.ItemData;
import arrivedbog593.ultimatecustomgear.registry.ItemRegistry;
import arrivedbog593.ultimatecustomgear.util.GearLookup;
import arrivedbog593.ultimatecustomgear.util.TooltipHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Generic item built from an ItemData JSON.
 * Items do not emit light in vanilla.
 * <p>
 * JSON properties:
 *  - texture / model → assets
 *  - names → translatable name
 */
public class CustomItem extends Item {

    private final ItemData itemData;

    public CustomItem(ItemData data, Item.Properties props) {
        super(buildProps(props, data));
        this.itemData = data;
    }

    private static Item.Properties buildProps(Item.Properties props, ItemData data) {
        return data.fireResistant ? props.fireResistant() : props;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.literal(resolveName(itemData));
    }

    public static String resolveName(ItemData data) {
        if (data.names == null || data.names.isEmpty()) return data.id;
        String lang = GearLookup.getCurrentLang();
        return data.names.getOrDefault(lang,
                data.names.getOrDefault("en_us", data.id));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull net.minecraft.world.item.component.TooltipDisplay display,
                                @NotNull java.util.function.Consumer<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        ItemData live = ItemRegistry.ITEM_MAP.get(BuiltInRegistries.ITEM.getKey(this));
        TooltipHelper.appendMobDrops(live != null ? live : itemData, tooltip);
        if (!TooltipHelper.detailsShown() && TooltipHelper.hasDetails(itemData)) {
            TooltipHelper.addDetailsHint(tooltip);
        }
    }
}