package arrivedbog593.customgear.registry;

import arrivedbog593.customgear.data.ItemData;
import arrivedbog593.customgear.items.items.CustomFoodItem;
import arrivedbog593.customgear.items.items.CustomItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import arrivedbog593.customgear.platform.registration.RegistrationProvider;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers simple items and food items loaded from JSON.
 * Does not handle tools or armor — those are managed by GearRegistry.
 */
public class ItemRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger("CustomGear");

    public static final RegistrationProvider<Item> ITEMS =
            RegistrationProvider.get(Registries.ITEM);

    /** itemId → ItemData for runtime lookups */
    public static final Map<ResourceLocation, ItemData> ITEM_MAP = new HashMap<>();

    public static void register(List<ItemData> itemList) {
        for (ItemData data : itemList) {
            if ("food".equals(data.type)) {
                ITEMS.register(data.id, () -> new CustomFoodItem(data));
                LOGGER.info("[CustomGear] Food item registered: {}", data.id);
            } else {
                ITEMS.register(data.id, () -> new CustomItem(data));
                LOGGER.info("[CustomGear] Item registered: {}", data.id);
            }
            ITEM_MAP.put(
                    ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
        }
    }

    /**
     * Updates item data during reload without restarting the game.
     * Allows refreshing names and other runtime properties.
     */
    public static void updateItemData(List<ItemData> itemList) {
        ITEM_MAP.clear();
        for (ItemData data : itemList) {
            ITEM_MAP.put(ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
        }
        LOGGER.info("[CustomGear] Updated {} items in registry", itemList.size());
    }
}