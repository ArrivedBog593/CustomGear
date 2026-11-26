package arrivedbog593.customgear.fabric;

import arrivedbog593.customgear.network.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Registers the payloads and the handshake task the Fabric way. What they do
 * is in CustomGearNetworking.
 */
final class FabricNetworking {

    private FabricNetworking() {}

    static void init() {
        PayloadTypeRegistry.configurationS2C().register(HashCheckPayload.TYPE, HashCheckPayload.STREAM_CODEC);
        PayloadTypeRegistry.configurationC2S().register(HashCheckAckPayload.TYPE, HashCheckAckPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(TransferItemsPayload.TYPE, TransferItemsPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SortContainerPayload.TYPE, SortContainerPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SortModePayload.TYPE, SortModePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(OpenBackpackPayload.TYPE, OpenBackpackPayload.STREAM_CODEC);

        // Fabric runs play handlers on the server thread already.
        ServerPlayNetworking.registerGlobalReceiver(TransferItemsPayload.TYPE, (payload, context) ->
                CustomGearNetworking.handleTransfer(payload, context.player()));
        ServerPlayNetworking.registerGlobalReceiver(SortContainerPayload.TYPE, (payload, context) ->
                CustomGearNetworking.handleSort(payload, context.player()));
        ServerPlayNetworking.registerGlobalReceiver(SortModePayload.TYPE, (payload, context) ->
                CustomGearNetworking.handleSortMode(payload, context.player()));
        ServerPlayNetworking.registerGlobalReceiver(OpenBackpackPayload.TYPE, (payload, context) ->
                CustomGearNetworking.handleOpenBackpack(payload, context.player()));

        // The handshake. A client that cannot receive it does not have the mod
        // at all, and Fabric's registry sync refuses it for the missing items.
        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            if (!CustomGearNetworking.isHandshakeEnabled()) return;
            if (ServerConfigurationNetworking.canSend(handler, HashCheckPayload.TYPE)) {
                handler.addTask(new HashCheckTask());
            }
        });
        ServerConfigurationNetworking.registerGlobalReceiver(HashCheckAckPayload.TYPE, (payload, context) -> {
            Component reason = CustomGearNetworking.onHashAck(payload);
            if (reason != null) {
                context.networkHandler().disconnect(reason);
                return;
            }
            context.networkHandler().completeTask(CustomGearNetworking.HASH_CHECK_TASK);
        });
    }

    /**
     * Runs on the server for every connecting client during the configuration
     * phase (only in ENFORCE/WARN modes). The task stays pending, holding the
     * login, until the client's answer completes it or a side disconnects.
     */
    private static final class HashCheckTask implements ConfigurationTask {

        @Override
        public void start(@NotNull Consumer<Packet<?>> sender) {
            sender.accept(ServerConfigurationNetworking.createS2CPacket(
                    CustomGearNetworking.createHashCheck()));
        }

        @Override
        public @NotNull Type type() {
            return CustomGearNetworking.HASH_CHECK_TASK;
        }
    }
}
