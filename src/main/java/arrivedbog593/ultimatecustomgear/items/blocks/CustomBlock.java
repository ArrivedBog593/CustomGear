package arrivedbog593.ultimatecustomgear.items.blocks;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import arrivedbog593.ultimatecustomgear.util.BlockSoundResolver;
import arrivedbog593.ultimatecustomgear.util.MapColorResolver;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.UnaryOperator;

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

    public CustomBlock(BlockData data, Properties props) {
        super(buildProperties(data, props));
    }

    /** For subtypes that had to adjust the properties before construction. */
    protected CustomBlock(Properties properties) {
        super(properties);
    }

    /**
     * The base properties come FROM THE REGISTRY rather than from
     * {@code Properties.of()}: a block now carries its own registry id, and the
     * only place that knows it is the DeferredRegister that is about to name it.
     * Building a fresh Properties here would produce a block that throws the
     * moment it is constructed.
     */
    public static Properties buildProperties(BlockData data, Properties base) {
        Properties props = base
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

    /**
     * Overload for subtypes that need properties the JSON does not express.
     * <p>
     * The shulker forced this: its shape changes at runtime, and it stops
     * occluding while the lid is out, and none of that can be set after
     * construction — Properties are frozen the moment the Block is built.
     */
    public static Properties buildProperties(BlockData data, Properties base,
                                             UnaryOperator<Properties> extra) {
        return extra.apply(buildProperties(data, base));
    }
}