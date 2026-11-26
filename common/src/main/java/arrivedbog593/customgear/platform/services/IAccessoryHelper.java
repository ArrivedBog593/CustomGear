package arrivedbog593.customgear.platform.services;

import arrivedbog593.customgear.items.containers.BackpackAnchor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Accessory slots added by another mod (Curios on NeoForge, Trinkets on
 * Fabric), where a player can wear a backpack. Every method answers as if no
 * such mod were installed when it is not, so callers never check for it
 * themselves.
 */
public interface IAccessoryHelper {

    /** The stack in one accessory slot, or EMPTY when it is gone or there is no accessory mod. */
    ItemStack stackIn(Player player, String slot, int index);

    /** Every worn accessory that is one of this mod's backpacks. */
    List<BackpackAnchor> equippedBackpacks(Player player);
}
