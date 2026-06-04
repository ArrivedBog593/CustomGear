package com.github.arrivedbog593.items.weapons;

import com.github.arrivedbog593.client.CustomShieldBEWLR;
import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.util.GearLookup;
import com.github.arrivedbog593.util.TooltipHelper;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class CustomShieldItem extends ShieldItem {

    private final GearData initialGearData;

    public CustomShieldItem(GearData data) {
        super(new Properties().durability(data.durability));
        this.initialGearData = data;
    }

    private GearData getGearData() {
        return GearLookup.getGearData(this, initialGearData);
    }

    /**
     * Registers the BEWLR so the shield renders its 3D model in hand and inventory.
     * The vanilla ShieldItem uses builtin/entity as its model parent, which requires
     * this renderer. We reuse the same BEWLR that vanilla uses for its own shield —
     * it reads the shield model from the shield_patterns atlas and renders it correctly
     * for any item that declares builtin/entity as its model parent.
     */
    @SuppressWarnings("removal")
    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            // Single instance shared across all renders of this item
            private final CustomShieldBEWLR renderer = new CustomShieldBEWLR();

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

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
                                @NotNull List<Component> tooltipComponents,
                                @NotNull net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipComponents, flag);
        TooltipHelper.addHeldEffectsTooltip(tooltipComponents, getGearData());
    }
}