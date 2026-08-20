package arrivedbog593.ultimatecustomgear.network;

import arrivedbog593.ultimatecustomgear.CustomGearMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The order the client wants the container laid out in.
 * <p>
 * ORDER ONLY, NEVER AMOUNTS. Each stack here carries a count of one and exists
 * purely to identify a type; the server merges the real contents itself and just
 * follows this sequence. A lying client gets a strangely ordered chest, never a
 * duplicated item.
 * <p>
 * The order is computed client side because sorting by display name needs the
 * language files, which a dedicated server does not have.
 */
public record SortContainerPayload(List<ItemStack> order) implements CustomPacketPayload {

    public static final Type<SortContainerPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CustomGearMod.MOD_ID, "sort_container"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SortContainerPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), SortContainerPayload::order,
                    SortContainerPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
