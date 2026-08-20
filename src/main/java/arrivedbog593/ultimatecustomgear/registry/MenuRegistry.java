package arrivedbog593.ultimatecustomgear.registry;

import arrivedbog593.ultimatecustomgear.menu.CustomContainerMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers the single menu type shared by every custom container.
 * <p>
 * ONE type for every size. The size is not part of the type: it travels in the
 * opening packet, which is what {@link IMenuTypeExtension} exists for. Vanilla's
 * MenuType carries no extra data, so a vanilla-built type would force either a
 * separate registration per size (unbounded, since sizes come from JSON) or a
 * client that has to guess how many slots to draw.
 * <p>
 * Unlike blocks and items, this does NOT depend on the loaded JSON — there is
 * exactly one entry, always. It is registered from the CustomGearMod
 * constructor alongside the other DeferredRegisters.
 */
public class MenuRegistry {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, "customgear");

    public static final DeferredHolder<MenuType<?>, MenuType<CustomContainerMenu>> CONTAINER_MENU =
            MENUS.register("container", () ->
                    IMenuTypeExtension.create(CustomContainerMenu::fromNetwork));

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
