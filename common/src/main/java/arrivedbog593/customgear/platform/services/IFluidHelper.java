package arrivedbog593.customgear.platform.services;

import arrivedbog593.customgear.items.fluids.CustomFluid;

/**
 * What a fluid needs from the loader. NeoForge gives every fluid a FluidType
 * (its name, light, swimming and rendering); Fabric needs nothing beyond the
 * vanilla fluid.
 */
public interface IFluidHelper {

    /**
     * Called once per fluid while it registers, before its source and flowing
     * variants are created. A loader that needs more registry entries per
     * fluid registers them here and keeps them in
     * {@link CustomFluid.Properties#platformData}.
     */
    void prepare(CustomFluid.Properties properties);

    CustomFluid.Source createSource(CustomFluid.Properties properties);

    CustomFluid.Flowing createFlowing(CustomFluid.Properties properties);
}
