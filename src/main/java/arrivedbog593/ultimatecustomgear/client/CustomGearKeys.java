package arrivedbog593.ultimatecustomgear.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
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

    /**
     * The category is no longer a bare translation key: it is a registered
     * object with an id of its own, so it has to exist before any mapping names
     * it — and be handed to the event, or the mappings land in a category the
     * controls screen never draws.
     */
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath("customgear", "general"));

    public static final KeyMapping SORT = new KeyMapping(
            "key.customgear.sort",
            KeyConflictContext.GUI,
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
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY);

    public static final KeyMapping TRANSFER_OUT = new KeyMapping(
            "key.customgear.transfer_out",
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY);

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
            CATEGORY);

    private CustomGearKeys() {}

    public static void register(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(SORT);
        event.register(TRANSFER_IN);
        event.register(TRANSFER_OUT);
        event.register(OPEN_BACKPACK);
    }
}