package arrivedbog593.ultimatecustomgear.items.blocks;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Generic block built from a BlockData JSON.
 * <p>
 * JSON properties:
 *  - lightLevel → light emission
 *  - requiredTool + toolLevel → controlled via block tags (see BlockRegistry)
 *  - texture / model → runtime-generated assets
 *  - names → translatable name
 */
public class CustomBlock extends Block {

    public CustomBlock(BlockData data) {
        super(buildProperties(data));
    }

    public static Properties buildProperties(BlockData data) {
        Properties props = BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(3.0f, 3.0f)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops();

        if (data.lightLevel > 0) {
            int clamped = Math.clamp(data.lightLevel, 0, 15);
            props = props.lightLevel(state -> clamped);
        }

        return props;
    }
}