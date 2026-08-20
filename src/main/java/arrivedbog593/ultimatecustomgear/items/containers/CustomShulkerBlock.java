package arrivedbog593.ultimatecustomgear.items.containers;

import arrivedbog593.ultimatecustomgear.data.ContainerContentData;
import arrivedbog593.ultimatecustomgear.items.blocks.CustomBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A container that sticks to whatever surface it is placed against, and whose
 * lid slides out of that surface.
 * <p>
 * THE SHAPE IS NOT CONSTANT. It grows as the lid rises, which is why the
 * progress lives on the server too and why this block refuses to open when the
 * grown box would not fit.
 */
public class CustomShulkerBlock extends CustomContainerBlock {

    /**
     * Points AWAY from the surface it is stuck to — the face that was clicked,
     * not the direction the player is looking. Place one on the floor and it
     * opens upward; stick it to a wall and it opens toward you.
     */
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    /** Almost the full block, so dropped items rest on top instead of sinking in. */
    private static final VoxelShape ITEM_ENTITY_SHAPE = box(0.05, 0.05, 0.05, 15.95, 15.95, 15.95);


    public CustomShulkerBlock(ContainerContentData data) {
        super(data, shulkerProperties(data));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    /**
     * Five properties a shulker cannot do without, none of them expressible in
     * the JSON and none settable after construction.
     * <p>
     * dynamicShape is the one that actually breaks things when missing:
     * BlockStateBase caches collision shapes at construction, so without it
     * getShape is never called again and the box stays a cube however far the
     * lid has risen. The rest follow from the shape not filling the block —
     * it must not occlude, suffocate or block the view while open — and
     * DESTROY stops a piston from moving a full inventory around.
     */
    private static Properties shulkerProperties(ContainerContentData data) {
        return CustomBlock.buildProperties(data, p -> p
                .forceSolidOn()
                .dynamicShape()
                .noOcclusion()
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
                .pushReaction(PushReaction.DESTROY));
    }

    /**
     * Item entities collide against a near-full cube rather than the real shape.
     * The lid leaves gaps a dropped item would fall through and get stuck in,
     * and vanilla does exactly this for the same reason.
     */
    @Override
    protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                    @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return context instanceof EntityCollisionContext ctx && ctx.getEntity() instanceof ItemEntity
                ? ITEM_ENTITY_SHAPE
                : super.getCollisionShape(state, level, pos, context);
    }

    @Override
    protected boolean isCollisionShapeFullBlock(@NotNull BlockState state, @NotNull BlockGetter level,
                                                @NotNull BlockPos pos) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    /**
     * Read from the block entity, because the lid's position is part of it.
     * Falls back to a full cube when there is no block entity yet — during world
     * load the shape is asked for before the entity exists.
     */
    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                           @NotNull BlockPos pos, @NotNull CollisionContext context) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CustomShulkerBlockEntity shulker) {
            return Shapes.create(shulker.getBoundingBox(state));
        }
        return Shapes.block();
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    /**
     * Refuses to open when the box it would grow into is occupied.
     * <p>
     * Not just fidelity: opening pushes entities out along the facing axis, and
     * against a solid block there is nowhere to push them to.
     */
    @Override
    protected boolean canOpen(BlockState state, Level level, BlockPos pos) {
        if (!state.hasProperty(FACING)) return true;
        AABB needed = Shulker.getProgressDeltaAabb(1.0F, state.getValue(FACING), 0.0F, 0.5F)
                .move(pos)
                .deflate(1.0E-6);
        return level.noCollision(needed);
    }

    /**
     * Ticks on BOTH sides, unlike the chest's: the server needs the progress to
     * answer getShape, and the client needs it to draw the lid.
     */
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return (lvl, pos, st, be) -> {
            if (be instanceof CustomShulkerBlockEntity shulker) {
                CustomShulkerBlockEntity.animateTick(lvl, pos, st, shulker);
            }
        };
    }

    @Override
    public SoundEvent openSound()  { return SoundEvents.SHULKER_BOX_OPEN; }

    @Override
    public SoundEvent closeSound() { return SoundEvents.SHULKER_BOX_CLOSE; }
}