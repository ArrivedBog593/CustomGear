package arrivedbog593.ultimatecustomgear.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Clientbound (configuration phase): the server's content hash of its
 * ultimatecustomgear JSON folder, plus whether mismatches are ENFORCED
 * (disconnect) or merely warned about. The mode is decided by the SERVER's
 * config and the client obeys it — a client's local config cannot weaken
 * the server's enforcement.
 * <p>
 * Note: this payload is only sent in ENFORCE and WARN modes; in OFF mode
 * the configuration task isn't registered at all.
 */
public record HashCheckPayload(String serverHash, boolean enforce) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HashCheckPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath("ultimatecustomgear", "hash_check"));

    public static final StreamCodec<ByteBuf, HashCheckPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, HashCheckPayload::serverHash,
                    ByteBufCodecs.BOOL, HashCheckPayload::enforce,
                    HashCheckPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}