package arrivedbog593.customgear.data;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * One "accessory_slots" entry, as each accessory mod names it.
 * <p>
 * Curios names a slot with one word ({@code back}); Trinkets with a group and
 * a slot ({@code chest/back}). A pack writes either form and the same JSON
 * works on both loaders: the slots the two mods ship are translated here, and
 * anything else is used as written for the mod whose form it is in.
 */
public final class AccessorySlots {

    /** The slot types Curios ships. Others may exist if another mod adds them. */
    public static final Set<String> CURIOS_SLOTS = Set.of(
            "back", "belt", "body", "bracelet", "charm",
            "curio", "hands", "head", "necklace", "ring");

    /** The slots Trinkets ships. Others may exist if another mod adds them. */
    public static final Set<String> TRINKETS_SLOTS = Set.of(
            "head/hat", "head/face", "chest/back", "chest/cape", "chest/necklace",
            "legs/belt", "feet/shoes", "feet/aglet",
            "hand/glove", "hand/ring", "offhand/glove", "offhand/ring");

    /** Curios slot → the Trinkets slot that means the same. bracelet, charm and curio have none. */
    private static final Map<String, String> CURIOS_TO_TRINKETS = Map.of(
            "back", "chest/back",
            "belt", "legs/belt",
            "body", "chest/cape",
            "hands", "hand/glove",
            "head", "head/hat",
            "necklace", "chest/necklace",
            "ring", "hand/ring");

    /** Trinkets slot → the Curios slot that means the same. face, shoes and aglet have none. */
    private static final Map<String, String> TRINKETS_TO_CURIOS = Map.of(
            "chest/back", "back",
            "legs/belt", "belt",
            "chest/cape", "body",
            "hand/glove", "hands",
            "offhand/glove", "hands",
            "head/hat", "head",
            "chest/necklace", "necklace",
            "hand/ring", "ring",
            "offhand/ring", "ring");

    private AccessorySlots() {}

    /** Whether the entry is written the Trinkets way, as group/slot. */
    public static boolean isTrinketsForm(String slot) {
        return slot.contains("/");
    }

    /** Whether a group/slot entry has exactly one slash and both halves. */
    public static boolean isWellFormed(String slot) {
        if (!isTrinketsForm(slot)) return true;
        String[] parts = slot.split("/", -1);
        return parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank();
    }

    /** The Curios slot for this entry, or null when Curios has nothing that matches. */
    public static @Nullable String toCurios(String slot) {
        return isTrinketsForm(slot) ? TRINKETS_TO_CURIOS.get(slot) : slot;
    }

    /** The Trinkets slot (group/slot) for this entry, or null when Trinkets has nothing that matches. */
    public static @Nullable String toTrinkets(String slot) {
        return isTrinketsForm(slot) ? slot : CURIOS_TO_TRINKETS.get(slot);
    }

    /** Whether one of the two mods ships this slot itself. */
    public static boolean isKnown(String slot) {
        return isTrinketsForm(slot) ? TRINKETS_SLOTS.contains(slot) : CURIOS_SLOTS.contains(slot);
    }
}
