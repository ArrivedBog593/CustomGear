package arrivedbog593.ultimatecustomgear.items.blocks;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A block that rotates horizontally when placed, facing the player.
 * Works like a furnace or dispenser — the "front" face points toward the player.
 * <p>
 * The "north" face in the JSON is treated as the front face.
 * When placed facing north, no rotation is applied.
 * When placed facing south, a 180° Y rotation is applied, etc.
 * <p>
 * The blockstate JSON handles the rotation automatically via variants.
 */
public class CustomDirectionalBlock extends HorizontalDirectionalBlock {

    public CustomDirectionalBlock(BlockData data, Properties props) {
        super(CustomBlock.buildProperties(data, props));
        // Register the default state with facing=north
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        // Face the direction the player is looking (opposite = faces toward player)
        return this.defaultBlockState().setValue(FACING,
                context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        throw new UnsupportedOperationException("CustomDirectionalBlock does not support codec serialization");
    }
}