package arrivedbog593.customgear.items.containers;

import net.minecraft.world.level.block.Block;

/**
 * The item form of a shulker container. Exists only to hand rendering over to a
 * BEWLR — see CustomChestBlockItem for why that is necessary at all.
 */
public class CustomShulkerBlockItem extends CustomContainerBlockItem {

    public CustomShulkerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }
}
