package arrivedbog593.ultimatecustomgear.items.blocks;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import arrivedbog593.ultimatecustomgear.util.BlockSoundResolver;
import arrivedbog593.ultimatecustomgear.util.MapColorResolver;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Generic block built from a BlockData JSON.
 * <p>
 * JSON properties:
 *  - light_level      → light emission (0-15)
 *  - destroy_time     → time to break with correct tool, in seconds (default: 3.0)
 *  - explosion_resistance → resistance to explosions (default: 3.0, obsidian: 1200)
 *  - sound            → block sound type (default: "stone")
 *  - texture          → model and texture configuration
 *  - names            → translatable name
 */
public class CustomBlock extends Block {

    public CustomBlock(BlockData data) {
        super(buildProperties(data));
    }

    public static Properties buildProperties(BlockData data) {
        Properties props = BlockBehaviour.Properties.of()
                .mapColor(MapColorResolver.resolve(data.mapColor))
                .strength(data.destroyTime, data.explosionResistance)
                .sound(BlockSoundResolver.resolve(data.sound))
                .requiresCorrectToolForDrops();

        if (data.lightLevel > 0) {
            int clamped = Math.clamp(data.lightLevel, 0, 15);
            props = props.lightLevel(state -> clamped);
        }

        return props;
    }
}