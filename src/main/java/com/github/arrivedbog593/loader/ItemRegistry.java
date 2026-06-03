package com.github.arrivedbog593.loader;

import com.github.arrivedbog593.data.ItemData;
import com.github.arrivedbog593.items.items.CustomItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers simple items loaded from JSON.
 * Does not handle tools or armor — those are managed by GearRegistry.
 */
public class ItemRegistry {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, "customgear");

    /** itemId → ItemData for runtime lookups */
    public static final Map<ResourceLocation, ItemData> ITEM_MAP = new HashMap<>();

    public static void register(IEventBus modEventBus, List<ItemData> itemList) {
        for (ItemData data : itemList) {
            ITEMS.register(data.id, () -> new CustomItem(data));
            ITEM_MAP.put(
                    ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
            LOGGER.info("[CustomGear] Item registered: {}", data.id);
        }
        ITEMS.register(modEventBus);
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