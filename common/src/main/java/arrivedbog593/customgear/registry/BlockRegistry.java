package arrivedbog593.customgear.registry;

import arrivedbog593.customgear.data.BlockData;
import arrivedbog593.customgear.items.blocks.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import arrivedbog593.customgear.platform.registration.RegistryEntry;
import arrivedbog593.customgear.platform.registration.RegistrationProvider;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers blocks loaded from JSON.
 * <p>
 * Also registers the corresponding BlockItem so the block
 * can be held in inventory and crafted.
 * <p>
 * Containers are NOT here — see ContainerRegistry. They register the same two
 * registry entries a block does, but not all of them are blocks, and the
 * block entity type belongs with them.
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

    private static final Logger LOGGER = LoggerFactory.getLogger("CustomGear");

    public static final RegistrationProvider<Block> BLOCKS =
            RegistrationProvider.get(Registries.BLOCK);

    public static final RegistrationProvider<Item> BLOCK_ITEMS =
            RegistrationProvider.get(Registries.ITEM);

    /** blockId → BlockData for runtime lookups */
    public static final Map<ResourceLocation, BlockData> BLOCK_MAP = new HashMap<>();

    public static void register(List<BlockData> blockList) {
        for (BlockData data : blockList) {
            registerBlock(data);
        }
    }

    private static void registerBlock(BlockData data) {
        if (data.directional) {
            RegistryEntry<CustomDirectionalBlock> blockHolder =
                    BLOCKS.register(data.id, () -> new CustomDirectionalBlock(data));
            BLOCK_ITEMS.register(data.id, () ->
                    new BlockItem(blockHolder.get(), blockItemProps(data)));
            LOGGER.info("[CustomGear] Directional block registered: {}", data.id);
        } else if (data.gravity) {
            RegistryEntry<CustomFallingBlock> blockHolder =
                    BLOCKS.register(data.id, () -> new CustomFallingBlock(data));
            BLOCK_ITEMS.register(data.id, () ->
                    new BlockItem(blockHolder.get(), blockItemProps(data)));
            LOGGER.info("[CustomGear] Falling block registered: {}", data.id);
        } else {
            RegistryEntry<CustomBlock> blockHolder =
                    BLOCKS.register(data.id, () -> new CustomBlock(data));
            BLOCK_ITEMS.register(data.id, () ->
                    new BlockItem(blockHolder.get(), blockItemProps(data)));
            LOGGER.info("[CustomGear] Block registered: {}", data.id);
        }
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("customgear", data.id);
        BLOCK_MAP.put(loc, data);
    }

    private static Item.Properties blockItemProps(BlockData data) {
        Item.Properties p = new Item.Properties();
        return data.fireResistant ? p.fireResistant() : p;
    }

    /**
     * Updates block data during reload without restarting the game.
     * Allows refreshing names and other runtime properties.
     */
    public static void updateBlockData(List<BlockData> blockList) {
        BLOCK_MAP.clear();
        for (BlockData data : blockList) {
            BLOCK_MAP.put(ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
        }
        LOGGER.info("[CustomGear] Updated {} blocks in registry", blockList.size());
    }
}