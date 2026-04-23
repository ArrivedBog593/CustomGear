package com.github.arrivedbog593.items.items;

import com.github.arrivedbog593.data.ItemData;
import com.github.arrivedbog593.items.gear.CustomSwordItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Generic item built from an ItemData JSON.
 * Items do not emit light in vanilla.
 * <p>
 * JSON properties:
 *  - texture / model → assets
 *  - names           → translatable name
 */
public class CustomItem extends Item {

    private final ItemData itemData;

    public CustomItem(ItemData data) {
        super(new Item.Properties());
        this.itemData = data;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.literal(resolveName(itemData));
    }

    public static String resolveName(ItemData data) {
        if (data.names == null || data.names.isEmpty()) return data.id;
        String lang = CustomSwordItem.getCurrentLang();
        return data.names.getOrDefault(lang,
                data.names.getOrDefault("en_us", data.id));
    }
}