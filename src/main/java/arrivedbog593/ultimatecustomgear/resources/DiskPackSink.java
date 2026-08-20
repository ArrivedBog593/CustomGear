package arrivedbog593.ultimatecustomgear.resources;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Writes the generated pack to a real folder, laid out exactly like a resource
 * pack: {@code assets/<namespace>/...} and {@code data/<namespace>/...}.
 * <p>
 * Backs {@code /customgear dump}. When someone reports that their model or
 * recipe does not work, they can send the folder containing precisely what
 * their instance generated, which turns a guessing game into reading a file.
 * The output is also a valid resource pack, so it can be dropped into
 * {@code resourcepacks/} to inspect it in-game.
 */
public class DiskPackSink implements PackSink {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    private final Path root;
    private int written = 0, failed = 0;

    public DiskPackSink(Path root) {
        this.root = root;
    }

    /**
     * Assets and data share one key space in the dynamic pack, so they are told
     * apart here by what the path starts with: the folders vanilla reads as
     * server data go under data/, everything else under assets/.
     */
    private Path target(ResourceLocation loc) {
        String p = loc.getPath();
        boolean serverData =
                p.startsWith("recipe") || p.startsWith("recipes") ||
                p.startsWith("tags/")  || p.startsWith("loot_table") ||
                p.startsWith("loot_tables") || p.startsWith("advancement");
        return root.resolve(serverData ? "data" : "assets")
                   .resolve(loc.getNamespace())
                   .resolve(p);
    }

    @Override
    public void addRaw(ResourceLocation location, byte[] data) {
        write(target(location), data);
    }

    @Override
    public void addTexture(ResourceLocation location, Path texturePath) {
        Path dest = target(location);
        try {
            Files.createDirectories(dest.getParent());
            Files.copy(texturePath, dest, StandardCopyOption.REPLACE_EXISTING);
            written++;
        } catch (IOException e) {
            LOGGER.error("[CustomGear] Dump failed for {}: {}", location, e.getMessage());
            failed++;
        }
    }

    @Override
    public void addRootFile(String name, byte[] data) {
        write(root.resolve(name), data);
    }

    private void write(Path dest, byte[] data) {
        try {
            Files.createDirectories(dest.getParent());
            Files.write(dest, data);
            written++;
        } catch (IOException e) {
            LOGGER.error("[CustomGear] Dump failed for {}: {}", dest, e.getMessage());
            failed++;
        }
    }

    public int written() { return written; }
    public int failed()  { return failed; }
    public Path root()   { return root; }
}
