package arrivedbog593.ultimatecustomgear.resources;

import net.minecraft.resources.Identifier;

import java.nio.file.Path;

/**
 * Destination for everything the generators produce.
 * <p>
 * The generators build model, recipe, tag and loot-table JSON. That work does
 * not depend on the pack being a real {@link DynamicResourcePack} — it only
 * needs somewhere to put bytes. Taking this interface instead of the concrete
 * pack lets the same generation run against a map (for tests), against disk
 * (for {@code /customgear dump}) or through a decorator that watches for
 * collisions.
 * <p>
 * Deliberately narrow: {@code clear()}, {@code contentHash()} and
 * {@code location()} stay on {@link DynamicResourcePack} because they belong to
 * the reload path and the multiplayer handshake, not to generation.
 */
public interface PackSink {

    /** Adds a generated file (model, recipe, tag, lang, loot table). */
    void addRaw(Identifier location, byte[] data);

    /** Copies a file from disk into the pack, typically a user PNG. */
    void addTexture(Identifier location, Path texturePath);

    /**
     * Copies a texture AND its .mcmeta if one sits beside it.
     * <p>
     * An animated texture is a vertical strip of frames plus a .mcmeta saying
     * how many and how fast; without the metadata the atlas stitches the whole
     * strip into one 16x16 cell and the result is unrecognisable. Vanilla's own
     * lava does exactly this, which is why pointing a fluid at a copy of it
     * looked broken while pointing at the original did not.
     * <p>
     * Automatic rather than a field in the JSON: putting the two files side by
     * side is what someone does in an ordinary resource pack, so there is
     * nothing new to learn.
     */
    default void addTextureWithMeta(Identifier location, Path texturePath) {
        addTexture(location, texturePath);

        Path meta = texturePath.resolveSibling(texturePath.getFileName() + ".mcmeta");
        if (!java.nio.file.Files.isRegularFile(meta)) return;

        try {
            addRaw(Identifier.fromNamespaceAndPath(
                            location.getNamespace(), location.getPath() + ".mcmeta"),
                    java.nio.file.Files.readAllBytes(meta));
        } catch (java.io.IOException e) {
            org.apache.logging.log4j.LogManager.getLogger("CustomGear").error(
                    "[CustomGear] Could not read {}: {}", meta, e.getMessage());
        }
    }

    /** Adds a file at the pack root, e.g. {@code pack.png}. */
    void addRootFile(String name, byte[] data);
}
