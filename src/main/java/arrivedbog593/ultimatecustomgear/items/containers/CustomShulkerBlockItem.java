package arrivedbog593.ultimatecustomgear.items.containers;

import arrivedbog593.ultimatecustomgear.client.CustomShulkerBEWLR;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The item form of a shulker container. Exists only to hand rendering over to a
 * BEWLR — see CustomChestBlockItem for why that is necessary at all.
 */
public class CustomShulkerBlockItem extends CustomContainerBlockItem {

    /**
     * ONE renderer for every shulker item. Held in a holder class so the field
     * is only initialised the first time a client asks for it: the constructor
     * calls Minecraft.getInstance(), which does not exist on a dedicated server.
     */
    private static final class RendererHolder {
        private static final CustomShulkerBEWLR INSTANCE = new CustomShulkerBEWLR();
    }

    public CustomShulkerBlockItem(Block block, Properties properties) {
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