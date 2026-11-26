package arrivedbog593.customgear.registry;

import arrivedbog593.customgear.data.components.ContainerContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import arrivedbog593.customgear.platform.registration.RegistryEntry;
import arrivedbog593.customgear.platform.registration.RegistrationProvider;

/**
 * Registers this mod's own data components.
 * <p>
 * Like MenuRegistry, this does NOT depend on the loaded JSON: the set of
 * components is fixed.
 */
public class ComponentRegistry {

    public static final RegistrationProvider<DataComponentType<?>> COMPONENTS =
            RegistrationProvider.get(Registries.DATA_COMPONENT_TYPE);

    /**
     * A container's inventory, carried inside its ItemStack.
     * <p>
     * cacheEncoding() is deliberately NOT used: these payloads are large and
     * change every time the player moves a single item, so caching the encoded
     * form would keep a second copy of the whole inventory alive for nothing.
     */
    public static final RegistryEntry<DataComponentType<ContainerContents>>
            CONTAINER_CONTENTS = COMPONENTS.register("container_contents", () ->
                    DataComponentType.<ContainerContents>builder()
                            .persistent(ContainerContents.CODEC)
                            .networkSynchronized(ContainerContents.STREAM_CODEC)
                            .build());

    /** Loads this class, which registers the components. */
    public static void register() {
    }
}
