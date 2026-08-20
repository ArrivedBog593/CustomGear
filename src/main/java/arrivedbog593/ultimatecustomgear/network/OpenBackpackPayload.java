package arrivedbog593.ultimatecustomgear.network;

import arrivedbog593.ultimatecustomgear.CustomGearMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Asks the server to open whichever backpack the player is carrying.
 * <p>
 * CARRIES NOTHING. Which one to open is decided server side from the player's
 * real inventory — a packet naming a slot would let a client open a backpack it
 * does not have, and the answer is cheap to work out anyway.
 */
public record OpenBackpackPayload() implements CustomPacketPayload {

    public static final Type<OpenBackpackPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CustomGearMod.MOD_ID, "open_backpack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenBackpackPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenBackpackPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}