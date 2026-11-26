package arrivedbog593.customgear.platform.registration;

import arrivedbog593.customgear.Constants;
import arrivedbog593.customgear.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.function.Supplier;

/**
 * Registers objects into one vanilla registry, under this mod's namespace,
 * the way the running loader wants it: a DeferredRegister on NeoForge, a
 * direct Registry.register on Fabric.
 * <p>
 * The supplier may run right away (Fabric) or later (NeoForge), so it must
 * not assume either: everything it reads from another entry goes through that
 * entry's {@link RegistryEntry#get()} inside the supplier, never outside it.
 * The entries are created in registration order on both loaders, so an entry
 * may read one registered before it.
 */
public interface RegistrationProvider<T> {

    static <T> RegistrationProvider<T> get(ResourceKey<? extends Registry<T>> registry) {
        return Services.REGISTRATION.create(registry, Constants.MOD_ID);
    }

    <I extends T> RegistryEntry<I> register(String name, Supplier<? extends I> supplier);

    /** Every entry registered through this provider, in registration order. */
    List<RegistryEntry<T>> getEntries();

    /**
     * How each loader creates a provider; found by {@link Services}.
     */
    interface Factory {
        <T> RegistrationProvider<T> create(ResourceKey<? extends Registry<T>> registry, String modId);
    }
}
