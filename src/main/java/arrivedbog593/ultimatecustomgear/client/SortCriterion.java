package arrivedbog593.ultimatecustomgear.client;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * How the sort button orders a container's contents.
 * <p>
 * CLIENT SIDE, and that is the point. Sorting by display name needs the
 * language files, which only the client has: a dedicated server would sort
 * everything in its own locale, which is what the storage mods this is modelled
 * on actually do. Here the client works out the order and the server only
 * applies it, so a Spanish player gets a Spanish alphabet on any server.
 * <p>
 * Every comparator ends in the registry id. Without that tiebreak two items
 * with the same name land in whatever order the map iterated, and sorting twice
 * gives different results — which reads as a bug even though nothing is lost.
 */
public enum SortCriterion {

    NAME(Comparator.comparing(SortCriterion::displayName)),

    MOD(Comparator.<ItemStack, String>comparing(s -> registryId(s).getNamespace())
            .thenComparing(SortCriterion::displayName)),

    COUNT(null),   // needs the totals, so it is built per sort — see comparator(Map)

    TAG(Comparator.comparing(SortCriterion::firstTag)
            .thenComparing(SortCriterion::displayName));

    private final Comparator<ItemStack> comparator;

    SortCriterion(Comparator<ItemStack> comparator) {
        this.comparator = comparator == null ? null
                : comparator.thenComparing(s -> registryId(s).toString());
    }

    /** The letter drawn on the criterion button, translated so each language picks its own. */
    public Component letter() {
        return Component.translatable("customgear.container.sort." + name().toLowerCase(Locale.ROOT) + ".letter");
    }

    public Component label() {
        return Component.translatable("customgear.container.sort." + name().toLowerCase(Locale.ROOT));
    }

    public SortCriterion next() {
        return values()[(ordinal() + 1) % values().length];
    }

    /** Wire form: an ordinal that survives an unknown value from an older client. */
    public static SortCriterion byId(int id) {
        return id >= 0 && id < values().length ? values()[id] : NAME;
    }

    private static String displayName(ItemStack stack) {
        return stack.getHoverName().getString().toLowerCase(Locale.ROOT);
    }

    private static Identifier registryId(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    /**
     * Sorted so the choice does not depend on tag iteration order. Items with no
     * tags sort last rather than first: an untagged oddity at the top would push
     * everything the player was looking for down the grid.
     */
    private static String firstTag(ItemStack stack) {
        List<TagKey<Item>> tags = stack.typeHolder().tags().sorted(Comparator.comparing(t -> t.location().toString())).toList();
        return tags.isEmpty() ? "\uFFFF" : tags.getFirst().location().toString();
    }

    /**
     * The comparator for a given set of totals. Only COUNT needs them — the other
     * three can rank a stack in isolation, but "most of" is a property of the
     * whole container.
     */
    public Comparator<ItemStack> comparator(Map<ItemStack, Integer> totals) {
        if (this != COUNT) return comparator;
        return Comparator.<ItemStack, Integer>comparing(s -> -totals.getOrDefault(s, 0))
                .thenComparing(s -> registryId(s).toString());
    }
}