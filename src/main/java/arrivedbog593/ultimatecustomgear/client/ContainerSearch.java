package arrivedbog593.ultimatecustomgear.client;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Parses a search query into a predicate over ItemStacks.
 * <p>
 * The grammar is AE2's, verified against its own {@code RepoSearch}: {@code |}
 * separates OR groups, spaces separate AND terms inside a group, and the first
 * character of a term picks what it matches — {@code @} mod, {@code $} tooltip,
 * {@code #} tag, {@code *} id, nothing at all the display name. Copying the
 * grammar rather than inventing one means anyone who has used AE2 or REI already
 * knows this box.
 * <p>
 * NO CACHE, unlike AE2. Their repo re-tests every entry on every frame, so they
 * need one; here the filter is recomputed only when the query or the contents
 * change, so every stack is tested once per change and a cache would only add a
 * second copy of the work to keep valid.
 * <p>
 * CLIENT ONLY. Tooltips and mod display names do not exist on a dedicated
 * server, and the filter never leaves the screen — the menu keeps working with
 * real slot indices and knows nothing about any of this.
 */
public final class ContainerSearch {

    private ContainerSearch() {
    }

    /**
     * Compiles a query, or returns null when there is nothing to filter by.
     * <p>
     * Null is the signal for "show everything" and callers are expected to take
     * a shortcut on it rather than build an identity filter: with no query the
     * screen behaves exactly as it did before search existed.
     */
    public static @Nullable Predicate<ItemStack> parse(String query,
                                                       Item.TooltipContext tooltipContext,
                                                       @Nullable Player player) {
        if (query == null || query.isBlank()) return null;

        List<Predicate<ItemStack>> orGroups = new ArrayList<>();
        for (String group : query.split("\\|")) {
            List<Predicate<ItemStack>> terms = new ArrayList<>();
            for (String term : group.toLowerCase(Locale.ROOT).trim().split("\\s+")) {
                if (!term.isEmpty()) terms.add(term(term, tooltipContext, player));
            }
            if (!terms.isEmpty()) orGroups.add(all(terms));
        }
        if (orGroups.isEmpty()) return null;

        Predicate<ItemStack> compiled = any(orGroups);
        // An empty slot never matches an active query. This is what makes the
        // compacted view hide the gaps as well as the misses, which is what
        // Sophisticated does and what makes the result readable at 300 slots.
        return stack -> !stack.isEmpty() && compiled.test(stack);
    }

    private static Predicate<ItemStack> term(String term,
                                             Item.TooltipContext tooltipContext,
                                             @Nullable Player player) {
        char prefix = term.charAt(0);
        String rest = term.substring(1);
        // A bare prefix leaves an empty term, which matches everything. AE2 does
        // the same: typing '@' on the way to '@minecraft' should not blank the
        // view for a keystroke.
        return switch (prefix) {
            case '@' -> stack -> matchesMod(stack, rest);
            case '$' -> stack -> matchesTooltip(stack, rest, tooltipContext, player);
            case '#' -> stack -> matchesTag(stack, rest);
            case '*' -> stack -> id(stack).contains(rest);
            default  -> stack -> lower(stack.getHoverName().getString()).contains(term);
        };
    }

    /** Matches the namespace and the mod's display name, so both "@ae2" and "@applied" work. */
    private static boolean matchesMod(ItemStack stack, String term) {
        String namespace = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
        if (namespace.contains(term)) return true;

        return ModList.get().getModContainerById(namespace)
                .map(container -> lower(container.getModInfo().getDisplayName()).contains(term))
                .orElse(false);
    }

    /**
     * A term with a colon is matched against the whole tag id; without one, the
     * namespace and the path are matched separately. Otherwise "#ingot" would
     * miss "c:ingots" for everything whose namespace happens to contain the term.
     */
    private static boolean matchesTag(ItemStack stack, String term) {
        return stack.getTags().anyMatch(tag -> {
            ResourceLocation id = tag.location();
            return term.contains(":")
                    ? id.toString().contains(term)
                    : id.getNamespace().contains(term) || id.getPath().contains(term);
        });
    }

    /**
     * The expensive one: building a tooltip allocates a list of Components per
     * stack. Only paid when a '$' term is actually present in the query.
     */
    private static boolean matchesTooltip(ItemStack stack, String term,
                                          Item.TooltipContext tooltipContext,
                                          @Nullable Player player) {
        for (Component line : stack.getTooltipLines(tooltipContext, player, TooltipFlag.NORMAL)) {
            if (lower(line.getString()).contains(term)) return true;
        }
        return false;
    }

    private static String id(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static String lower(String s) {
        return s.toLowerCase(Locale.ROOT);
    }

    // Plain loops rather than chained Predicate.and/or: at 300 slots per
    // recompute the difference is noise, but a deeply nested lambda chain is
    // genuinely harder to read in a stack trace.

    private static Predicate<ItemStack> all(List<Predicate<ItemStack>> parts) {
        if (parts.size() == 1) return parts.getFirst();
        return stack -> {
            for (Predicate<ItemStack> part : parts) {
                if (!part.test(stack)) return false;
            }
            return true;
        };
    }

    private static Predicate<ItemStack> any(List<Predicate<ItemStack>> parts) {
        if (parts.size() == 1) return parts.getFirst();
        return stack -> {
            for (Predicate<ItemStack> part : parts) {
                if (part.test(stack)) return true;
            }
            return false;
        };
    }
}