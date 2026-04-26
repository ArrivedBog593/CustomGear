package com.github.arrivedbog593.loader;

import com.github.arrivedbog593.data.BlockData;
import com.github.arrivedbog593.items.blocks.CustomBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers blocks loaded from JSON.
 * <p>
 * Also registers the corresponding BlockItem so the block
 * can be held in inventory and crafted.
 * <p>
 * Required tool and tool level are applied via block tags:
 *   data/minecraft/tags/blocks/needs_stone_tool.json → toolLevel >= 1
 *   data/minecraft/tags/blocks/needs_iron_tool.json → toolLevel >= 2
 *   data/minecraft/tags/blocks/needs_diamond_tool.json → toolLevel >= 3
 * Tool type tags:
 *   data/minecraft/tags/blocks/mineable/pickaxe.json → requiredTool = "pickaxe"
 *   data/minecraft/tags/blocks/mineable/axe.json → requiredTool = "axe"
 *   etc.
 */
public class BlockRegistry {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(BuiltInRegistries.BLOCK, "customgear");

    public static final DeferredRegister<Item> BLOCK_ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, "customgear");

    /** blockId → BlockData for runtime lookups */
    public static final Map<ResourceLocation, BlockData> BLOCK_MAP = new HashMap<>();

    public static void register(IEventBus modEventBus, List<BlockData> blockList) {
        for (BlockData data : blockList) {
            registerBlock(data);
        }
        BLOCKS.register(modEventBus);
        BLOCK_ITEMS.register(modEventBus);
    }

    private static void registerBlock(BlockData data) {
        // Register the Block and keep a reference via DeferredHolder
        DeferredHolder<Block, CustomBlock> blockHolder =
                BLOCKS.register(data.id, () -> new CustomBlock(data));

        // Register the BlockItem using the DeferredHolder — avoids null lookup
        BLOCK_ITEMS.register(data.id, () ->
                new BlockItem(blockHolder.get(), new Item.Properties()));

        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("customgear", data.id);
        BLOCK_MAP.put(loc, data);
        LOGGER.info("[CustomGear] Block registered: {}", data.id);
    }
}