package arrivedbog593.ultimatecustomgear.items.containers;

import arrivedbog593.ultimatecustomgear.registry.ContainerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Adds the animated lid.
 * <p>
 * CLIENT SIDE ONLY, and that is what separates it from the shulker: nothing on
 * the server depends on how far this lid has swung. The server broadcasts an
 * opener count through a block event, and each client interpolates its own.
 */
public class CustomChestBlockEntity extends CustomContainerBlockEntity implements LidBlockEntity {

    private final ChestLidController lidController = new ChestLidController();

    public CustomChestBlockEntity(BlockPos pos, BlockState state) {
        super(ContainerRegistry.CONTAINER_BE.get(), pos, state);
    }

    @Override
    public float getOpenNess(float partialTick) {
        return lidController.getOpenness(partialTick);
    }

    /**
     * Block event 1 carries the opener count, exactly as vanilla's chest does.
     * The openers counter broadcasts it, and every client feeds it to its own
     * lid controller.
     */
    @Override
    public boolean triggerEvent(int id, int value) {
        if (id == 1) {
            lidController.shouldBeOpen(value > 0);
            return true;
        }
        return super.triggerEvent(id, value);
    }

    /** Advances the lid animation. Registered only on the client. */
    public static void lidAnimateTick(CustomChestBlockEntity be) {
        be.lidController.tickLid();
    }
}