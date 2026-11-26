package arrivedbog593.customgear.items;

import net.minecraft.world.item.ItemStack;

/**
 * An item whose durability follows its live definition, so a
 * /customgear reload changes it without a restart. Vanilla bakes the
 * durability into the stack's max_damage component when the item is built;
 * ItemStackMixin asks this first.
 */
public interface DynamicDurability {

    /** The max damage from the live definition, or 0 to keep the stack's own. */
    int dynamicMaxDamage(ItemStack stack);
}
