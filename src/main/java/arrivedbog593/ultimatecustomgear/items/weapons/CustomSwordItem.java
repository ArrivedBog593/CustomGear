package arrivedbog593.ultimatecustomgear.items.weapons;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.items.gear.CustomTier;
import arrivedbog593.ultimatecustomgear.util.GearLookup;
import arrivedbog593.ultimatecustomgear.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

/**
 * A sword built from JSON.
 * <p>
 * NOT a subclass of the vanilla sword: that class no longer exists. What makes
 * an item a sword is now the set of components applied by
 * {@code Item.Properties.sword(...)}.
 */
public class CustomSwordItem extends Item {

    private final GearData initialGearData;

    public CustomSwordItem(GearData data, Item.Properties props) {
        super(buildProps(props, data));
        this.initialGearData = data;
    }

    /**
     * Same baseline conversion the tools use: vanilla adds the player's own 1.0
     * damage and the material's bonus, so the JSON's totals arrive here minus
     * one and minus four.
     */
    private static Item.Properties buildProps(Item.Properties props, GearData data) {
        Item.Properties p = props.sword(
                CustomTier.of(data),
                data.attackDamage - 1,
                data.attackSpeed - 4);
        if (data.durability > 0) p = p.durability(data.durability);
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
        return Component.literal(buildName(getGearData(), GearLookup.getCurrentLang(), "sword"));
    }

    /**
     * @deprecated Use {@link GearLookup#getCurrentLang()} directly.
     * Kept for compatibility with other item classes that still call this method.
     */
    @Deprecated
    public static String getCurrentLang() {
        return GearLookup.getCurrentLang();
    }

    public static String buildName(GearData data, String lang, String toolType) {
        // 1. weapon_set names
        if (data.weaponNames != null) {
            Map<String, String> namesForLang = data.weaponNames.getOrDefault(lang,
                    data.weaponNames.get("en_us"));
            if (namesForLang != null && namesForLang.containsKey(toolType)) {
                return namesForLang.get(toolType);
            }
        }
        // 2. tool_set names
        if (data.toolNames != null) {
            Map<String, String> namesForLang = data.toolNames.getOrDefault(lang,
                    data.toolNames.get("en_us"));
            if (namesForLang != null && namesForLang.containsKey(toolType)) {
                return namesForLang.get(toolType);
            }
        }
        // 3. individual item names
        if (data.names != null) {
            return data.names.getOrDefault(lang, data.names.getOrDefault("en_us", data.id));
        }
        return data.id != null ? data.id : "unknown";
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull TooltipDisplay display,
                                @NotNull Consumer<Component> builder,
                                @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, builder, tooltipFlag);
        GearData data = getGearData();
        TooltipHelper.addHeldEffectsTooltip(builder, data);
        if (!TooltipHelper.detailsShown() && TooltipHelper.hasDetails(data)) {
            TooltipHelper.addDetailsHint(builder);
        }
    }
}
