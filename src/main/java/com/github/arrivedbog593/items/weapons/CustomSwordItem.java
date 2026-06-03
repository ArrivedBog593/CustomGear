package com.github.arrivedbog593.items.weapons;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.items.gear.CustomTier;
import com.github.arrivedbog593.loader.GearRegistry;
import com.github.arrivedbog593.util.TooltipHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CustomSwordItem extends SwordItem {

    private final GearData initialGearData;

    public CustomSwordItem(GearData data) {
        this(data, new CustomTier(data));
    }

    private CustomSwordItem(GearData data, CustomTier tier) {
        super(
                tier,
                new Item.Properties()
                        .durability(data.durability)
                        .attributes(SwordItem.createAttributes(
                                tier,
                                (int) data.attackDamage-1,
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
                buildName(getGearData(), getCurrentLang(), "sword"));
    }

    /**
     * Returns the currently selected client language, or "en_us" as a safe fallback
     * when running on a dedicated server where Minecraft client classes are absent.
     */
    public static String getCurrentLang() {
        if (FMLEnvironment.dist.isClient()) {
            try {
                return net.minecraft.client.Minecraft.getInstance()
                        .getLanguageManager().getSelected();
            } catch (Exception ignored) {}
        }
        return "en_us";
    }

    public static String buildName(GearData data, String lang, String toolType) {
        // First try weaponNames (for weapon_set)
        if (data.weaponNames != null) {
            Map<String, String> namesForLang = data.weaponNames.getOrDefault(lang,
                    data.weaponNames.get("en_us"));
            if (namesForLang != null && namesForLang.containsKey(toolType)) {
                return namesForLang.get(toolType);
            }
        }

        // Then try toolNames (for tool_set)
        if (data.toolNames != null) {
            Map<String, String> namesForLang = data.toolNames.getOrDefault(lang,
                    data.toolNames.get("en_us"));
            if (namesForLang != null && namesForLang.containsKey(toolType)) {
                return namesForLang.get(toolType);
            }
        }

        // Finally try name (for individual items)
        if (data.name != null) {
            return data.name.getOrDefault(lang, data.name.getOrDefault("en_us", data.id));
        }

        return "Unknown";
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<net.minecraft.network.chat.Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, getGearData());
    }
}
