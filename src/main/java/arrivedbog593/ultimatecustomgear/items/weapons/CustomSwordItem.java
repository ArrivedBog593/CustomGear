package arrivedbog593.ultimatecustomgear.items.weapons;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.items.gear.CustomTier;
import arrivedbog593.ultimatecustomgear.util.GearLookup;
import arrivedbog593.ultimatecustomgear.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CustomSwordItem extends SwordItem {

    private final GearData initialGearData;

    public CustomSwordItem(GearData data) {
        this(data, new CustomTier(data));
    }

    private CustomSwordItem(GearData data, CustomTier tier) {
        super(tier, buildProps(data, tier));
        this.initialGearData = data;
    }

    private static Properties buildProps(GearData data, CustomTier tier) {
        Properties p = new Properties()
                .durability(data.durability)
                .attributes(SwordItem.createAttributes(
                        tier,
                        (int) data.attackDamage - 1,
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
                                @NotNull List<Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, getGearData());
    }
}
