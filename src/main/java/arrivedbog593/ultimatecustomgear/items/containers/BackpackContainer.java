package arrivedbog593.ultimatecustomgear.items.containers;

import arrivedbog593.ultimatecustomgear.data.components.ContainerContents;
import arrivedbog593.ultimatecustomgear.registry.ComponentRegistry;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The inventory of a carried backpack.
 * <p>
 * A SimpleContainer loaded from the item's component and written back on every
 * change, rather than a Container reading the component directly: writing the
 * whole component costs the same either way, and this keeps every slot access
 * cheap instead of only the reads.
 * <p>
 * IDENTITY, NOT POSITION. stillValid re-resolves the anchor and compares the
 * stack by reference — an ItemStack moved to another slot is the same object,
 * but a different backpack in the same slot is not. That single check covers
 * dragging it elsewhere, another player taking it, a hopper pulling it, and
 * dying with the screen open.
 */
public class BackpackContainer extends SimpleContainer {

    private final BackpackAnchor anchor;
    private final ItemStack backpack;
    private final Player owner;

    public BackpackContainer(Player owner, BackpackAnchor anchor, ItemStack backpack, int size) {
        super(size);
        this.owner = owner;
        this.anchor = anchor;
        this.backpack = backpack;

        ContainerContents stored = backpack.get(ComponentRegistry.CONTAINER_CONTENTS.get());
        if (stored != null) stored.copyInto(this);
    }

    /**
     * Persists straight back into the item.
     * <p>
     * The cost of this model, spelled out: the whole ItemStack is re-sent to the
     * client every time this runs, because it lives in the player's inventory.
     * Cheap for a small backpack, and the reason a large one warns at parse time.
     */
    @Override
    public void setChanged() {
        super.setChanged();
        backpack.set(ComponentRegistry.CONTAINER_CONTENTS.get(), ContainerContents.from(this));
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return player == owner && anchor.resolve(player) == backpack;
    }

    /**
     * A backpack refuses to hold another container, for the same reason a
     * shulker does — see ContainerNesting.
     */
    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return !ContainerNesting.isContainer(stack);
    }
}