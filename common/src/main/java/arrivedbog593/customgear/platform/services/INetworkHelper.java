package arrivedbog593.customgear.platform.services;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Sending this mod's packets. Registering them is each loader's job, in its
 * own entry point: both register the payloads in
 * {@link arrivedbog593.customgear.network.CustomGearNetworking}.
 */
public interface INetworkHelper {

    /** Client only: sends a play-phase payload to the server. */
    void sendToServer(CustomPacketPayload payload);
}
