package arrivedbog593.ultimatecustomgear.items.containers;

import arrivedbog593.ultimatecustomgear.client.CustomChestBEWLR;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The item form of a chest-shaped container.
 * <p>
 * Exists only to hand rendering over to a BEWLR. The block reports
 * ENTITYBLOCK_ANIMATED and its item model is builtin/entity, so without this the
 * item is invisible in the hand, the inventory, on the ground and in item
 * frames. A barrel needs none of this — it has a real baked model.
 */
public class CustomChestBlockItem extends CustomContainerBlockItem {

    /**
     * ONE renderer for every chest item, not one per definition. Held in a
     * holder class so the field is only initialised the first time a client
     * actually asks for it: the constructor calls Minecraft.getInstance(), which
     * does not exist on a dedicated server.
     */
    private static final class RendererHolder {
        private static final CustomChestBEWLR INSTANCE = new CustomChestBEWLR();
    }

    public CustomChestBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @SuppressWarnings("removal")
    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return RendererHolder.INSTANCE;
            }
        });
    }
}