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
 *  - light_level          → light emission (0-15)
 *  - destroy_time         → time to break with correct tool, in seconds (default: 3.0)
 *  - explosion_resistance → resistance to explosions (default: 3.0, obsidian: 1200)
 *  - sound                → block sound type (default: "stone")
 *  - required_tool        → tool that mines efficiently (see BlockTagLoader)
 *  - harvest_level        → tool tier required for drops (see BlockTagLoader)
 *  - texture              → model and texture configuration
 *  - names                → translatable name
 */
public class CustomBlock extends Block {

    public CustomBlock(BlockData data) {
        super(buildProperties(data));
    }

    public static Properties buildProperties(BlockData data) {
        Properties props = BlockBehaviour.Properties.of()
                .mapColor(MapColorResolver.resolve(data.mapColor))
                .strength(data.destroyTime, data.explosionResistance)
                .sound(BlockSoundResolver.resolve(data.sound));

        // Only gate drops when the block actually declares a tool/tier
        // requirement. Unconditional requiresCorrectToolForDrops() would make
        // blocks with NO mineable tag drop nothing with ANY tool.
        // "sword" is excluded: it only speeds up mining (sword_efficient tag),
        // it never gates drops — gating would leave the block lootless too.
        boolean gatesDrops = data.harvestLevel > 0;
        if (gatesDrops) {
            props = props.requiresCorrectToolForDrops();
        }

        if (data.lightLevel > 0) {
            int clamped = Math.clamp(data.lightLevel, 0, 15);
            props = props.lightLevel(state -> clamped);
        }

        return props;
    }
}