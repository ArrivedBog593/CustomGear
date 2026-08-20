package arrivedbog593.ultimatecustomgear.client;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The grid of items a container shows while Shift is held.
 * <p>
 * GROUPED AND SORTED AT CONSTRUCTION, never while drawing. The tooltip is
 * rebuilt every frame the cursor sits on the item, and walking three hundred
 * slots to add up totals sixty times a second is work nobody sees.
 *
 * @param entries one line per distinct type, most numerous first
 * @param hidden  how many types did not fit, for the "and N more" line
 */
public record ContainerTooltip(List<Entry> entries, int hidden) implements TooltipComponent {

    /** How many distinct types are drawn. Three rows of nine, like the storage mods. */
    public static final int MAX_SHOWN = 27;

    public record Entry(ItemStack stack, int total) {}

    /**
     * Builds the grid from a container's contents.
     * <p>
     * Merged by "same item and components", the same test the game uses to
     * stack, so an enchanted tool does not fold into a plain one.
     */
    public static ContainerTooltip of(List<ItemStack> stacks) {
        Map<String, Entry> merged = new LinkedHashMap<>();
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            boolean found = false;
            for (Map.Entry<String, Entry> e : merged.entrySet()) {
                if (ItemStack.isSameItemSameComponents(e.getValue().stack(), stack)) {
                    e.setValue(new Entry(e.getValue().stack(),
                            e.getValue().total() + stack.getCount()));
                    found = true;
                    break;
                }
            }
            if (!found) {
                merged.put(String.valueOf(merged.size()),
                        new Entry(stack.copyWithCount(1), stack.getCount()));
            }
        }

        List<Entry> all = new ArrayList<>(merged.values());
        all.sort(Comparator.comparingInt(Entry::total).reversed());

        int hidden = Math.max(0, all.size() - MAX_SHOWN);
        return new ContainerTooltip(all.subList(0, Math.min(all.size(), MAX_SHOWN)), hidden);
    }
}