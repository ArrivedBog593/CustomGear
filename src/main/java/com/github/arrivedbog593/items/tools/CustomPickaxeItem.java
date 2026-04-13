package com.github.arrivedbog593.items.tools;

import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.items.CustomSwordItem;
import com.github.arrivedbog593.items.CustomTier;
import com.github.arrivedbog593.items.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomPickaxeItem extends PickaxeItem {

    private final GearData gearData;

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
        this.gearData = data;
    }

    public GearData getGearData() { return gearData; }

    @Override
    public @NotNull net.minecraft.network.chat.Component getName(@NotNull ItemStack stack) {
        String lang = "en_us";
        try {
            lang = net.minecraft.client.Minecraft.getInstance()
                    .getLanguageManager().getSelected();
        } catch (Exception ignored) {}
        return net.minecraft.network.chat.Component.literal(
                CustomSwordItem.buildName(gearData, lang, "pickaxe"));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, gearData);
    }
}