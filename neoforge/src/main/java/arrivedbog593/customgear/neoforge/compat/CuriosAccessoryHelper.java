package arrivedbog593.customgear.neoforge.compat;

import arrivedbog593.customgear.items.containers.BackpackAnchor;
import arrivedbog593.customgear.items.containers.CustomBackpackItem;
import arrivedbog593.customgear.platform.services.IAccessoryHelper;
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
 * a loaded check — and behind a class whose Curios code never runs when it is
 * absent. Everything else goes through IAccessoryHelper and stays free of the
 * import; a stray CuriosApi reference anywhere else would throw
 * NoClassDefFoundError the moment someone played without the mod.
 */
public final class CuriosAccessoryHelper implements IAccessoryHelper {

    private Boolean missing;

    private boolean isMissing() {
        if (missing == null) missing = !ModList.get().isLoaded("curios");
        return missing;
    }

    @Override
    public ItemStack stackIn(Player player, String slot, int index) {
        if (isMissing()) return ItemStack.EMPTY;
        return CuriosApi.getCuriosInventory(player)
                .flatMap(inv -> inv.findCurio(slot, index))
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    /**
     * Returns anchors rather than stacks because the caller needs to remember
     * WHERE it found one: the menu re-resolves that position every tick to
     * notice the backpack being taken off.
     */
    @Override
    public List<BackpackAnchor> equippedBackpacks(Player player) {
        if (isMissing()) return List.of();

        List<BackpackAnchor> found = new ArrayList<>();
        CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
            for (SlotResult result : inv.findCurios(
                    stack -> stack.getItem() instanceof CustomBackpackItem)) {
                found.add(new BackpackAnchor.AccessorySlot(
                        result.slotContext().identifier(), result.slotContext().index()));
            }
        });
        return found;
    }
}
