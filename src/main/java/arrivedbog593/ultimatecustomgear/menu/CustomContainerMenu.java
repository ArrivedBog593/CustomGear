package arrivedbog593.ultimatecustomgear.menu;

import arrivedbog593.ultimatecustomgear.items.containers.ContainerNesting;
import arrivedbog593.ultimatecustomgear.items.containers.CustomContainerBlockEntity;
import arrivedbog593.ultimatecustomgear.registry.MenuRegistry;
import arrivedbog593.ultimatecustomgear.network.ContainerOpenData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * One menu for every custom container, parameterised by slot count and column
 * width. There is no per-size menu type and no per-size GUI texture: the screen
 * draws the frame procedurally and scrolls when the rows do not fit.
 * <p>
 * SLOT ORDER matters and is relied on elsewhere: container slots come first
 * (0 .. size-1), then the player inventory, then the hotbar. Ranges are
 * computed from {@code size}, never hardcoded — code copied from a vanilla
 * chest assumes 27 or 54 and would silently move items into the wrong half.
 * <p>
 * Initial slot positions are a plain grid. The screen repositions them while
 * scrolling (moving off-screen slots to y = -100), so the values set here only
 * matter for the first render.
 */
public class CustomContainerMenu extends AbstractContainerMenu {

    private static final int SLOT    = 18;
    private static final int SLOTS_X = 8;
    private static final int SLOTS_Y = 18;

    private final Container container;
    private final int size;
    private final int columns;

    /** Client-side mirror of the block's remembered sort mode. */
    private byte sortCriterion;
    private boolean sortDescending;

    /**
     * The player-inventory slot holding the container being viewed, or -1.
     * <p>
     * A backpack lives in the player's own inventory, so the slot showing it is
     * also a slot the player could drag it out of — and doing that from inside
     * its own screen leaves the menu pointing at nothing. Freezing that one slot
     * is cheaper than handling the aftermath, and stillValid still covers what
     * the player does not control: another player taking it, a hopper, dying.
     * <p>
     * -1 for every placeable container: their inventory is not in the player's.
     */
    private final int lockedSlot;

    /** Server side: the real block entity goes straight in. */
    public CustomContainerMenu(int containerId, Inventory playerInv, Container container,
                               int size, int columns) {
        this(containerId, playerInv, container, size, columns, -1);
    }

    public CustomContainerMenu(int containerId, Inventory playerInv, Container container,
                               int size, int columns, int lockedSlot) {
        super(MenuRegistry.CONTAINER_MENU.get(), containerId);
        this.container  = container;
        this.size       = size;
        this.columns    = Math.max(1, columns);
        this.lockedSlot = lockedSlot;

        checkContainerSize(container, size);
        container.startOpen(playerInv.player);

        addContainerSlots();
        addPlayerSlots(playerInv);
    }

    public static CustomContainerMenu fromNetwork(int containerId, Inventory playerInv,
                                                  RegistryFriendlyByteBuf buf) {
        ContainerOpenData data = ContainerOpenData.read(buf);

        // The client's container is a stand-in for the real one, so the nesting
        // rule has to be baked into it — a plain SimpleContainer accepts
        // everything, and the screen would let the player drop a container in
        // before the server bounced it back.
        Container mirror = data.rejectsContainers()
                ? new SimpleContainer(data.size()) {
            @Override
            public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                return !ContainerNesting.isContainer(stack);
            }
        }
                : new SimpleContainer(data.size());

        CustomContainerMenu menu = new CustomContainerMenu(
                containerId, playerInv, mirror, data.size(), data.columns(), data.lockedSlot());
        menu.sortCriterion  = data.sortCriterion();
        menu.sortDescending = data.sortDescending();
        return menu;
    }


    public byte getSortCriterion()    { return sortCriterion; }
    public boolean isSortDescending() { return sortDescending; }

    public int getSize()    { return size; }
    public int getColumns() { return columns; }

    /** The backing inventory, so the block entity can tell its own menu apart. */
    public Container getContainer() { return container; }

    /** Last row may be partially filled — slots are a count, not a grid. */
    public int getRows()    { return size / columns + (size % columns > 0 ? 1 : 0); }

    /** First index of the player inventory. The screen needs this to lay it out. */
    public int getPlayerSlotStart() { return size; }

    private void addContainerSlots() {
        for (int i = 0; i < size; i++) {
            addSlot(new Slot(container, i,
                    SLOTS_X + (i % columns) * SLOT,
                    SLOTS_Y + (i / columns) * SLOT) {
                /**
                 * A plain Slot accepts everything: Container.canPlaceItem is only
                 * consulted by hoppers and a few vanilla edge cases, never by the
                 * screen. Without this override the nesting rule holds against
                 * automation and fails against a player dragging by hand, which
                 * is the one case it exists for.
                 */
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return container.canPlaceItem(getContainerSlot(), stack);
                }
            });
        }
    }

    private void addPlayerSlots(Inventory inv) {
        int y = SLOTS_Y + getRows() * SLOT + 13;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(playerSlot(inv, col + row * 9 + 9,
                        SLOTS_X + col * SLOT, y + row * SLOT));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(playerSlot(inv, col, SLOTS_X + col * SLOT, y + 3 * SLOT + 4));
        }
    }

    /**
     * mayPickup covers dragging, shift-clicking and dropping with Q; mayPlace
     * stops anything being swapped onto it. Both are needed — either alone
     * leaves a way to get the backpack out of its own screen.
     */
    private Slot playerSlot(Inventory inv, int index, int x, int y) {
        return new Slot(inv, index, x, y) {
            @Override
            public boolean mayPickup(@NotNull Player player) {
                return getContainerSlot() != lockedSlot;
            }

            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return getContainerSlot() != lockedSlot;
            }
        };
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack    = slot.getItem();
        ItemStack original = stack.copy();

        // Ranges derived from size, never from vanilla chest constants.
        int playerStart = size;
        int playerEnd   = size + 36;

        if (index < playerStart) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, playerStart, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    // ── Transfer helpers ────────────────────────────────────────────────────────────────

    /** First index of the player's three main rows. The hotbar sits after them. */
    private int mainInventoryStart() { return size; }
    /** One past the last main row: the hotbar is deliberately excluded. */
    private int mainInventoryEnd()   { return size + 27; }

    /**
     * Bulk transfer between the container and the player's three main rows.
     * <p>
     * THE HOTBAR IS NEVER TOUCHED, in either direction. It holds what the player
     * chose to keep at hand, and a button that empties it into a chest would be
     * a trap.
     *
     * @param everything false to move only item types the destination already
     *                   holds, which is the safe default: it tops up a chest
     *                   without dumping unrelated things into it
     */
    public void transfer(boolean toStorage, boolean everything) {
        int fromStart = toStorage ? mainInventoryStart() : 0;
        int fromEnd   = toStorage ? mainInventoryEnd()   : size;
        int toStart   = toStorage ? 0                    : mainInventoryStart();
        int toEnd     = toStorage ? size                 : mainInventoryEnd();

        boolean moved = false;
        for (int i = fromStart; i < fromEnd; i++) {
            Slot from = slots.get(i);
            ItemStack stack = from.getItem();
            if (stack.isEmpty()) continue;
            if (!everything && !destinationHas(stack, toStart, toEnd)) continue;

            // moveItemStackTo mutates the stack it is given, so the slot has to
            // be told afterwards, exactly as quickMoveStack does.
            int before = stack.getCount();
            if (moveItemStackTo(stack, toStart, toEnd, false) || stack.getCount() != before) {
                from.setByPlayer(stack.isEmpty() ? ItemStack.EMPTY : stack);
                from.setChanged();
                moved = true;
            }
        }
        if (moved) broadcastChanges();
    }

    /** Same test the game uses to stack, so an enchanted tool is not "the same". */
    private boolean destinationHas(ItemStack stack, int start, int end) {
        for (int i = start; i < end; i++) {
            ItemStack there = slots.get(i).getItem();
            if (!there.isEmpty() && ItemStack.isSameItemSameComponents(there, stack)) return true;
        }
        return false;
    }

    // ── Sort helpers ─────────────────────────────────────────────────────────────────────

    /**
     * Merges the container into whole stacks and lays them out in the requested
     * order.
     * <p>
     * THE TOTALS COME FROM HERE, NEVER FROM THE PACKET. The client only says
     * which type goes first; how much of it exists is counted from the real
     * contents. That is what makes a forged order harmless.
     *
     * @param order types in display order, each with a count of one
     */
    public void sort(List<ItemStack> order) {
        // Merge first: three half stacks of stone become one and a half.
        List<ItemStack> merged = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ItemStack stack = slots.get(i).getItem();
            if (stack.isEmpty()) continue;

            ItemStack existing = null;
            for (ItemStack m : merged) {
                if (ItemStack.isSameItemSameComponents(m, stack)) { existing = m; break; }
            }
            if (existing != null) {
                existing.setCount(existing.getCount() + stack.getCount());
            } else {
                merged.add(stack.copy());
            }
        }

        List<ItemStack> ordered = new ArrayList<>(merged.size());
        // Follow the client's sequence, then append anything it did not know
        // about — a hopper may have inserted between the click and the packet.
        for (ItemStack wanted : order) {
            for (Iterator<ItemStack> it = merged.iterator(); it.hasNext(); ) {
                ItemStack m = it.next();
                if (ItemStack.isSameItemSameComponents(m, wanted)) {
                    ordered.add(m);
                    it.remove();
                    break;
                }
            }
        }
        ordered.addAll(merged);

        // Lay out: fill each slot to the stack limit before moving on.
        int slot = 0;
        for (ItemStack stack : ordered) {
            int left = stack.getCount();
            while (left > 0 && slot < size) {
                int put = Math.min(left, stack.getMaxStackSize());
                slots.get(slot).set(stack.copyWithCount(put));
                left -= put;
                slot++;
            }
            // Ran out of slots: the container shrank under us. Nothing is lost —
            // the leftovers stay where the loop stopped writing.
            if (slot >= size && left > 0) return;
        }
        for (; slot < size; slot++) {
            slots.get(slot).set(ItemStack.EMPTY);
        }
        broadcastChanges();
    }

    /**
     * Mirrors the mode locally and, on the server, persists it on the block.
     * The client's container is a SimpleContainer, so the instanceof fails there
     * and only the local fields update — which is all the screen needs.
     */
    public void setSortMode(byte criterion, boolean descending) {
        this.sortCriterion = criterion;
        this.sortDescending = descending;
        if (container instanceof CustomContainerBlockEntity be) {
            be.setSortMode(criterion, descending);
        }
    }
}
