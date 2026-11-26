package arrivedbog593.customgear.network;

import arrivedbog593.customgear.config.CustomGearConfig;
import arrivedbog593.customgear.config.CustomGearConfig.HandshakeMode;
import arrivedbog593.customgear.items.containers.BackpackAnchor;
import arrivedbog593.customgear.items.containers.CustomBackpackItem;
import arrivedbog593.customgear.menu.CustomContainerMenu;
import arrivedbog593.customgear.platform.Services;
import arrivedbog593.customgear.util.ContentHasher;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * What this mod's packets do, the same on every loader. Each loader registers
 * the payloads and the configuration task in its own way and calls in here.
 * <p>
 * <b>Configuration-phase handshake</b>: verifies the joining client LOADED
 * the same customgear content as the server. The server's config
 * (content_handshake_mode) decides what a mismatch means:
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

    private static final Logger LOGGER = LoggerFactory.getLogger("CustomGear");

    /**
     * Payload protocol version. NeoForge negotiates it per channel and
     * rejects a peer on another version with a clear message instead of a
     * codec crash.
     * <p>
     * Bump it only when the wire format of an EXISTING payload changes.
     * Adding a payload doesn't need it: negotiation already refuses a peer
     * that lacks a required channel, and a bump would also cut off every
     * release that shares this protocol.
     */
    public static final String PROTOCOL_VERSION = "3";

    /** The configuration task that holds a joining client until the handshake ends. */
    public static final ConfigurationTask.Type HASH_CHECK_TASK =
            new ConfigurationTask.Type("customgear:hash_check");

    /**
     * Set on the CLIENT when it joins a WARN-mode server with mismatching
     * files. Client-side join handling reads and clears it to show a chat
     * warning. Volatile: written on the network thread, read on the client
     * thread.
     */
    public static volatile boolean pendingMismatchWarning = false;

    private CustomGearNetworking() {}

    // ── Handshake ────────────────────────────────────────────────────────────

    /** SERVER side: whether to run the handshake task at all (OFF skips it). */
    public static boolean isHandshakeEnabled() {
        if (CustomGearConfig.HANDSHAKE_MODE.get() == HandshakeMode.OFF) {
            LOGGER.debug("[CustomGear] Content handshake disabled by config (OFF)");
            return false;
        }
        return true;
    }

    /**
     * SERVER side: what the configuration task sends to every connecting
     * client — the server's captured content hash plus the enforcement flag.
     */
    public static HashCheckPayload createHashCheck() {
        String serverHash = ContentHasher.current();
        boolean enforce = CustomGearConfig.HANDSHAKE_MODE.get() == HandshakeMode.ENFORCE;
        LOGGER.debug("[CustomGear] Sending content hash to connecting client: {} (enforce={})",
                serverHash, enforce);
        return new HashCheckPayload(serverHash, enforce);
    }

    /**
     * CLIENT side: compares the server's hash against what WE loaded at
     * startup.
     *
     * @return the reason to disconnect, or null to answer the server with
     *         {@link #createHashAck()}
     */
    public static @Nullable Component onHashCheck(HashCheckPayload payload) {
        String localHash = ContentHasher.current();

        if (!payload.serverHash().equals(localHash)) {
            if (payload.enforce()) {
                LOGGER.warn("[CustomGear] Content hash mismatch — server: {}, local: {}. Disconnecting.",
                        payload.serverHash(), localHash);
                return Component.translatable("customgear.network.hash_mismatch",
                        payload.serverHash(), localHash);
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
        return null;
    }

    /** CLIENT side: the answer to the server's hash check. */
    public static HashCheckAckPayload createHashAck() {
        return new HashCheckAckPayload(ContentHasher.current());
    }

    /**
     * SERVER side: verifies the client's hash according to the configured mode.
     *
     * @return the reason to disconnect, or null to finish the task and let the
     *         client in
     */
    public static @Nullable Component onHashAck(HashCheckAckPayload payload) {
        String serverHash = ContentHasher.current();

        if (!payload.clientHash().equals(serverHash)) {
            if (CustomGearConfig.HANDSHAKE_MODE.get() == HandshakeMode.ENFORCE) {
                LOGGER.warn("[CustomGear] Client sent mismatching content hash '{}' (server: '{}') — disconnecting",
                        payload.clientHash(), serverHash);
                return Component.translatable("customgear.network.hash_mismatch",
                        serverHash, payload.clientHash());
            }
            // WARN mode: let them in, leave a trace for the server owner.
            LOGGER.warn("[CustomGear] Client joined with mismatching content (WARN mode). "
                    + "Server: {}, client: {}", serverHash, payload.clientHash());
        }
        return null;
    }

    /**
     * CLIENT side, once in the world: the chat warning a WARN-mode server
     * asked for, or null.
     */
    public static @Nullable Component takeMismatchWarning() {
        if (!pendingMismatchWarning) return null;
        pendingMismatchWarning = false;
        return Component.translatable("customgear.network.hash_mismatch_warn");
    }

    // ── Play handlers (server thread) ────────────────────────────────────────

    /**
     * The menu the player has open is the only authority on what they can touch.
     * A client can ask to transfer, but never says what — so the worst a forged
     * packet achieves is a transfer the player could have done by hand.
     */
    public static void handleTransfer(TransferItemsPayload payload, Player player) {
        if (player.containerMenu instanceof CustomContainerMenu menu) {
            menu.transfer(payload.toStorage(), payload.everything());
        }
    }

    public static void handleSort(SortContainerPayload payload, Player player) {
        if (player.containerMenu instanceof CustomContainerMenu menu) {
            menu.sort(payload.order());
        }
    }

    public static void handleSortMode(SortModePayload payload, Player player) {
        if (player.containerMenu instanceof CustomContainerMenu menu) {
            menu.setSortMode(payload.criterion(), payload.descending());
        }
    }

    /**
     * Opens the backpack the player is carrying, in this order: the selected
     * hotbar slot, then the offhand, then accessory slots, then the first one
     * found walking the inventory — which puts the hotbar ahead of the rest,
     * since it is numbered first.
     * <p>
     * The two hands come first because "whatever I am holding" is what a player
     * means by the key, and it is the only way to choose between two backpacks
     * without inventing a rule.
     */
    public static void handleOpenBackpack(Player player) {
        int selected = player.getInventory().selected;
        if (tryOpen(player, new BackpackAnchor.InventorySlot(selected))) return;
        if (tryOpen(player, new BackpackAnchor.Offhand())) return;

        // Equipped beats loose: someone wearing a storage ring is wearing it
        // to have it at hand, and a backpack lying in the inventory covering
        // it would be the opposite of what they asked for. This is where we
        // part from Sophisticated, which checks Curios last.
        for (BackpackAnchor anchor : Services.ACCESSORIES.equippedBackpacks(player)) {
            if (tryOpen(player, anchor)) return;
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (tryOpen(player, new BackpackAnchor.InventorySlot(i))) return;
        }
    }

    /** Opens the stack this anchor points at, if it is a backpack at all. */
    private static boolean tryOpen(Player player, BackpackAnchor anchor) {
        ItemStack stack = anchor.resolve(player);
        if (!(stack.getItem() instanceof CustomBackpackItem backpack)) return false;
        backpack.open(player, anchor, stack);
        return true;
    }
}
