package arrivedbog593.ultimatecustomgear.items.tools;

import arrivedbog593.ultimatecustomgear.data.GearData;
import net.minecraft.world.item.Item;

public class CustomToolItem {

    public static Item create(GearData data) {
        return switch (data.type) {
            case "pickaxe" -> new CustomPickaxeItem(data);
            case "axe"     -> new CustomAxeItem(data);
            case "shovel"  -> new CustomShovelItem(data);
            case "hoe"     -> new CustomHoeItem(data);
            default -> throw new IllegalArgumentException("Invalid tool type: " + data.type);
        };
    }
}