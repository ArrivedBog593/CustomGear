package arrivedbog593.ultimatecustomgear.network;

import arrivedbog593.ultimatecustomgear.CustomGearMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Asks the server to move items between the open container and the player
 * inventory.
 * <p>
 * Carries intent, never contents: two flags, and the server decides what moves.
 * A packet that named the stacks would let a client conjure them.
 *
 * @param toStorage true to push into the container, false to pull out of it
 * @param everything true for shift-click (move all that fits), false for the
 *                   default (only item types the destination already holds)
 */
public record TransferItemsPayload(boolean toStorage, boolean everything)
        implements CustomPacketPayload {

    public static final Type<TransferItemsPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(CustomGearMod.MOD_ID, "transfer_items"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TransferItemsPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, TransferItemsPayload::toStorage,
                    ByteBufCodecs.BOOL, TransferItemsPayload::everything,
                    TransferItemsPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}