package arrivedbog593.customgear.fabric.platform;

import arrivedbog593.customgear.platform.registration.RegistrationProvider;
import arrivedbog593.customgear.platform.registration.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Registers each object as soon as it is handed over: on Fabric the
 * registries stay open until the server starts, so there is nothing to defer.
 */
public class FabricRegistrationFactory implements RegistrationProvider.Factory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> RegistrationProvider<T> create(ResourceKey<? extends Registry<T>> registry, String modId) {
        Registry<T> target = (Registry<T>) BuiltInRegistries.REGISTRY.get(registry.location());
        if (target == null) {
            throw new IllegalStateException("No registry " + registry.location());
        }
        return new Provider<>(target, modId);
    }

    private static final class Provider<T> implements RegistrationProvider<T> {

        private final Registry<T> registry;
        private final String modId;
        private final List<RegistryEntry<T>> entries = new ArrayList<>();
        private final List<RegistryEntry<T>> view = Collections.unmodifiableList(entries);

        Provider(Registry<T> registry, String modId) {
            this.registry = registry;
            this.modId = modId;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <I extends T> RegistryEntry<I> register(String name, Supplier<? extends I> supplier) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, name);
            I value = Registry.register(registry, id, supplier.get());
            Entry<I> entry = new Entry<>(id, value);
            entries.add((RegistryEntry<T>) entry);
            return entry;
        }

        @Override
        public List<RegistryEntry<T>> getEntries() {
            return view;
        }
    }

    private record Entry<I>(ResourceLocation id, I value) implements RegistryEntry<I> {

        @Override
        public I get() {
            return value;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }
    }
}
