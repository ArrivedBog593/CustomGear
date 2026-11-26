package arrivedbog593.customgear.registry;

import arrivedbog593.customgear.Constants;
import arrivedbog593.customgear.data.FluidData;
import arrivedbog593.customgear.items.fluids.CustomFluid;
import arrivedbog593.customgear.platform.Services;
import arrivedbog593.customgear.platform.registration.RegistrationProvider;
import arrivedbog593.customgear.platform.registration.RegistryEntry;
import arrivedbog593.customgear.resources.TextureRef;
import arrivedbog593.customgear.util.GearLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers fluids loaded from JSON.
 * <p>
 * For each FluidData, the following are created:
 *  1. Fluid Source
 *  2. Fluid Flowing
 *  3. LiquidBlock (world block)
 *  4. BucketItem (bucket)
 * plus whatever the loader adds (NeoForge: a FluidType, see IFluidHelper).
 */
public class FluidRegistry {

    public static final RegistrationProvider<Fluid> FLUIDS =
            RegistrationProvider.get(Registries.FLUID);

    public static final RegistrationProvider<Block> FLUID_BLOCKS =
            RegistrationProvider.get(Registries.BLOCK);

    public static final RegistrationProvider<Item> FLUID_BUCKETS =
            RegistrationProvider.get(Registries.ITEM);

    public static final Map<ResourceLocation, FluidData> FLUID_MAP = new HashMap<>();

    /** Every registered fluid, for the client to attach its rendering to. */
    public static final List<RegisteredFluid> REGISTERED = new ArrayList<>();

    public record RegisteredFluid(FluidData data,
                                  RegistryEntry<CustomFluid.Source> source,
                                  RegistryEntry<CustomFluid.Flowing> flowing,
                                  RegistryEntry<LiquidBlock> block,
                                  CustomFluid.Properties properties) {}

    private static final ResourceLocation WATER_STILL =
            ResourceLocation.withDefaultNamespace("block/water_still");
    private static final ResourceLocation WATER_FLOW =
            ResourceLocation.withDefaultNamespace("block/water_flow");

    private static final Logger LOGGER = LoggerFactory.getLogger("CustomGear");

    public static void register(List<FluidData> fluidList) {
        for (FluidData data : fluidList) {
            registerFluid(data);
        }
    }

    @SuppressWarnings("unchecked")
    private static void registerFluid(FluidData data) {
        // Each object links to the others, so they are reached through these
        // slots, filled as each one registers, and read only inside suppliers.
        RegistryEntry<CustomFluid.Source>[] sourceRef = new RegistryEntry[1];
        RegistryEntry<CustomFluid.Flowing>[] flowingRef = new RegistryEntry[1];
        RegistryEntry<LiquidBlock>[] blockRef = new RegistryEntry[1];
        RegistryEntry<BucketItem>[] bucketRef = new RegistryEntry[1];

        CustomFluid.Properties props = new CustomFluid.Properties(data,
                () -> sourceRef[0].get(),
                () -> flowingRef[0].get(),
                () -> blockRef[0].get(),
                () -> bucketRef[0].get());
        Services.FLUIDS.prepare(props);

        sourceRef[0] = FLUIDS.register(data.id, () -> Services.FLUIDS.createSource(props));
        flowingRef[0] = FLUIDS.register(data.id + "_flowing", () -> Services.FLUIDS.createFlowing(props));
        blockRef[0] = FLUID_BLOCKS.register(data.id, () -> CustomFluid.createBlock(sourceRef[0], data));
        bucketRef[0] = FLUID_BUCKETS.register(data.id + "_bucket", () ->
                new BucketItem(sourceRef[0].get(), bucketProps(data)));

        FLUID_MAP.put(Constants.id(data.id), data);
        REGISTERED.add(new RegisteredFluid(data, sourceRef[0], flowingRef[0], blockRef[0], props));
    }

    private static Item.Properties bucketProps(FluidData data) {
        Item.Properties p = new Item.Properties()
                .craftRemainder(Items.BUCKET)
                .stacksTo(1);
        return data.fireResistant ? p.fireResistant() : p;
    }

    // ── Rendering, read by each loader's client setup ───────────────────────

    public static ResourceLocation resolveStillTexture(FluidData data) {
        return resolveFluidTexture(data, "still", WATER_STILL);
    }

    public static ResourceLocation resolveFlowingTexture(FluidData data) {
        return resolveFluidTexture(data, "flowing", WATER_FLOW);
    }

    /** ARGB tint the fluid's sprites are drawn with. */
    public static int tintColor(FluidData data) {
        if (data.color != null && !data.color.equals("0xFFFFFFFF")) {
            try {
                return (int) Long.parseLong(
                        data.color.replace("0x", "").replace("0X", ""), 16);
            } catch (Exception ignored) {}
        }
        // No textures declared means the fluid is drawing water's sprites, so
        // it gets water's blue. A fluid that brought its own textures already
        // has whatever color they have.
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
    private static ResourceLocation resolveFluidTexture(FluidData data, String slot,
                                                        ResourceLocation fallback) {
        String value = data.texture != null && data.texture.refs != null
                ? data.texture.refs.get(slot) : null;
        if (value == null) return fallback;

        return switch (TextureRef.kindOf(value)) {
            case REFERENCE -> {
                ResourceLocation parsed = ResourceLocation.tryParse(value);
                yield parsed != null ? parsed : fallback;
            }
            case FILE -> Constants.id("block/" + data.id + "_" + slot);
            case INVALID -> {
                TextureRef.reportInvalid("Fluid '" + data.id + "'", slot, value);
                yield fallback;
            }
        };
    }

    public static String resolveFluidName(FluidData data) {
        if (data.names == null || data.names.isEmpty()) return data.id;
        String lang = GearLookup.getCurrentLang();
        return data.names.getOrDefault(lang,
                data.names.getOrDefault("en_us", data.id));
    }

    /**
     * Updates fluid data during reload. Allows changing texture references without restart.
     */
    public static void updateFluidData(List<FluidData> fluidList) {
        FLUID_MAP.clear();
        for (FluidData data : fluidList) {
            FLUID_MAP.put(Constants.id(data.id), data);
        }
        LOGGER.info("[CustomGear] Updated {} fluids in registry", fluidList.size());
    }
}
