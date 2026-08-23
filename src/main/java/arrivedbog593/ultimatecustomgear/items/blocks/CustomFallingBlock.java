package arrivedbog593.ultimatecustomgear.items.blocks;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import arrivedbog593.ultimatecustomgear.util.MapColorResolver;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
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
 *     "refs": { "block": "minecraft:block/sand" }
 *   }
 * }
 */
public class CustomFallingBlock extends FallingBlock {

    /**
     * Colour of the falling-dust particles.
     * <p>
     * getDustColor became abstract, so every falling block now has to answer it.
     * The JSON says nothing about dust, and asking authors for a second colour
     * would be a worse deal than deriving one — so it comes from the map colour
     * the block already declares, which is what vanilla's own coloured falling
     * blocks do.
     */
    private final int dustColor;

    public CustomFallingBlock(BlockData data, Properties props) {
        super(CustomBlock.buildProperties(data, props));
        this.dustColor = MapColorResolver.resolve(data.mapColor).col;
    }

    @Override
    public int getDustColor(@NotNull BlockState state, @NotNull BlockGetter level,
                            @NotNull BlockPos pos) {
        return this.dustColor;
    }

    @Override
    protected @NotNull MapCodec<? extends FallingBlock> codec() {
        throw new UnsupportedOperationException(
                "CustomFallingBlock does not support codec serialization");
    }
}
