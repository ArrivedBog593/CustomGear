package arrivedbog593.ultimatecustomgear.registry;

import arrivedbog593.ultimatecustomgear.data.ContainerContentData;
import arrivedbog593.ultimatecustomgear.data.ContainerData;
import arrivedbog593.ultimatecustomgear.items.containers.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers containers: storage that exists either as a placeable block
 * (barrel, chest, shulker) or as a carried item (backpack).
 * <p>
 * WHY THIS IS NOT PART OF BlockRegistry: a backpack is not a block. Keeping
 * containers there would force that class to know which subtypes register a
 * Block and which register only an Item. Here the split lives in one place and
 * BlockRegistry goes back to knowing nothing about storage.
 * <p>
 * Three DeferredRegisters in one class is deliberate and not a workaround:
 * a container's Block, its BlockItem and the shared BlockEntityType are
 * registered together and reference each other. Splitting them across classes
 * would only mean two classes that have to import each other.
 * <p>
 * Several DeferredRegisters over the same registry and namespace are fine —
 * they are queues feeding the same registration event, not owners of it.
 */
public class ContainerRegistry {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(BuiltInRegistries.BLOCK, "customgear");

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, "customgear");

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "customgear");

    /**
     * Container blocks collected while registering, so the block entity type
     * can declare exactly which blocks it is valid for. Blocks register before
     * block entities, so by the time the supplier below runs every holder here
     * resolves.
     */
    private static final List<DeferredHolder<Block, CustomContainerBlock>> BLOCK_HOLDERS =
            new ArrayList<>();

    /**
     * ONE type for every custom container regardless of subtype or size.
     * <p>
     * The type's id ends up in the NBT of every placed block, so splitting it
     * per subtype would freeze three ids into people's worlds while the subtypes
     * are still being written — and a backpack, which is not a block at all,
     * would need its own branch regardless. The variation lives in the supplier
     * instead, where reorganising costs nothing.
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CustomContainerBlockEntity>>
            CONTAINER_BE = BLOCK_ENTITIES.register("container", () ->
            BlockEntityType.Builder.of(
                    ContainerRegistry::blockEntityFor,
                    BLOCK_HOLDERS.stream()
                            .map(DeferredHolder::get)
                            .toArray(Block[]::new)
                    // The DFU Type is always null for mods: only Mojang registers
                    // data-fixer types, and vanilla passes null here too.
            ).build(null));


    /** The block decides which block entity class backs it. */
    private static CustomContainerBlockEntity blockEntityFor(BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof CustomChestBlock) {
            return new CustomChestBlockEntity(pos, state);
        }
        if (state.getBlock() instanceof CustomShulkerBlock) {
            return new CustomShulkerBlockEntity(pos, state);
        }
        return new CustomBarrelBlockEntity(pos, state);
    }

    /**
     * containerId → its definition, for runtime lookups.
     * <p>
     * Separate from BlockRegistry.BLOCK_MAP on purpose. That map holds
     * BlockData, so every read had to test whether the entry happened to be a
     * container; here absence means exactly one thing — the definition is gone —
     * which is what orphan rescue keys off.
     */
    public static final Map<ResourceLocation, ContainerContentData> CONTAINER_MAP = new HashMap<>();

    public static void register(IEventBus modEventBus, List<ContainerContentData> containerList) {
        for (ContainerContentData data : containerList) {
            if (data.container == null) continue;

            if (!data.container.isBlock()) {
                registerCarriedContainer(data);
                continue;
            }
            registerBlockContainer(data);
        }
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
    }

    private static void registerBlockContainer(ContainerContentData data) {
        DeferredHolder<Block, CustomContainerBlock> blockHolder =
                BLOCKS.register(data.id, () -> blockFor(data));
        BLOCK_HOLDERS.add(blockHolder);
        ITEMS.register(data.id, () -> itemFor(data, blockHolder.get()));
        CONTAINER_MAP.put(ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
        LOGGER.info("[CustomGear] Container registered: {} ({}, {} slots)",
                data.id, data.container.kind(), data.container.slots);
    }

    /**
     * A backpack registers an Item and no Block: there is nothing to place, so
     * the block registry never hears about it and BLOCK_HOLDERS stays free of
     * something the block entity type would choke on.
     */
    private static void registerCarriedContainer(ContainerContentData data) {
        ITEMS.register(data.id, () -> new CustomBackpackItem(data, containerItemProps(data)));
        CONTAINER_MAP.put(ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
        LOGGER.info("[CustomGear] Container registered: {} (backpack, {} slots)",
                data.id, data.container.slots);
    }

    /**
     * Only the shapes drawn by a renderer need their own BlockItem. A barrel is
     * a baked model like any block, so a plain BlockItem draws it correctly and
     * a subclass would only add a redundant client extension.
     */
    private static BlockItem itemFor(ContainerContentData data, Block block) {
        Item.Properties props = containerItemProps(data);
        if (ContainerData.KIND_CHEST.equals(data.container.kind())) {
            return new CustomChestBlockItem(block, props);
        }
        if (ContainerData.KIND_SHULKER.equals(data.container.kind())) {
            return new CustomShulkerBlockItem(block, props);
        }
        return new CustomContainerBlockItem(block, props);
    }

    /**
     * The subtype picks the block class. Anything not listed here was already
     * rejected by the parser, so reaching the default means the two lists have
     * drifted apart — worth failing loudly rather than guessing a shape.
     */
    private static CustomContainerBlock blockFor(ContainerContentData data) {
        if (ContainerData.KIND_BARREL.equals(data.container.kind())) {
            return new CustomBarrelBlock(data);
        }
        if (ContainerData.KIND_CHEST.equals(data.container.kind())) {
            return new CustomChestBlock(data);
        }
        if (ContainerData.KIND_SHULKER.equals(data.container.kind())) {
            return new CustomShulkerBlock(data);
        }
        throw new IllegalStateException(
                "No block class for container type '" + data.container.kind()
                        + "' on '" + data.id + "' — the parser accepted a subtype "
                        + "ContainerRegistry cannot build.");
    }

    /**
     * A keeps_contents container must NOT stack. Stacking merges two items into
     * one set of components, which is one inventory: place both, and each reads
     * the same contents (duplication), or one inventory is simply lost. An item
     * cannot stack conditionally on its components, so vanilla's shulker box is
     * stacksTo(1) whether it holds anything or not, and so is this.
     */
    private static Item.Properties containerItemProps(ContainerContentData data) {
        Item.Properties p = new Item.Properties();
        if (data.fireResistant) p = p.fireResistant();
        return data.container.keepsContents() ? p.stacksTo(1) : p;
    }

    /**
     * Refreshes the definitions during /customgear reload. Slot count is baked
     * at registration and is NOT affected — everything else (names, drop
     * behaviour, columns, obstruction) reads this map live.
     */
    public static void updateContainerData(List<ContainerContentData> containerList) {
        CONTAINER_MAP.clear();
        for (ContainerContentData data : containerList) {
            CONTAINER_MAP.put(ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
        }
        LOGGER.info("[CustomGear] Updated {} containers in registry", containerList.size());
    }
}