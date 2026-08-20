package arrivedbog593.ultimatecustomgear.items.containers;

import arrivedbog593.ultimatecustomgear.registry.ContainerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A barrel needs nothing beyond the shared inventory: its open state is a
 * blockstate property, so there is no animation to keep anywhere.
 * <p>
 * The class exists anyway so every subtype has one and the block entity
 * supplier has something to return. If it stays empty, that is the point.
 */
public class CustomBarrelBlockEntity extends CustomContainerBlockEntity {

    public CustomBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ContainerRegistry.CONTAINER_BE.get(), pos, state);
    }
}