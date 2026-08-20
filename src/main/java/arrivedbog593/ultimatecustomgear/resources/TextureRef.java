package arrivedbog593.ultimatecustomgear.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Works out what a texture value in a JSON actually points at.
 * <p>
 * ONE PLACE, called by both the parser and the generators. The rule used to
 * live in a per-object {@code mode} field, which meant every value in an object
 * had to be the same kind — a barrel with its own top and vanilla sides was
 * impossible. Deducing it per value removes that limit and the field with it.
 * <p>
 * THE RULE, in order:
 * <ol>
 *   <li>Contains {@code :} → a resource location, used as written.</li>
 *   <li>Last segment has an extension → a file shipped beside the JSON.</li>
 *   <li>Neither → REJECTED. {@code item/bundle} is ambiguous: as a resource
 *       location it means {@code minecraft:item/bundle}, as a path it looks
 *       like a file someone forgot the extension on. Guessing either way is
 *       worse than saying so.</li>
 * </ol>
 * The extension is not required to be {@code .png}: armor_3d points at
 * {@code .geo.json} and {@code .animation.json} through this same path.
 */
public final class TextureRef {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    /** What a value turned out to be. */
    public enum Kind {
        /** namespace:path — someone else's asset, or vanilla's. */
        REFERENCE,
        /** A file in the content root, to be copied into the dynamic pack. */
        FILE,
        /** Neither form. The caller falls back and the author gets an error. */
        INVALID
    }

    private TextureRef() {}

    public static Kind kindOf(String value) {
        if (value == null || value.isBlank()) return Kind.INVALID;
        if (value.contains(":")) return Kind.REFERENCE;

        int slash = value.lastIndexOf('/');
        int dot   = value.lastIndexOf('.');
        return dot > slash ? Kind.FILE : Kind.INVALID;
    }

    /**
     * Expands the short form of a texture reference to a full asset path.
     * <p>
     * {@code minecraft:entity/chest/christmas} becomes
     * {@code minecraft:textures/entity/chest/christmas.png}. Both forms are
     * accepted everywhere a value means a TEXTURE, because the short one is what
     * the model system takes and the long one is what the texture manager takes
     * — and an author has no reason to know which of the two is reading their
     * value.
     * <p>
     * NOT for values that mean a MODEL, and NOT for fluids: a tool's reference
     * points at minecraft:item/diamond_pickaxe, which is a model and has no long
     * form, and a fluid sprite is addressed in the short form by the atlas.
     */
    public static String expandTexturePath(String value) {
        if (value == null || value.endsWith(".png")) return value;
        int colon = value.indexOf(':');
        if (colon < 0) return value;
        return value.substring(0, colon + 1) + "textures/" + value.substring(colon + 1) + ".png";
    }

    /**
     * Reports a value that is neither form, naming both so the author can pick.
     *
     * @param owner what the value belongs to, for the message — "block 'x'"
     * @param key   which key held it, e.g. "top"
     */
    public static void reportInvalid(String owner, String key, String value) {
        LOGGER.error("[CustomGear] {}: cannot tell what '{}' is in '{}'. Use 'namespace:path' "
                        + "for another mod's or vanilla's asset (minecraft:block/stone), or "
                        + "'path/file.png' for a file in your content folder.",
                owner, value, key);
    }

}