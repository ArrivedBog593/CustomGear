package arrivedbog593.customgear.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

import java.util.List;

/**
 * Keys the mod adds. Each loader registers {@link #ALL}.
 * <p>
 * On NeoForge the container keys are also limited to GUIs (see
 * {@link #GUI_ONLY}). Those bindings only mean anything with one of this mod's
 * containers open, so declaring them GUI-only lets players reuse a key they
 * already have bound for something in the world without a conflict warning.
 * Fabric has no key contexts; the screen reads them itself either way.
 */
public final class CustomGearKeys {

    private static final String CATEGORY = "key.categories.customgear";

    public static final KeyMapping SORT = new KeyMapping(
            "key.customgear.sort",
            InputConstants.Type.MOUSE,
            InputConstants.MOUSE_BUTTON_MIDDLE,
            CATEGORY);

    /**
     * Transfer without pointing at the button. Same two payloads the buttons
     * send, so shift still means "everything" rather than "matching types only".
     * <p>
     * UNBOUND by default. Sorting earns a default because it is the one action
     * with no other way in; these already have visible buttons, and claiming two
     * more keys for a duplicate is rude.
     */
    public static final KeyMapping TRANSFER_IN = new KeyMapping(
            "key.customgear.transfer_in",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY);

    public static final KeyMapping TRANSFER_OUT = new KeyMapping(
            "key.customgear.transfer_out",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY);

    /**
     * Opens a backpack without pointing at it. NOT limited to GUIs, unlike the
     * others: it is meant to work while the player is walking around.
     */
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping(
            "key.customgear.open_backpack",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_B,
            CATEGORY);

    public static final List<KeyMapping> ALL = List.of(SORT, TRANSFER_IN, TRANSFER_OUT, OPEN_BACKPACK);

    /** The keys that only act inside a container screen. */
    public static final List<KeyMapping> GUI_ONLY = List.of(SORT, TRANSFER_IN, TRANSFER_OUT);

    private CustomGearKeys() {}
}
