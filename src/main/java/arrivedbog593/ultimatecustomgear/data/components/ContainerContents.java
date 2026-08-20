package arrivedbog593.ultimatecustomgear.data.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventory carried inside an ItemStack, with no slot cap.
 * <p>
 * WHY NOT minecraft:container: vanilla's component holds at most 256 slots and
 * silently discards anything past that. This is the same model — contents live
 * in the ItemStack, so copying the item copies the contents — minus that limit.
 * <p>
 * SPARSE FORMAT, and that matters. An earlier version stored a plain list of
 * every slot and lost everything past ~256 on world save while surviving a
 * break-and-place, because breaking keeps the object in memory and only saving
 * runs it through the codec. Storing (slot, stack) pairs for non-empty slots
 * only sidesteps any length limit in the list codecs, and as a bonus a mostly
 * empty 5000-slot container costs almost nothing on disk.
 * <p>
 * The declared size travels with the contents so the inventory can be rebuilt
 * at its original width even when every trailing slot was empty.
 * <p>
 * COST OF THIS MODEL: the whole ItemStack travels the network on every sync.
 * With hundreds of slots that is a large packet each time the player moves the
 * item. Workable, and the reason the remaining size limit is the click-packet
 * one, not this.
 */
public record ContainerContents(int size, List<Entry> entries) {

    public static final ContainerContents EMPTY = new ContainerContents(0, List.of());

    /** One occupied slot. */
    public record Entry(int slot, ItemStack stack) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("slot").forGetter(Entry::slot),
                ItemStack.CODEC.fieldOf("item").forGetter(Entry::stack)
        ).apply(i, Entry::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, Entry::slot,
                        ItemStack.STREAM_CODEC, Entry::stack,
                        Entry::new);
    }

    public static final Codec<ContainerContents> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("size").forGetter(ContainerContents::size),
                    Entry.CODEC.listOf().fieldOf("entries").forGetter(ContainerContents::entries)
            ).apply(instance, ContainerContents::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ContainerContents> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ContainerContents::size,
                    Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ContainerContents::entries,
                    ContainerContents::new);

    /** Snapshot of a container's contents, ready to store on an ItemStack. */
    public static ContainerContents from(Container container) {
        List<Entry> found = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) found.add(new Entry(i, stack.copy()));
        }
        return new ContainerContents(container.getContainerSize(), found);
    }

    /**
     * Snapshot including stacks that no longer fit, renumbered onto the slots
     * just past the end. They survive a save between loading and dropping.
     */
    public static ContainerContents from(Container container, List<ItemStack> pending) {
        ContainerContents base = from(container);
        if (pending.isEmpty()) return base;

        List<Entry> all = new ArrayList<>(base.entries());
        int slot = container.getContainerSize();
        for (ItemStack stack : pending) {
            if (!stack.isEmpty()) all.add(new Entry(slot, stack.copy()));
            slot++;
        }
        return new ContainerContents(slot, all);
    }

    /**
     * Writes these contents back into a container.
     * <p>
     * Slots beyond the container's current size are skipped: a stored inventory
     * can be LARGER than the block it is placed into if its JSON changed 'slots'
     * in between. Those stacks are reported by {@link #overflow(int)} so the
     * caller can drop them instead of losing them.
     */
    public void copyInto(Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            container.setItem(i, ItemStack.EMPTY);
        }
        for (Entry e : entries) {
            if (e.slot() >= 0 && e.slot() < container.getContainerSize()) {
                container.setItem(e.slot(), e.stack().copy());
            }
        }
    }

    /** Stacks that would not fit, for the caller to drop and log. */
    public List<ItemStack> overflow(int containerSize) {
        List<ItemStack> extra = new ArrayList<>();
        for (Entry e : entries) {
            if (e.slot() >= containerSize) extra.add(e.stack().copy());
        }
        return extra;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /** Non-empty stacks in slot order, for the tooltip. */
    public List<ItemStack> stacks() {
        return entries.stream().map(Entry::stack).toList();
    }
}
