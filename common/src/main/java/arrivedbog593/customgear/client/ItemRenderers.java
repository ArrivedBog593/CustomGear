package arrivedbog593.customgear.client;

import arrivedbog593.customgear.items.containers.CustomChestBlockItem;
import arrivedbog593.customgear.items.containers.CustomShulkerBlockItem;
import arrivedbog593.customgear.items.weapons.CustomShieldItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

/**
 * The renderers for items whose model is builtin/entity: chests, shulkers and
 * shields. Each loader attaches them to the items in its own way (NeoForge:
 * IClientItemExtensions; Fabric: BuiltinItemRendererRegistry).
 * <p>
 * ONE renderer per shape, not one per definition. Each sits in a holder class
 * so it is only created the first time a client actually asks for it: the
 * constructors call Minecraft.getInstance(), which does not exist on a
 * dedicated server.
 */
public final class ItemRenderers {

    private ItemRenderers() {}

    /**
     * Whether this item draws with one of these renderers. Creates nothing,
     * so it is safe while registering, before the renderers can exist.
     */
    public static boolean hasRenderer(Item item) {
        return item instanceof CustomChestBlockItem
                || item instanceof CustomShulkerBlockItem
                || item instanceof CustomShieldItem;
    }

    /**
     * The renderer this item draws with, or null when it has a normal model.
     * Call it when rendering, not while registering: the first call creates
     * the renderer.
     */
    public static @Nullable BlockEntityWithoutLevelRenderer forItem(Item item) {
        return switch (item) {
            case CustomChestBlockItem ignored -> Chest.INSTANCE;
            case CustomShulkerBlockItem ignored -> Shulker.INSTANCE;
            case CustomShieldItem ignored -> Shield.INSTANCE;
            default -> null;
        };
    }

    private static final class Chest {
        private static final CustomChestBEWLR INSTANCE = new CustomChestBEWLR();
    }

    private static final class Shulker {
        private static final CustomShulkerBEWLR INSTANCE = new CustomShulkerBEWLR();
    }

    private static final class Shield {
        private static final CustomShieldBEWLR INSTANCE = new CustomShieldBEWLR();
    }
}
