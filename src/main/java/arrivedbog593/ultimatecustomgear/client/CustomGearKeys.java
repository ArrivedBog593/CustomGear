package arrivedbog593.ultimatecustomgear.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

/**
 * Keys the mod adds. Only one so far: sorting the open container.
 * <p>
 * GUI context on purpose. The binding only means anything with one of this
 * mod's containers open, so declaring it as GUI-only lets players reuse a key
 * they already have bound for something in the world without a conflict warning.
 */
public final class CustomGearKeys {

    public static final KeyMapping SORT = new KeyMapping(
            "key.customgear.sort",
            KeyConflictContext.GUI,
            InputConstants.Type.MOUSE,
            InputConstants.MOUSE_BUTTON_MIDDLE,
            "key.categories.customgear");

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
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "key.categories.customgear");

    public static final KeyMapping TRANSFER_OUT = new KeyMapping(
            "key.customgear.transfer_out",
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "key.categories.customgear");

    /**
     * Opens a backpack without pointing at it.
     * <p>
     * NOT GUI context, unlike the others: this one is meant to work while the
     * player is walking around, so it needs the universal context to be seen at
     * all outside a screen.
     */
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping(
            "key.customgear.open_backpack",
            KeyConflictContext.UNIVERSAL,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_B,
            "key.categories.customgear");

    private CustomGearKeys() {}

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(SORT);
        event.register(TRANSFER_IN);
        event.register(TRANSFER_OUT);
        event.register(OPEN_BACKPACK);
    }
}