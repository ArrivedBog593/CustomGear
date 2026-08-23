package arrivedbog593.ultimatecustomgear.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Serverbound (configuration phase): the client's own content hash, sent
 * back after receiving {@link HashCheckPayload}. The server re-verifies it
 * (defense in depth against modified clients that skip the client-side
 * check) and completes the configuration task on match.
 */
public record HashCheckAckPayload(String clientHash) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HashCheckAckPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    Identifier.fromNamespaceAndPath("ultimatecustomgear", "hash_check_ack"));

    public static final StreamCodec<ByteBuf, HashCheckAckPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, HashCheckAckPayload::clientHash,
                    HashCheckAckPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}