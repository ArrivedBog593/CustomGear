package arrivedbog593.ultimatecustomgear.network;

import arrivedbog593.ultimatecustomgear.CustomGearMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Remembers how a placed container wants to be sorted.
 * <p>
 * The server stores this and hands it back when the container is opened again,
 * but never acts on it: sorting itself happens client side, because only the
 * client knows the player's language. This is a preference the server keeps
 * without interpreting.
 */
public record SortModePayload(byte criterion, boolean descending) implements CustomPacketPayload {

    public static final Type<SortModePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CustomGearMod.MOD_ID, "sort_mode"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SortModePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BYTE, SortModePayload::criterion,
                    ByteBufCodecs.BOOL, SortModePayload::descending,
                    SortModePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}