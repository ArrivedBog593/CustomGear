package com.github.arrivedbog593;

import com.github.arrivedbog593.loader.BlockRegistry;
import com.github.arrivedbog593.loader.FluidRegistry;
import com.github.arrivedbog593.loader.GearRegistry;
import com.github.arrivedbog593.loader.ItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CustomGearTab {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CustomGearMod.MOD_ID);

    public static void register(IEventBus modEventBus) {
        TABS.register("customgear_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.customgear"))
                .icon(() -> GearRegistry.ITEMS.getEntries().stream()
                        .findFirst()
                        .map(item -> new ItemStack(item.get()))
                        .orElse(ItemStack.EMPTY))
                .displayItems((params, output) -> {
                    GearRegistry.ITEMS.getEntries().forEach(item ->
                            output.accept(item.get()));
                    ItemRegistry.ITEMS.getEntries().forEach(item ->
                            output.accept(item.get()));
                    BlockRegistry.BLOCK_ITEMS.getEntries().forEach(item ->
                            output.accept(item.get()));
                    FluidRegistry.FLUID_BUCKETS.getEntries().forEach(item ->
                            output.accept(item.get()));
                })
                .build());
        TABS.register(modEventBus);
    }
}