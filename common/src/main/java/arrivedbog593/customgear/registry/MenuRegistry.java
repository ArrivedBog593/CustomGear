package arrivedbog593.customgear.registry;

import arrivedbog593.customgear.menu.CustomContainerMenu;
import arrivedbog593.customgear.network.ContainerOpenData;
import arrivedbog593.customgear.platform.Services;
import arrivedbog593.customgear.platform.registration.RegistrationProvider;
import arrivedbog593.customgear.platform.registration.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

/**
 * Registers the single menu type shared by every custom container.
 * <p>
 * ONE type for every size. The size is not part of the type: it travels in the
 * opening packet as {@link ContainerOpenData}, which each loader carries in its
 * own way (see IMenuHelper). Vanilla's MenuType carries no extra data, so a
 * vanilla-built type would force either a separate registration per size
 * (unbounded, since sizes come from JSON) or a client that has to guess how
 * many slots to draw.
 * <p>
 * Unlike blocks and items, this does NOT depend on the loaded JSON — there is
 * exactly one entry, always.
 */
public class MenuRegistry {

    public static final RegistrationProvider<MenuType<?>> MENUS =
            RegistrationProvider.get(Registries.MENU);

    public static final RegistryEntry<MenuType<CustomContainerMenu>> CONTAINER_MENU =
            MENUS.register("container", () -> Services.MENUS.createMenuType(
                    CustomContainerMenu::fromNetwork, ContainerOpenData.STREAM_CODEC));

    /** Loads this class, which registers the menu type. */
    public static void register() {
    }
}
