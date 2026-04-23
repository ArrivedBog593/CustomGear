package com.github.arrivedbog593.loader;

import com.github.arrivedbog593.data.FluidData;
import com.github.arrivedbog593.items.fluids.CustomFluid;
import com.github.arrivedbog593.items.gear.CustomSwordItem;
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
                new CustomFluid.Source(buildProps(typeHolder, sourceRef, flowingRef, blockRef, bucketRef)));

        flowingRef[0] = FLUIDS.register(data.id + "_flowing", () ->
                new CustomFluid.Flowing(buildProps(typeHolder, sourceRef, flowingRef, blockRef, bucketRef)));

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
            DeferredHolder<Item, BucketItem>[] bucketRef) {

        return new BaseFlowingFluid.Properties(type, sourceRef[0], flowingRef[0])
                .block(blockRef[0])
                .bucket(bucketRef[0]);
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
        };
    }

    public static String resolveFluidName(FluidData data) {
        if (data.names == null || data.names.isEmpty()) return data.id;
        String lang = CustomSwordItem.getCurrentLang();
        return data.names.getOrDefault(lang,
                data.names.getOrDefault("en_us", data.id));
    }
}