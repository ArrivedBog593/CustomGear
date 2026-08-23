package arrivedbog593.ultimatecustomgear.items.containers;

import arrivedbog593.ultimatecustomgear.registry.ContainerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Adds the sliding lid.
 * <p>
 * UNLIKE THE CHEST, this animation is NOT client-only. The collision box grows
 * while the lid rises, so the server has to know how far along it is — which
 * means the progress ticks on both sides and the block's shape is read from
 * here rather than from a constant.
 * <p>
 * Vanilla's own geometry helpers do the maths: Shulker.getProgressAabb for the
 * box at a given progress, and getProgressDeltaAabb for the slice swept between
 * two of them, which is what decides whether there is room to open and what has
 * to be pushed out of the way.
 */
public class CustomShulkerBlockEntity extends CustomContainerBlockEntity {

    /** How far the lid has risen, 0 closed to 1 fully open. */
    private float progress;
    private float progressOld;
    private AnimationStatus animationStatus = AnimationStatus.CLOSED;

    public enum AnimationStatus { CLOSED, OPENING, OPENED, CLOSING }

    public CustomShulkerBlockEntity(BlockPos pos, BlockState state) {
        super(ContainerRegistry.CONTAINER_BE.get(), pos, state);
    }

    /** Interpolated for rendering; the raw value is what the shape uses. */
    public float getProgress(float partialTick) {
        return Mth.lerp(partialTick, progressOld, progress);
    }

    /**
     * Advances the lid one tick, ON BOTH SIDES.
     * <p>
     * 0.1 per tick, so a full open takes ten ticks. While opening, anything
     * standing in the slice the lid is about to occupy gets pushed out — without
     * that, a player on top of a shulker ends up inside the block.
     */
    public static void animateTick(Level level, BlockPos pos, BlockState state,
                                   CustomShulkerBlockEntity be) {
        be.progressOld = be.progress;

        switch (be.animationStatus) {
            case CLOSED -> be.progress = 0.0F;
            case OPENING -> {
                be.progress += 0.1F;
                if (be.progress >= 1.0F) {
                    be.progress = 1.0F;
                    be.animationStatus = AnimationStatus.OPENED;
                    // The shape stopped changing: neighbours have to re-evaluate
                    // theirs, or the collision caches around here stay stale and
                    // the engine keeps nudging whatever stands nearby.
                    state.updateNeighbourShapes(level, pos, 3);
                }
                be.pushEntities(level, pos, state);
            }
            case OPENED -> be.progress = 1.0F;
            case CLOSING -> {
                be.progress -= 0.1F;
                if (be.progress <= 0.0F) {
                    be.progress = 0.0F;
                    be.animationStatus = AnimationStatus.CLOSED;
                    state.updateNeighbourShapes(level, pos, 3);
                }
            }
        }
    }

    /**
     * Shoves entities out of the volume the lid just swept through.
     * <p>
     * The push is the FULL size of that volume on each axis, not half of it:
     * anything less leaves the entity still inside, so the next tick sweeps it
     * again and the player creeps outward for as long as the lid is moving
     * instead of being moved clear once.
     */
    private void pushEntities(Level level, BlockPos pos, BlockState state) {
        if (!state.hasProperty(CustomShulkerBlock.FACING)) return;
        Direction facing = state.getValue(CustomShulkerBlock.FACING);

        AABB swept = Shulker.getProgressDeltaAabb(1.0F, facing, progressOld, progress, pos.getBottomCenter());
        List<Entity> caught = level.getEntities(null, swept);
        if (caught.isEmpty()) return;

        for (Entity entity : caught) {
            if (entity.getPistonPushReaction() == PushReaction.IGNORE) continue;
            entity.move(MoverType.SHULKER_BOX, new Vec3(
                    (swept.getXsize() + 0.01) * facing.getStepX(),
                    (swept.getYsize() + 0.01) * facing.getStepY(),
                    (swept.getZsize() + 0.01) * facing.getStepZ()));
        }
    }

    /** Grows and shrinks with the lid — see the class doc for why. */
    public AABB getBoundingBox(BlockState state) {
        Direction facing = state.hasProperty(CustomShulkerBlock.FACING)
                ? state.getValue(CustomShulkerBlock.FACING) : Direction.UP;
        // Relative to the block corner: getShape wants a shape in block space, and
        // the AABB comes back already positioned around whatever centre it is given.
        return Shulker.getProgressAabb(1.0F, facing, 0.5F * getProgress(1.0F),
                new Vec3(0.5, 0.0, 0.5));
    }

    /**
     * The animation is driven ENTIRELY by the block event, on both sides.
     * <p>
     * startOpen only ever runs on the server — the client's menu is built from
     * a SimpleContainer, not from this block entity — so setting the status
     * there would leave the client's lid shut and its collision box a cube. The
     * player would then be corrected by the server instead of moved locally,
     * which feels exactly like lag.
     */
    @Override
    public boolean triggerEvent(int id, int value) {
        if (id == 1) {
            animationStatus = value > 0 ? AnimationStatus.OPENING : AnimationStatus.CLOSING;
            if (level != null) {
                getBlockState().updateNeighbourShapes(level, worldPosition, 3);
            }
            return true;
        }
        return super.triggerEvent(id, value);
    }
}