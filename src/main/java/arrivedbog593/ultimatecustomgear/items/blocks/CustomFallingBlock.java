package arrivedbog593.ultimatecustomgear.items.blocks;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.FallingBlock;
import org.jetbrains.annotations.NotNull;


/**
 * A block that falls when it has no support below, like sand or gravel.
 * <p>
 * Uses the vanilla FallingBlock physics — the block converts to a
 * FallingBlockEntity when unsupported, falls with gravity,
 * and becomes a block again when it lands.
 * <p>
 * JSON example:
 * {
 *   "id": "my_falling_block",
 *   "type": "block",
 *   "gravity": true,
 *   "texture": {
 *     "mode": "reference",
 *     "refs": { "block": "minecraft:block/sand" }
 *   }
 * }
 */
public class CustomFallingBlock extends FallingBlock {

    public CustomFallingBlock(BlockData data) {
        super(CustomBlock.buildProperties(data));
    }


    @Override
    protected @NotNull MapCodec<? extends FallingBlock> codec() {
        throw new UnsupportedOperationException(
                "CustomFallingBlock does not support codec serialization");
    }
}