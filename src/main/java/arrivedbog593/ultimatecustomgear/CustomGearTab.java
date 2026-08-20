package arrivedbog593.ultimatecustomgear;

import arrivedbog593.ultimatecustomgear.registry.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CustomGearTab {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CustomGearMod.MOD_ID);

    /**
     * Exists only to give the creative tab a face. Registered but never added to
     * displayItems, so it does not show up as content: the tab's icon has to be
     * an ItemStack, and every real item here comes from user JSON that may
     * legitimately be empty.
     */
    public static final DeferredRegister<Item> ICON =
            DeferredRegister.create(BuiltInRegistries.ITEM, "customgear");

    public static final DeferredHolder<Item, Item> TAB_ICON =
            ICON.register("tab_icon", () -> new Item(new Item.Properties()));

    public static void register(IEventBus modEventBus) {
        TABS.register("customgear_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.customgear"))
                .icon(() -> new ItemStack(TAB_ICON.get()))
                .displayItems((params, output) -> {
                    GearRegistry.ITEMS.getEntries().forEach(item ->
                            output.accept(item.get()));
                    ItemRegistry.ITEMS.getEntries().forEach(item ->
                            output.accept(item.get()));
                    BlockRegistry.BLOCK_ITEMS.getEntries().forEach(item ->
                            output.accept(item.get()));
                    // Containers have their own item register. Leaving this out
                    // hides every container from the creative tab AND from JEI,
                    // with no error anywhere — those lists are built from tabs.
                    ContainerRegistry.ITEMS.getEntries().forEach(item ->
                            output.accept(item.get()));
                    FluidRegistry.FLUID_BUCKETS.getEntries().forEach(item ->
                            output.accept(item.get()));
                })
                .build());
        TABS.register(modEventBus);
        ICON.register(modEventBus);
    }
}