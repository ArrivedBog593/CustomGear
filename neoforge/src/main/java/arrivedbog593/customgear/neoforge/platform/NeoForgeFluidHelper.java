package arrivedbog593.customgear.neoforge.platform;

import arrivedbog593.customgear.Constants;
import arrivedbog593.customgear.data.FluidData;
import arrivedbog593.customgear.items.fluids.CustomFluid;
import arrivedbog593.customgear.platform.services.IFluidHelper;
import arrivedbog593.customgear.registry.FluidRegistry;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

/**
 * Gives every fluid the FluidType NeoForge requires: its name, its light, and
 * the water-like swimming and drowning a default FluidType has. How it draws
 * is attached on the client (NeoForgeClient).
 */
public class NeoForgeFluidHelper implements IFluidHelper {

    private DeferredRegister<FluidType> fluidTypes;

    @Override
    public void prepare(CustomFluid.Properties properties) {
        FluidData data = properties.data;
        properties.platformData = fluidTypes().register(data.id, () -> buildFluidType(data));
    }

    @Override
    public CustomFluid.Source createSource(CustomFluid.Properties properties) {
        return new CustomFluid.Source(properties) {
            @Override
            public @NotNull FluidType getFluidType() {
                return typeOf(properties);
            }
        };
    }

    @Override
    public CustomFluid.Flowing createFlowing(CustomFluid.Properties properties) {
        return new CustomFluid.Flowing(properties) {
            @Override
            public @NotNull FluidType getFluidType() {
                return typeOf(properties);
            }
        };
    }

    public static FluidType typeOf(CustomFluid.Properties properties) {
        return (FluidType) properties.platformData.get();
    }

    /** Created on first use, while the mod is being constructed. */
    private DeferredRegister<FluidType> fluidTypes() {
        if (fluidTypes == null) {
            IEventBus bus = ModList.get().getModContainerById(Constants.MOD_ID).orElseThrow().getEventBus();
            if (bus == null) {
                throw new IllegalStateException("Mod " + Constants.MOD_ID + " has no event bus");
            }
            fluidTypes = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Constants.MOD_ID);
            fluidTypes.register(bus);
        }
        return fluidTypes;
    }

    private static FluidType buildFluidType(FluidData data) {
        FluidType.Properties props = FluidType.Properties.create()
                .descriptionId("fluid.customgear." + data.id);

        if (data.lightLevel > 0) {
            props.lightLevel(Math.clamp(data.lightLevel, 0, 15));
        }

        return new FluidType(props) {
            @Override
            public @NotNull Component getDescription() {
                return Component.literal(FluidRegistry.resolveFluidName(data));
            }
        };
    }
}
