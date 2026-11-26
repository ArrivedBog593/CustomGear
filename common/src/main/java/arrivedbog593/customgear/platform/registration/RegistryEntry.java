package arrivedbog593.customgear.platform.registration;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * One registered object, as handed back by {@link RegistrationProvider}.
 * <p>
 * On NeoForge the object exists only once the registry event has fired, so
 * {@link #get()} must not be called before that, exactly as with a
 * DeferredHolder. On Fabric it exists as soon as it is registered.
 */
public interface RegistryEntry<T> extends Supplier<T> {

    ResourceLocation getId();
}
