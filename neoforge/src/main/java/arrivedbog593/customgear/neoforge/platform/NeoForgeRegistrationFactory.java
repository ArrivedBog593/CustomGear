package arrivedbog593.customgear.neoforge.platform;

import arrivedbog593.customgear.platform.registration.RegistrationProvider;
import arrivedbog593.customgear.platform.registration.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * A DeferredRegister behind each provider, attached to the mod's event bus as
 * soon as it is created. Providers are only created while the mod is being
 * constructed, which is when the bus accepts them.
 */
public class NeoForgeRegistrationFactory implements RegistrationProvider.Factory {

    @Override
    public <T> RegistrationProvider<T> create(ResourceKey<? extends Registry<T>> registry, String modId) {
        ModContainer container = ModList.get().getModContainerById(modId).orElseThrow(
                () -> new IllegalStateException("No mod container for " + modId));
        IEventBus bus = container.getEventBus();
        if (bus == null) {
            throw new IllegalStateException("Mod " + modId + " has no event bus");
        }
        DeferredRegister<T> register = DeferredRegister.create(registry, modId);
        register.register(bus);
        return new Provider<>(register);
    }

    private static final class Provider<T> implements RegistrationProvider<T> {

        private final DeferredRegister<T> register;
        private final List<RegistryEntry<T>> entries = new ArrayList<>();
        private final List<RegistryEntry<T>> view = Collections.unmodifiableList(entries);

        Provider(DeferredRegister<T> register) {
            this.register = register;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <I extends T> RegistryEntry<I> register(String name, Supplier<? extends I> supplier) {
            DeferredHolder<T, I> holder = register.register(name, supplier);
            Entry<T, I> entry = new Entry<>(holder);
            entries.add((RegistryEntry<T>) entry);
            return entry;
        }

        @Override
        public List<RegistryEntry<T>> getEntries() {
            return view;
        }
    }

    private record Entry<T, I extends T>(DeferredHolder<T, I> holder) implements RegistryEntry<I> {

        @Override
        public I get() {
            return holder.get();
        }

        @Override
        public ResourceLocation getId() {
            return holder.getId();
        }
    }
}
