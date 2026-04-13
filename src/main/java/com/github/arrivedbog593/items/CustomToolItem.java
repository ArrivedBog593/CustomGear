package com.github.arrivedbog593.items;

import com.github.arrivedbog593.items.tools.*;
import com.github.arrivedbog593.data.GearData;
import net.minecraft.world.item.Item;

public class CustomToolItem {

    public static Item create(GearData data) {
        return switch (data.type) {
            case "pickaxe" -> new CustomPickaxeItem(data);
            case "axe"     -> new CustomAxeItem(data);
            case "shovel"  -> new CustomShovelItem(data);
            case "hoe"     -> new CustomHoeItem(data);
            default -> throw new IllegalArgumentException("Tool type inválido: " + data.type);
        };
    }
}