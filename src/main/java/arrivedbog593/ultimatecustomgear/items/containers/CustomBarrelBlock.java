package arrivedbog593.ultimatecustomgear.items.containers;

import arrivedbog593.ultimatecustomgear.data.ContainerContentData;
import arrivedbog593.ultimatecustomgear.items.blocks.CustomBlock;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A full-cube container placed on any of six axes, whose open state is a
 * blockstate property rather than an animated model.
 * <p>
 * The whole appearance is describable in generated JSON: twelve blockstate
 * variants and two cube models. No block entity renderer, which is why this is
 * the only subtype that costs nothing to draw.
 */
public class CustomBarrelBlock extends CustomContainerBlock {

    /** Six directions, unlike a horizontal block — a barrel goes on ceilings. */
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    /** Driven by the opener count in the block entity, never by the player. */
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public CustomBarrelBlock(ContainerContentData data, Properties props) {
        super(data, CustomBlock.buildProperties(data, props));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    /**
     * getNearestLookingDirection covers up and down as well as the horizontal
     * four, which is what makes a barrel placeable on a ceiling.
     */
    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(OPEN, false);
    }

    @Override
    public SoundEvent openSound()  { return SoundEvents.BARREL_OPEN; }

    @Override
    public SoundEvent closeSound() { return SoundEvents.BARREL_CLOSE; }
}