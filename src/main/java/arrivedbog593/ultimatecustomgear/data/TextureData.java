package arrivedbog593.ultimatecustomgear.data;

import java.util.Map;

/**
 * Where a piece of content gets its textures from.
 * <p>
 * ONE MAP, no mode. What each value is gets deduced from the value itself — a
 * resource location if it contains a colon, a file in the content root if it
 * ends in an extension. A single 'mode' is used to decide for the whole object,
 * which made a block with its own top and vanilla sides impossible; per value,
 * they mix freely.
 * <p>
 * WHAT A KEY MEANS depends on the content type, and the type is what knows:
 * a tool's value names a MODEL to inherit, an armor piece names a texture for
 * layer0, a fluid names an atlas sprite. See each generator.
 */
public class TextureData {

    /**
     * Texture values by key. Which keys are valid depends on the content type —
     * "top"/"side" for a block, "helmet"/"boots" for an armor set, "still" and
     * "flowing" for a fluid, "single"/"left"/"right" for a chest.
     */
    public Map<String, String> refs;
}