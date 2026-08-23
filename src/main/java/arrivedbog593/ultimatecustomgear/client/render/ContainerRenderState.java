package arrivedbog593.ultimatecustomgear.client.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jetbrains.annotations.Nullable;

/**
 * What one container looks like this frame, snapshotted off the block entity.
 * <p>
 * WHY A STATE OBJECT AT ALL. A block entity renderer no longer draws during the
 * world pass: it EXTRACTS what it needs on the main thread and submits later,
 * off that thread. Reading the block entity inside submit would be a data race,
 * so everything the drawing needs has to be copied here first.
 * <p>
 * One state serves both shapes because one renderer serves both — see
 * ChestRenderer. A barrel produces a state whose shape is NONE and draws
 * nothing at all: its model is baked like any block's.
 */
public class ContainerRenderState extends BlockEntityRenderState {

    public enum Shape { NONE, CHEST, SHULKER }

    public Shape shape = Shape.NONE;

    /** Chest and shulker both need it, for different rotations. */
    public Direction facing = Direction.NORTH;

    /** Which of the three chest meshes to use. Ignored by a shulker. */
    public ChestType chestType = ChestType.SINGLE;

    /** Lid position: a chest hinge angle, or how far a shulker has risen. */
    public float openness;

    public @Nullable SpriteId sprite;
}
