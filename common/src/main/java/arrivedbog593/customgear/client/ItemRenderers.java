package arrivedbog593.customgear.client;

import arrivedbog593.customgear.Constants;
import arrivedbog593.customgear.items.containers.CustomChestBlockItem;
import arrivedbog593.customgear.items.containers.CustomShulkerBlockItem;
import arrivedbog593.customgear.items.weapons.CustomShieldItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
     * This mod's items that draw with one of these renderers, for each loader
     * to attach them to. Creates no renderer, so it is safe while registering,
     * before the renderers can exist.
     */
    public static List<Item> itemsWithRenderers() {
        List<Item> items = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (Constants.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(item).getNamespace())
                    && hasRenderer(item)) {
                items.add(item);
            }
        }
        return items;
    }

    private static boolean hasRenderer(Item item) {
        return item instanceof CustomChestBlockItem
                || item instanceof CustomShulkerBlockItem
                || item instanceof CustomShieldItem;
    }

    /**
     * The renderer this item draws with. Only for items from
     * {@link #itemsWithRenderers()}: any other item throws.
     * Call it when rendering, not while registering: the first call creates
     * the renderer.
     */
    public static @NotNull BlockEntityWithoutLevelRenderer forItem(Item item) {
        return switch (item) {
            case CustomChestBlockItem ignored -> Chest.INSTANCE;
            case CustomShulkerBlockItem ignored -> Shulker.INSTANCE;
            case CustomShieldItem ignored -> Shield.INSTANCE;
            default -> throw new IllegalArgumentException(item + " has no item renderer");
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
