package arrivedbog593.customgear;

import arrivedbog593.customgear.platform.Services;
import arrivedbog593.customgear.registry.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import arrivedbog593.customgear.platform.registration.RegistryEntry;
import arrivedbog593.customgear.platform.registration.RegistrationProvider;

public class CustomGearTab {

    public static final RegistrationProvider<CreativeModeTab> TABS =
            RegistrationProvider.get(Registries.CREATIVE_MODE_TAB);

    /**
     * Exists only to give the creative tab a face. Registered but never added to
     * displayItems, so it does not show up as content: the tab's icon has to be
     * an ItemStack, and every real item here comes from user JSON that may
     * legitimately be empty.
     */
    public static final RegistrationProvider<Item> ICON =
            RegistrationProvider.get(Registries.ITEM);

    public static final RegistryEntry<Item> TAB_ICON =
            ICON.register("tab_icon", () -> new Item(new Item.Properties()));

    public static void register() {
        TABS.register("customgear_tab", () -> Services.PLATFORM.creativeTabBuilder()
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
    }
}