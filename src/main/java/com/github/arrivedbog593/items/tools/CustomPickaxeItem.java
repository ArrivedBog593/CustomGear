package com.github.arrivedbog593.items.tools;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.items.CustomSwordItem;
import com.github.arrivedbog593.items.CustomTier;
import com.github.arrivedbog593.items.TooltipHelper;
import com.github.arrivedbog593.loader.GearRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomPickaxeItem extends PickaxeItem {

    private final GearData initialGearData;

    public CustomPickaxeItem(GearData data) {
        super(
                new CustomTier(data),
                new Properties()
                        .durability(data.durability)
                        .attributes(PickaxeItem.createAttributes(
                                new CustomTier(data),
                                data.attackDamage,
                                data.attackSpeed))
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
        String lang = "en_us";
        try {
            lang = net.minecraft.client.Minecraft.getInstance()
                    .getLanguageManager().getSelected();
        } catch (Exception ignored) {}
        return net.minecraft.network.chat.Component.literal(
                CustomSwordItem.buildName(getGearData(), lang, "pickaxe"));
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
