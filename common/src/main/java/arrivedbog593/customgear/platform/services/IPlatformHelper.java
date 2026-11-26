package arrivedbog593.customgear.platform.services;

import net.minecraft.world.item.CreativeModeTab;

import java.nio.file.Path;
import java.util.Optional;

/**
 * What the running loader knows about the game and the other mods.
 */
public interface IPlatformHelper {

    /** "NeoForge" or "Fabric". */
    String getPlatformName();

    boolean isModLoaded(String modId);

    /** Whether the game runs from the IDE. */
    boolean isDevelopmentEnvironment();

    /** True on the physical client, even while it hosts a singleplayer world. */
    boolean isClient();

    /**
     * The real game directory. Paths.get(".") depends on the working directory
     * of the process, which some launchers do not set to .minecraft.
     */
    Path getGameDir();

    /** The shared config/ directory. */
    Path getConfigDir();

    /** The display name of an installed mod, for searching by mod name. */
    Optional<String> getModName(String modId);

    /**
     * A creative tab builder. Vanilla only has one that takes a row and
     * column; each loader places mod tabs on its own pages.
     */
    CreativeModeTab.Builder creativeTabBuilder();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }
}
