package arrivedbog593.ultimatecustomgear.items.containers;

import arrivedbog593.ultimatecustomgear.compat.CuriosCompat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Where the backpack a menu is showing actually lives.
 * <p>
 * WHY AN ABSTRACTION AND NOT AN INDEX: a backpack has no block entity, so the
 * menu operates on an ItemStack — and that stack can move, be taken by another
 * player, or drop on death while the screen is still open. The menu asks this
 * every tick whether its backpack is still there, and closes when it is not.
 * <p>
 * The offhand needs its own case because it is not one of the 36 numbered
 * inventory slots. Curios, if it ever lands, is a third case rather than a
 * rewrite.
 */
public sealed interface BackpackAnchor {

    /** The stack this anchor points at right now, or EMPTY if it is gone. */
    ItemStack resolve(Player player);

    /**
     * A numbered slot of the player's own inventory — hotbar included, which is
     * what makes right-clicking with the backpack in hand just a special case of
     * opening it from a slot.
     */
    record InventorySlot(int index) implements BackpackAnchor {
        @Override
        public ItemStack resolve(Player player) {
            if (index < 0 || index >= player.getInventory().getContainerSize()) {
                return ItemStack.EMPTY;
            }
            return player.getInventory().getItem(index);
        }
    }

    /** The offhand, which lives outside the numbered slots. */
    record Offhand() implements BackpackAnchor {
        @Override
        public ItemStack resolve(Player player) {
            return player.getOffhandItem();
        }
    }

    /**
     * A Curios slot, addressed by its identifier and index.
     * <p>
     * Goes through the compat layer rather than the API directly, so this record
     * can exist and be compared without Curios on the classpath.
     */
    record CuriosSlot(String identifier, int index) implements BackpackAnchor {
        @Override
        public ItemStack resolve(Player player) {
            return CuriosCompat.stackIn(player, identifier, index);
        }
    }
}