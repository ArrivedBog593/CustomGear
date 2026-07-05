package arrivedbog593.ultimatecustomgear.network;

import arrivedbog593.ultimatecustomgear.config.CustomGearConfig;
import arrivedbog593.ultimatecustomgear.config.CustomGearConfig.HandshakeMode;
import arrivedbog593.ultimatecustomgear.util.ContentHasher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Configuration-phase handshake that verifies the joining client LOADED the
 * same ultimatecustomgear content as the server.
 * <p>
 * The server's config (content_handshake_mode) decides what a mismatch means:
 * <ul>
 *   <li>ENFORCE (default) — the client is disconnected with a message
 *       showing both hashes and how to fix it.</li>
 *   <li>WARN — the client is let in; the server logs the mismatch and the
 *       client shows itself a chat warning after joining.</li>
 *   <li>OFF — the configuration task isn't registered; no check happens.</li>
 * </ul>
 * The mode travels INSIDE the payload: the client obeys what the server
 * sends, so a client's local config can never weaken a server's enforcement.
 * <p>
 * Both sides compare {@link ContentHasher#current()} — the hash captured when
 * content was loaded into memory — NOT the disk state at connection time
 * (item attributes bake at registration; editing a JSON after startup changes
 * the disk but not the running game).
 */
public final class CustomGearNetworking {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    /**
     * Payload protocol version — bumped to "2" when the enforce flag was
     * added to HashCheckPayload. Clients and servers on different protocol
     * versions are rejected by NeoForge's channel negotiation with a clear
     * version-mismatch message instead of a codec crash.
     */
    private static final String PROTOCOL_VERSION = "2";

    /**
     * Set on the CLIENT when it joins a WARN-mode server with mismatching
     * files. Client-side join handling reads and clears it to show a chat
     * warning (see ClientSetup). Volatile: written on the network thread,
     * read on the client thread.
     */
    public static volatile boolean pendingMismatchWarning = false;

    private CustomGearNetworking() {}

    // ── Registration (both called from CustomGearMod on the MOD event bus) ───

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.configurationToClient(
                HashCheckPayload.TYPE,
                HashCheckPayload.STREAM_CODEC,
                CustomGearNetworking::handleHashCheckOnClient);

        registrar.configurationToServer(
                HashCheckAckPayload.TYPE,
                HashCheckAckPayload.STREAM_CODEC,
                CustomGearNetworking::handleAckOnServer);
    }

    public static void registerConfigurationTasks(final RegisterConfigurationTasksEvent event) {
        // OFF mode: skip the whole handshake — don't even register the task.
        if (CustomGearConfig.HANDSHAKE_MODE.get() == HandshakeMode.OFF) {
            LOGGER.debug("[CustomGear] Content handshake disabled by config (OFF)");
            return;
        }
        event.register(new HashCheckTask());
    }

    // ── Handlers ──────────────────────────────────────────────────────────────

    /** CLIENT side: compare the server's hash against what WE loaded at startup. */
    private static void handleHashCheckOnClient(HashCheckPayload payload, IPayloadContext context) {
        String localHash = ContentHasher.current();

        if (!payload.serverHash().equals(localHash)) {
            if (payload.enforce()) {
                LOGGER.warn("[CustomGear] Content hash mismatch — server: {}, local: {}. Disconnecting.",
                        payload.serverHash(), localHash);
                context.disconnect(Component.translatable(
                        "customgear.network.hash_mismatch",
                        payload.serverHash(), localHash));
                return;
            }
            // WARN mode: proceed, but remember to warn the player in chat
            // once they're actually in the world.
            LOGGER.warn("[CustomGear] Content hash mismatch (server allows it — WARN mode). "
                            + "Server: {}, local: {}. Tooltips/names may not match server values.",
                    payload.serverHash(), localHash);
            pendingMismatchWarning = true;
        } else {
            LOGGER.info("[CustomGear] Content hash verified with server ({})", localHash);
        }

        context.reply(new HashCheckAckPayload(localHash));
    }

    /** SERVER side: verify the client's hash according to the configured mode. */
    private static void handleAckOnServer(HashCheckAckPayload payload, IPayloadContext context) {
        String serverHash = ContentHasher.current();

        if (!payload.clientHash().equals(serverHash)) {
            if (CustomGearConfig.HANDSHAKE_MODE.get() == HandshakeMode.ENFORCE) {
                LOGGER.warn("[CustomGear] Client sent mismatching content hash '{}' (server: '{}') — disconnecting",
                        payload.clientHash(), serverHash);
                context.disconnect(Component.translatable(
                        "customgear.network.hash_mismatch",
                        serverHash, payload.clientHash()));
                return;
            }
            // WARN mode: let them in, leave a trace for the server owner.
            LOGGER.warn("[CustomGear] Client joined with mismatching content (WARN mode). "
                    + "Server: {}, client: {}", serverHash, payload.clientHash());
        }

        context.finishCurrentTask(HashCheckTask.TYPE);
    }

    // ── Configuration task ────────────────────────────────────────────────────

    /**
     * Runs on the server for every connecting client during the configuration
     * phase (only registered in ENFORCE/WARN modes). Sends the server's
     * captured content hash plus the enforcement flag; the task stays pending
     * (holding the login) until {@link #handleAckOnServer} completes it or a
     * side disconnects.
     */
    public static class HashCheckTask implements ICustomConfigurationTask {

        public static final ConfigurationTask.Type TYPE =
                new ConfigurationTask.Type("ultimatecustomgear:hash_check");

        @Override
        public void run(@NotNull Consumer<CustomPacketPayload> sender) {
            String serverHash = ContentHasher.current();
            boolean enforce = CustomGearConfig.HANDSHAKE_MODE.get() == HandshakeMode.ENFORCE;
            LOGGER.debug("[CustomGear] Sending content hash to connecting client: {} (enforce={})",
                    serverHash, enforce);
            sender.accept(new HashCheckPayload(serverHash, enforce));
        }

        @Override
        public @NotNull ConfigurationTask.Type type() {
            return TYPE;
        }
    }
}