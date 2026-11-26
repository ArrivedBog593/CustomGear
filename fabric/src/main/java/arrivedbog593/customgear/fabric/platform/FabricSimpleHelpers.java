package arrivedbog593.customgear.fabric.platform;

import arrivedbog593.customgear.items.fluids.CustomFluid;
import arrivedbog593.customgear.platform.services.IFluidHelper;
import arrivedbog593.customgear.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * The Fabric services with nothing loader-specific to say.
 */
public final class FabricSimpleHelpers {

    private FabricSimpleHelpers() {}

    public static final class Network implements INetworkHelper {
        @Override
        public void sendToServer(CustomPacketPayload payload) {
            ClientPlayNetworking.send(payload);
        }
    }

    /** Vanilla fluids are all Fabric needs; rendering is attached on the client. */
    public static final class Fluids implements IFluidHelper {
        @Override
        public void prepare(CustomFluid.Properties properties) {
        }

        @Override
        public CustomFluid.Source createSource(CustomFluid.Properties properties) {
            return new CustomFluid.Source(properties);
        }

        @Override
        public CustomFluid.Flowing createFlowing(CustomFluid.Properties properties) {
            return new CustomFluid.Flowing(properties);
        }
    }
}
