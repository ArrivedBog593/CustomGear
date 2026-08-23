package arrivedbog593.ultimatecustomgear.resources;

import arrivedbog593.ultimatecustomgear.data.FluidData;
import net.minecraft.resources.Identifier;

/**
 * How a fluid definition turns into the sprites and tint it draws with.
 * <p>
 * WHY NOT IN FluidRegistry, where this used to live. These are pure functions of
 * the JSON — no registry, no game state — and three callers need them from three
 * different places: the registry itself, the client when it builds the
 * FluidModel, and the model generator when it writes the fluid block. That last
 * one is what forced the move: reaching into FluidRegistry loads its
 * DeferredRegisters, which cannot be touched before the game has bootstrapped.
 * Resource-path logic belongs beside the other resource-path logic anyway.
 */
public final class FluidTextures {

    private FluidTextures() {}

    private static final Identifier WATER_STILL =
            Identifier.withDefaultNamespace("block/water_still");
    private static final Identifier WATER_FLOW =
            Identifier.withDefaultNamespace("block/water_flow");

    public static Identifier still(FluidData data) {
        return resolve(data, "still", WATER_STILL);
    }

    public static Identifier flowing(FluidData data) {
        return resolve(data, "flowing", WATER_FLOW);
    }

    /**
     * The colour multiplied over the fluid's sprites.
     * <p>
     * No textures declared means the fluid is drawing water's sprites, so it
     * gets water's blue. A fluid that brought its own textures already has
     * whatever colour they have, so it is left alone.
     */
    public static int tint(FluidData data) {
        if (data.color != null && !data.color.equals("0xFFFFFFFF")) {
            try {
                return (int) Long.parseLong(
                        data.color.replace("0x", "").replace("0X", ""), 16);
            } catch (Exception ignored) {}
        }
        boolean hasOwnTextures = data.texture != null
                && data.texture.refs != null
                && (data.texture.refs.get("still") != null
                || data.texture.refs.get("flowing") != null);
        return hasOwnTextures ? 0xFFFFFFFF : 0xFF3F76E4;
    }

    /**
     * The sprite this fluid draws with.
     * <p>
     * A reference is used as written — and for a fluid that means the SHORT
     * form, minecraft:block/lava_still, because that is how atlas sprites are
     * addressed. A file was copied into the pack by the generator, so it is
     * referenced by the name derived from the id.
     */
    private static Identifier resolve(FluidData data, String slot, Identifier fallback) {
        String value = data.texture != null && data.texture.refs != null
                ? data.texture.refs.get(slot) : null;
        if (value == null) return fallback;

        return switch (TextureRef.kindOf(value)) {
            case REFERENCE -> {
                Identifier parsed = Identifier.tryParse(value);
                yield parsed != null ? parsed : fallback;
            }
            case FILE -> Identifier.fromNamespaceAndPath(
                    ModelConstants.NAMESPACE, "block/" + data.id + "_" + slot);
            case INVALID -> {
                TextureRef.reportInvalid("Fluid '" + data.id + "'", slot, value);
                yield fallback;
            }
        };
    }
}
