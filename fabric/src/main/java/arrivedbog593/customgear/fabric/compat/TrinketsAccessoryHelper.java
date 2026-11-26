package arrivedbog593.customgear.fabric.compat;

import arrivedbog593.customgear.items.containers.BackpackAnchor;
import arrivedbog593.customgear.items.containers.CustomBackpackItem;
import arrivedbog593.customgear.platform.services.IAccessoryHelper;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * THE ONLY CLASS THAT TOUCHES THE TRINKETS API.
 * <p>
 * Trinkets is an optional dependency, so every reference to it has to sit
 * behind a loaded check — and behind a class whose Trinkets code never runs
 * when it is absent. Everything else goes through IAccessoryHelper and stays
 * free of the import.
 * <p>
 * Slots are addressed as Trinkets names them, group/slot ("chest/back"): the
 * anchors this hands out carry that id, and {@link #stackIn} reads it back.
 */
public final class TrinketsAccessoryHelper implements IAccessoryHelper {

    private Boolean missing;

    private boolean isMissing() {
        if (missing == null) missing = !FabricLoader.getInstance().isModLoaded("trinkets");
        return missing;
    }

    @Override
    public ItemStack stackIn(Player player, String slot, int index) {
        if (isMissing()) return ItemStack.EMPTY;
        String[] parts = slot.split("/", 2);
        if (parts.length != 2) return ItemStack.EMPTY;

        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        if (component.isEmpty()) return ItemStack.EMPTY;

        Map<String, TrinketInventory> group = component.get().getInventory().get(parts[0]);
        TrinketInventory inventory = group == null ? null : group.get(parts[1]);
        if (inventory == null || index < 0 || index >= inventory.getContainerSize()) {
            return ItemStack.EMPTY;
        }
        return inventory.getItem(index);
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
        TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
            for (Tuple<SlotReference, ItemStack> equipped : component.getEquipped(
                    stack -> stack.getItem() instanceof CustomBackpackItem)) {
                SlotReference reference = equipped.getA();
                found.add(new BackpackAnchor.AccessorySlot(
                        reference.inventory().getSlotType().getId(), reference.index()));
            }
        });
        return found;
    }
}
