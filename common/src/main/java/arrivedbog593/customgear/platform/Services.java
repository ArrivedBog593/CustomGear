package arrivedbog593.customgear.platform;

import arrivedbog593.customgear.Constants;
import arrivedbog593.customgear.platform.registration.RegistrationProvider;
import arrivedbog593.customgear.platform.services.IAccessoryHelper;
import arrivedbog593.customgear.platform.services.IFluidHelper;
import arrivedbog593.customgear.platform.services.IMenuHelper;
import arrivedbog593.customgear.platform.services.INetworkHelper;
import arrivedbog593.customgear.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

// Java's ServiceLoader finds the implementation of an interface that the
// running loader's jar provides. Each loader module lists its own in
// src/main/resources/META-INF/services/<fully qualified interface name>, a
// text file holding the fully qualified name of the implementing class.
public final class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final RegistrationProvider.Factory REGISTRATION = load(RegistrationProvider.Factory.class);
    public static final INetworkHelper NETWORK = load(INetworkHelper.class);
    public static final IMenuHelper MENUS = load(IMenuHelper.class);
    public static final IFluidHelper FLUIDS = load(IFluidHelper.class);
    public static final IAccessoryHelper ACCESSORIES = load(IAccessoryHelper.class);

    private Services() {
    }

    public static <T> T load(Class<T> clazz) {
        // This class's loader, not the thread's: the mod's classes may not
        // be visible from the thread that first touches this class.
        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No service found for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
