package arrivedbog593.customgear.neoforge;

import arrivedbog593.customgear.network.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Registers the payloads and the handshake task the NeoForge way. What they
 * do is in CustomGearNetworking.
 */
final class NeoForgeNetworking {

    private NeoForgeNetworking() {}

    static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(CustomGearNetworking.PROTOCOL_VERSION);

        registrar.configurationToClient(HashCheckPayload.TYPE, HashCheckPayload.STREAM_CODEC,
                (payload, context) -> {
                    Component reason = CustomGearNetworking.onHashCheck(payload);
                    if (reason != null) {
                        context.disconnect(reason);
                        return;
                    }
                    context.reply(CustomGearNetworking.createHashAck());
                });

        registrar.configurationToServer(HashCheckAckPayload.TYPE, HashCheckAckPayload.STREAM_CODEC,
                (payload, context) -> {
                    Component reason = CustomGearNetworking.onHashAck(payload);
                    if (reason != null) {
                        context.disconnect(reason);
                        return;
                    }
                    context.finishCurrentTask(CustomGearNetworking.HASH_CHECK_TASK);
                });

        registrar.playToServer(TransferItemsPayload.TYPE, TransferItemsPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        CustomGearNetworking.handleTransfer(payload, context.player())));

        registrar.playToServer(SortContainerPayload.TYPE, SortContainerPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        CustomGearNetworking.handleSort(payload, context.player())));

        registrar.playToServer(SortModePayload.TYPE, SortModePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        CustomGearNetworking.handleSortMode(payload, context.player())));

        registrar.playToServer(OpenBackpackPayload.TYPE, OpenBackpackPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        CustomGearNetworking.handleOpenBackpack(context.player())));
    }

    static void registerConfigurationTasks(RegisterConfigurationTasksEvent event) {
        if (CustomGearNetworking.isHandshakeEnabled()) {
            event.register(new HashCheckTask());
        }
    }

    /**
     * Runs on the server for every connecting client during the configuration
     * phase (only registered in ENFORCE/WARN modes). The task stays pending,
     * holding the login, until the client's answer completes it or a side
     * disconnects.
     */
    private static final class HashCheckTask implements ICustomConfigurationTask {

        @Override
        public void run(@NotNull Consumer<CustomPacketPayload> sender) {
            sender.accept(CustomGearNetworking.createHashCheck());
        }

        @Override
        public @NotNull ConfigurationTask.Type type() {
            return CustomGearNetworking.HASH_CHECK_TASK;
        }
    }
}
