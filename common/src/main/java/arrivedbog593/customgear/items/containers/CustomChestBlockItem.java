package arrivedbog593.customgear.items.containers;

import net.minecraft.world.level.block.Block;

/**
 * The item form of a chest-shaped container.
 * <p>
 * Exists only to hand rendering over to a BEWLR (attached by each loader, see
 * ItemRenderers). The block reports
 * ENTITYBLOCK_ANIMATED and its item model is builtin/entity, so without this the
 * item is invisible in the hand, the inventory, on the ground and in item
 * frames. A barrel needs none of this — it has a real baked model.
 */
public class CustomChestBlockItem extends CustomContainerBlockItem {

    public CustomChestBlockItem(Block block, Properties properties) {
        super(block, properties);
    }
}