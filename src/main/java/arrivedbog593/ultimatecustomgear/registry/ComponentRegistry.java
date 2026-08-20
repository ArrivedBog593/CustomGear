package arrivedbog593.ultimatecustomgear.registry;

import arrivedbog593.ultimatecustomgear.data.components.ContainerContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers this mod's own data components.
 * <p>
 * Like MenuRegistry, this does NOT depend on the loaded JSON: the set of
 * components is fixed. Registered from the CustomGearMod constructor alongside
 * the other DeferredRegisters.
 */
public class ComponentRegistry {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "customgear");

    /**
     * A container's inventory, carried inside its ItemStack.
     * <p>
     * cacheEncoding() is deliberately NOT used: these payloads are large and
     * change every time the player moves a single item, so caching the encoded
     * form would keep a second copy of the whole inventory alive for nothing.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ContainerContents>>
            CONTAINER_CONTENTS = COMPONENTS.register("container_contents", () ->
                    DataComponentType.<ContainerContents>builder()
                            .persistent(ContainerContents.CODEC)
                            .networkSynchronized(ContainerContents.STREAM_CODEC)
                            .build());

    public static void register(IEventBus modEventBus) {
        COMPONENTS.register(modEventBus);
    }
}
