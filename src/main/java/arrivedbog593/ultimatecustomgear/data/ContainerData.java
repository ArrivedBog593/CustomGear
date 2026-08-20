package arrivedbog593.ultimatecustomgear.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Locale;

/**
 * Configuration of a container, shared by all four variants.
 * <p>
 * Declared as the {@code container} object inside a {@code type: "container"}
 * file. The {@code type} inside it picks the variant, and it is REQUIRED: the
 * four differ in shape, in how they are placed, and in whether breaking one
 * keeps its contents. A silent default would decide the last of those without
 * the author knowing.
 * <p>
 * WHAT EACH VARIANT IS:
 * <ul>
 *   <li>{@code barrel} — a full cube, placed facing any of six directions,
 *       whose open state is a blockstate property. Fully describable in
 *       generated JSON: no block entity renderer.</li>
 *   <li>{@code chest} — the vanilla chest shape, smaller than a full block,
 *       with an animated lid. Needs a block entity renderer, and its texture
 *       is a 64x64 unwrap rather than six faces. Can join into a double.</li>
 *   <li>{@code shulker} — like a chest, but attaches to any face and its
 *       collision box grows while opening, so it refuses to open when
 *       blocked.</li>
 *   <li>{@code backpack} — not a block at all. An item that carries its
 *       inventory with it.</li>
 * </ul>
 * <p>
 * Slot count is baked at registration, so changing {@code slots} needs a
 * restart. Everything else here applies with /customgear reload.
 */
public class ContainerData {

    public static final String KIND_BARREL   = "barrel";
    public static final String KIND_CHEST    = "chest";
    public static final String KIND_SHULKER  = "shulker";
    public static final String KIND_BACKPACK = "backpack";

    /** Every accepted value of {@code container.type}, for error messages. */
    public static final String[] KINDS = {
            KIND_BARREL, KIND_CHEST, KIND_SHULKER, KIND_BACKPACK
    };

    /**
     * HARD PROTOCOL LIMIT, not a design choice.
     * <p>
     * ServerboundContainerClickPacket carries the slots changed by one click in
     * a map whose codec rejects more than 128 entries. A single drag across a
     * larger container produces more than that in one packet and DISCONNECTS
     * the player with an EncoderException — verified in game at 169 and at 201.
     * <p>
     * Getting past this means replacing vanilla's click handling with a custom
     * packet, which is a feature of its own and not a tweak.
     */
    public static final int CLICK_PACKET_LIMIT = 128;

    /** Required. One of {@link #KINDS}. */
    @SerializedName("type")
    public String kind;

    /**
     * Total number of slots, at least 1. This is the real value, NOT rows times
     * columns: the last row is allowed to be partially filled.
     * <p>
     * {@link #CLICK_PACKET_LIMIT} is a WARNING threshold, not a ceiling — see there for
     * what going past it costs.
     */
    public int slots;

    /**
     * Curios slot identifiers this container can be equipped in — {@code "back"},
     * {@code "charm"}, {@code "ring"}, or whatever a pack has configured.
     * <p>
     * BACKPACK ONLY, and empty by default: without it the container never
     * appears in a Curios slot at all. Declaring {@code "curio"} puts it in the
     * generic slot, which is a category rather than a wildcard — Curios still
     * requires the item to be tagged for it, which is exactly what this field
     * generates.
     * <p>
     * The identifiers are NOT validated: which slots exist depends on which mods
     * the player has installed, so a name this mod does not recognise may still
     * be perfectly correct.
     */
    @SerializedName("curios_slots")
    public List<String> curiosSlots;

    public List<String> curiosSlots() {
        return curiosSlots == null ? List.of() : curiosSlots;
    }

    /**
     * Slots per row. Presentation only — it does not change how many slots exist
     * or in what order they are stored.
     * <p>
     * OMITTED, the width is worked out from the slot count so the contents fit
     * in nine rows without scrolling, capped by {@link #maxColumns}. Declared,
     * it is used exactly and the formula is off: an author who writes a width
     * wants that width.
     * <p>
     * No hard maximum. There is no horizontal scrolling, so a value too wide for
     * the player's window cannot simply be drawn: the screen re-flows the same
     * slots into fewer columns instead, so no slot is ever unreachable. Up to 12
     * fits every configuration; beyond roughly 18 it depends on the player's
     * resolution and GUI scale.
     */
    public Integer columns;

    /**
     * Ceiling for the automatic width, ignored when {@code columns} is declared.
     * <p>
     * Twelve by default, which is where Sophisticated Storage stops and the
     * widest window known to fit at any resolution and GUI scale. Raising it is
     * allowed and is the author's call to make.
     */
    @SerializedName("max_columns")
    public Integer maxColumns;

    public int maxColumns() {
        return maxColumns == null || maxColumns < 1 ? 12 : maxColumns;
    }

    /**
     * The declared width, or the automatic one for a container of {@code size}
     * slots — which is NOT always {@code slots}: half of a double chest holds
     * twice its definition, and the window widens with it.
     * <p>
     * Nine rows is the target because that is the tallest grid that still leaves
     * room for the player inventory on a standard window. Past the cap the rows
     * keep growing and the scrollbar takes over.
     */
    public int columnsFor(int size) {
        if (columns != null && columns >= 1) return columns;
        int needed = size / 9 + (size % 9 > 0 ? 1 : 0);
        return Math.clamp(needed, 9, maxColumns());
    }

    /** The width for a container at its declared size. */
    public int columns() {
        return columnsFor(slots);
    }

    /**
     * When true, breaking the block keeps the contents inside the dropped item
     * instead of spilling them.
     * <p>
     * Defaults to the variant: true for {@code shulker} and {@code backpack},
     * false for {@code barrel} and {@code chest}. Set it explicitly to override
     * — a barrel that keeps its contents is perfectly legal.
     * <p>
     * A chest that keeps its contents CANNOT join into a double: there would be
     * no answer to which half the dropped item carries.
     */
    @SerializedName("keeps_contents")
    public Boolean keepsContents;

    /**
     * When false, the container refuses to open if a solid block sits directly
     * above it.
     * <p>
     * Defaults to the variant: false for {@code chest} and {@code shulker},
     * true for {@code barrel}. A shulker enforces this for a second reason
     * beyond fidelity — its collision box grows while opening, so opening
     * against a block would push whatever stands on it.
     */
    @SerializedName("openable_when_obstructed")
    public Boolean openableWhenObstructed;

    // ── Resolved accessors: defaults live here, not scattered through the code ──

    /** Never null once validation has passed; may be an unknown string before. */
    public String kind() {
        return kind == null ? "" : kind.toLowerCase(Locale.ROOT).trim();
    }

    public static boolean isKnownKind(String kind) {
        for (String k : KINDS) {
            if (k.equals(kind)) return true;
        }
        return false;
    }

    public boolean keepsContents() {
        if (keepsContents != null) return keepsContents;
        String k = kind();
        return KIND_SHULKER.equals(k) || KIND_BACKPACK.equals(k);
    }

    public boolean openableWhenObstructed() {
        if (openableWhenObstructed != null) return openableWhenObstructed;
        String k = kind();
        return !KIND_CHEST.equals(k) && !KIND_SHULKER.equals(k);
    }

    /** False only for the backpack, which is an item and has no block form. */
    public boolean isBlock() {
        return !KIND_BACKPACK.equals(kind());
    }

    /**
     * Only a chest joins into a double, and only when breaking it spills — see
     * {@link #keepsContents}.
     */
    public boolean canDouble() {
        return KIND_CHEST.equals(kind()) && !keepsContents();
    }

    /** Rows needed to lay the DECLARED slots out, the last one possibly incomplete. */
    public int rows() {
        int c = columns();
        return slots / c + (slots % c > 0 ? 1 : 0);
    }
}