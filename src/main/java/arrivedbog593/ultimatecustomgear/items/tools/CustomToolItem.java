package arrivedbog593.ultimatecustomgear.items.tools;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.items.gear.CustomTier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

/**
 * Builds the right tool for a gear definition, and the properties every tool
 * shares.
 * <p>
 * WHY THE PROPERTIES ARE BUILT HERE. A tool is no longer a class — it is an
 * {@code Item} carrying a TOOL component, attribute modifiers and a durability,
 * all applied by one {@code Item.Properties} call per tool type. Keeping that
 * one call in a single place is what stops the four tool classes from drifting
 * apart, which is exactly what happened while each of them built its own
 * {@code createAttributes} arguments.
 */
public class CustomToolItem {

    public static Item create(GearData data, Item.Properties props) {
        return switch (data.type) {
            case "pickaxe" -> new CustomPickaxeItem(data, props);
            case "axe"     -> new CustomAxeItem(data, props);
            case "shovel"  -> new CustomShovelItem(data, props);
            case "hoe"     -> new CustomHoeItem(data, props);
            default -> throw new IllegalArgumentException("Invalid tool type: " + data.type);
        };
    }

    /**
     * The attack numbers are passed as BASELINES, not as totals: vanilla adds
     * the player's own 1.0 damage and the material's damage bonus on top. That
     * is why the JSON's attack_damage arrives here minus one and its
     * attack_speed minus four — the same conversion the 1.21.1 code did through
     * {@code createAttributes}, kept identical so existing packs keep their
     * numbers.
     */
    static Item.Properties applyToolProperties(Item.Properties props, GearData data, String type) {
        ToolMaterial material = CustomTier.of(data);
        float damage = data.attackDamage - 1;
        float speed  = data.attackSpeed - 4;

        Item.Properties p = switch (type) {
            case "pickaxe" -> props.pickaxe(material, damage, speed);
            case "axe"     -> props.axe(material, damage, speed);
            case "shovel"  -> props.shovel(material, damage, speed);
            case "hoe"     -> props.hoe(material, damage, speed);
            default -> throw new IllegalArgumentException("Invalid tool type: " + type);
        };

        if (data.durability > 0) p = p.durability(data.durability);
        return data.fireResistant ? p.fireResistant() : p;
    }
}
