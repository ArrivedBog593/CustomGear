package arrivedbog593.ultimatecustomgear.loader;

import arrivedbog593.ultimatecustomgear.data.FluidData;
import arrivedbog593.ultimatecustomgear.items.fluids.CustomFluid;
import arrivedbog593.ultimatecustomgear.util.GearLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers fluids loaded from JSON.
 * <p>
 * For each FluidData, the following are created:
 *  1. FluidType → visual properties (texture, color, light, name)
 *  2. Fluid Source
 *  3. Fluid Flowing
 *  4. LiquidBlock (world block)
 *  5. BucketItem (bucket)
 */
public class FluidRegistry {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, "customgear");

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, "customgear");

    public static final DeferredRegister<Block> FLUID_BLOCKS =
            DeferredRegister.create(BuiltInRegistries.BLOCK, "customgear");

    public static final DeferredRegister<Item> FLUID_BUCKETS =
            DeferredRegister.create(BuiltInRegistries.ITEM, "customgear");

    public static final Map<ResourceLocation, FluidData> FLUID_MAP = new HashMap<>();

    private static final ResourceLocation WATER_STILL =
            ResourceLocation.withDefaultNamespace("block/water_still");
    private static final ResourceLocation WATER_FLOW =
            ResourceLocation.withDefaultNamespace("block/water_flow");

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");


    public static void  register(IEventBus modEventBus, List<FluidData> fluidList) {
        for (FluidData data : fluidList) {
            registerFluid(data);
        }
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
        FLUID_BLOCKS.register(modEventBus);
        FLUID_BUCKETS.register(modEventBus);
    }

    @SuppressWarnings("unchecked")
    private static void registerFluid(FluidData data) {
        // 1. FluidType
        DeferredHolder<FluidType, FluidType> typeHolder = FLUID_TYPES.register(
                data.id, () -> buildFluidType(data));

        // 2. Source and Flowing — use DeferredHolder to avoid circular array references
        DeferredHolder<Fluid, CustomFluid.Source>[] sourceRef = new DeferredHolder[1];
        DeferredHolder<Fluid, CustomFluid.Flowing>[] flowingRef = new DeferredHolder[1];
        DeferredHolder<Block, LiquidBlock>[] blockRef = new DeferredHolder[1];
        DeferredHolder<Item, BucketItem>[] bucketRef = new DeferredHolder[1];

        sourceRef[0] = FLUIDS.register(data.id, () ->
                new CustomFluid.Source(buildProps(typeHolder, sourceRef, flowingRef, blockRef, bucketRef, data)));

        flowingRef[0] = FLUIDS.register(data.id + "_flowing", () ->
                new CustomFluid.Flowing(buildProps(typeHolder, sourceRef, flowingRef, blockRef, bucketRef, data)));

        blockRef[0] = FLUID_BLOCKS.register(data.id, () ->
                CustomFluid.createBlock(sourceRef[0], data));

        bucketRef[0] = FLUID_BUCKETS.register(data.id + "_bucket", () ->
                new BucketItem(sourceRef[0].get(), new Item.Properties()
                        .craftRemainder(Items.BUCKET)
                        .stacksTo(1)));

        FLUID_MAP.put(ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
    }

    private static BaseFlowingFluid.Properties buildProps(
            DeferredHolder<FluidType, FluidType> type,
            DeferredHolder<Fluid, CustomFluid.Source>[] sourceRef,
            DeferredHolder<Fluid, CustomFluid.Flowing>[] flowingRef,
            DeferredHolder<Block, LiquidBlock>[] blockRef,
            DeferredHolder<Item, BucketItem>[] bucketRef,
            FluidData data) {

        // levelDecreasePerBlock controls spread distance:
        // water uses 1 (reaches 8 blocks), lava uses 4 (reaches 4 blocks)
        // formula: levelDecreasePerBlock = 8 - spreadDistance (clamped 1-7)
        int levelDecrease = Math.clamp(8 - data.spreadDistance, 1, 7);

        return new BaseFlowingFluid.Properties(type, sourceRef[0], flowingRef[0])
                .block(blockRef[0])
                .bucket(bucketRef[0])
                .tickRate(Math.max(1, data.tickRate))
                .levelDecreasePerBlock(levelDecrease);
    }

    private static FluidType buildFluidType(FluidData data) {
        FluidType.Properties props = FluidType.Properties.create()
                .descriptionId("fluid.customgear." + data.id);

        if (data.lightLevel > 0) {
            props.lightLevel(Math.clamp(data.lightLevel, 0, 15));
        }

        return new FluidType(props) {
            @Override
            public net.minecraft.network.chat.@NotNull Component getDescription() {
                return net.minecraft.network.chat.Component.literal(resolveFluidName(data));
            }

            @SuppressWarnings("removal")
            @Override
            public void initializeClient(
                    java.util.function.@NotNull Consumer<net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions> consumer) {
                consumer.accept(new net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions() {
                    @Override
                    public @NotNull ResourceLocation getStillTexture() {
                        return resolveStillTexture(data);
                    }

                    @Override
                    public @NotNull ResourceLocation getFlowingTexture() {
                        return resolveFlowingTexture(data);
                    }

                    @Override
                    public int getTintColor() {
                        if (data.color != null && !data.color.equals("0xFFFFFFFF")) {
                            try {
                                return (int) Long.parseLong(
                                        data.color.replace("0x", "").replace("0X", ""), 16);
                            } catch (Exception ignored) {}
                        }
                        // Default mode — use water blue tint
                        if (data.texture == null || data.texture.mode == null
                                || data.texture.mode.equals("default")) {
                            return 0xFF3F76E4;
                        }
                        return 0xFFFFFFFF;
                    }
                });
            }
        };
    }

    private static ResourceLocation resolveStillTexture(FluidData data) {
        if (data.texture == null || data.texture.mode == null || data.texture.mode.equals("default")) {
            return WATER_STILL;
        }
        if (data.texture.mode.equals("custom")) {
            return ResourceLocation.fromNamespaceAndPath("customgear",
                    "fluid/" + data.id + "_still");
        }
        // reference mode
        String stillRef = data.texture.refs != null ? data.texture.refs.get("still") : null;
        return stillRef != null ? ResourceLocation.parse(stillRef) : WATER_STILL;
    }

    private static ResourceLocation resolveFlowingTexture(FluidData data) {
        if (data.texture == null || data.texture.mode == null || data.texture.mode.equals("default")) {
            return WATER_FLOW;
        }
        if (data.texture.mode.equals("custom")) {
            return ResourceLocation.fromNamespaceAndPath("customgear",
                    "fluid/" + data.id + "_flowing");
        }
        // reference mode
        String flowingRef = data.texture.refs != null ? data.texture.refs.get("flowing") : null;
        return flowingRef != null ? ResourceLocation.parse(flowingRef) : WATER_FLOW;
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
            FLUID_MAP.put(ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
        }
        LOGGER.info("[CustomGear] Updated {} fluids in registry", fluidList.size());
    }
}