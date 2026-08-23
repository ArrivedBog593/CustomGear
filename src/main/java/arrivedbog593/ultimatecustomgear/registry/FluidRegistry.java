package arrivedbog593.ultimatecustomgear.registry;

import arrivedbog593.ultimatecustomgear.data.FluidData;
import arrivedbog593.ultimatecustomgear.items.fluids.CustomFluid;
import arrivedbog593.ultimatecustomgear.resources.TextureRef;
import arrivedbog593.ultimatecustomgear.util.GearLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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

import java.util.ArrayList;
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

    public static final DeferredRegister.Blocks FLUID_BLOCKS =
            DeferredRegister.createBlocks("customgear");

    public static final DeferredRegister.Items FLUID_BUCKETS =
            DeferredRegister.createItems("customgear");

    public static final Map<Identifier, FluidData> FLUID_MAP = new HashMap<>();

    /**
     * Everything the client needs to build one FluidModel: the two fluids and
     * the definition that names their sprites.
     */
    public record RenderEntry(DeferredHolder<Fluid, CustomFluid.Source> source,
                              DeferredHolder<Fluid, CustomFluid.Flowing> flowing,
                              FluidData data) {}

    public static final List<RenderEntry> RENDERED = new ArrayList<>();

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

        blockRef[0] = FLUID_BLOCKS.registerBlock(data.id, props ->
                CustomFluid.createBlock(sourceRef[0], data, props));

        bucketRef[0] = FLUID_BUCKETS.registerItem(data.id + "_bucket", props ->
                new BucketItem(sourceRef[0].get(), bucketProps(data, props)));

        // Kept so the client can build a FluidModel per fluid — see
        // ClientSetup.onRegisterFluidModels. The textures used to be answered by
        // the FluidType itself; they are now registered separately, and this is
        // the only place that still pairs a fluid with the JSON it came from.
        RENDERED.add(new RenderEntry(sourceRef[0], flowingRef[0], data));

        FLUID_MAP.put(Identifier.fromNamespaceAndPath("customgear", data.id), data);
    }

    private static Item.Properties bucketProps(FluidData data, Item.Properties props) {
        Item.Properties p = props
                .craftRemainder(Items.BUCKET)
                .stacksTo(1);
        return data.fireResistant ? p.fireResistant() : p;
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

    /**
     * NO CLIENT EXTENSION HERE ANY MORE. A FluidType used to answer its own
     * still/flowing sprites and tint through initializeClient. Those methods are
     * gone: a fluid's appearance is now a FluidModel, registered on the client
     * event — see ClientSetup.onRegisterFluidModels. What is left here is the
     * part that is genuinely server-side.
     */
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
            FLUID_MAP.put(Identifier.fromNamespaceAndPath("customgear", data.id), data);
        }
        LOGGER.info("[CustomGear] Updated {} fluids in registry", fluidList.size());
    }
}