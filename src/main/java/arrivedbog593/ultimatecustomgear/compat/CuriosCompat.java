package arrivedbog593.ultimatecustomgear.compat;

import arrivedbog593.ultimatecustomgear.items.containers.BackpackAnchor;
import arrivedbog593.ultimatecustomgear.items.containers.CustomBackpackItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.ArrayList;
import java.util.List;

/**
 * THE ONLY CLASS THAT TOUCHES THE CURIOS API.
 * <p>
 * Curios is an optional dependency, so every reference to it has to sit behind
 * a loaded check — and behind a class that is never touched when it is absent.
 * Classes that only need "is there a backpack in a curio slot" call in here and
 * stay free of the import; a stray CuriosApi reference anywhere else would
 * throw NoClassDefFoundError the moment someone played without the mod.
 */
public final class CuriosCompat {

    private static Boolean loaded;

    private CuriosCompat() {}

    public static boolean isLoaded() {
        if (loaded == null) loaded = ModList.get().isLoaded("curios");
        return loaded;
    }

    /** The stack in one curio slot, or EMPTY when it is gone or Curios is not here. */
    public static ItemStack stackIn(Player player, String identifier, int index) {
        if (!isLoaded()) return ItemStack.EMPTY;
        return CuriosApi.getCuriosInventory(player)
                .flatMap(inv -> inv.findCurio(identifier, index))
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    /**
     * Every backpack the player has equipped, as anchors ready to open.
     * <p>
     * Returns anchors rather than stacks because the caller needs to remember
     * WHERE it found one: the menu re-resolves that position every tick to
     * notice the backpack being taken off.
     */
    public static List<BackpackAnchor> equippedBackpacks(Player player) {
        if (!isLoaded()) return List.of();

        List<BackpackAnchor> found = new ArrayList<>();
        CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
            for (SlotResult result : inv.findCurios(
                    stack -> stack.getItem() instanceof CustomBackpackItem)) {
                found.add(new BackpackAnchor.CuriosSlot(
                        result.slotContext().identifier(), result.slotContext().index()));
            }
        });
        return found;
    }
}