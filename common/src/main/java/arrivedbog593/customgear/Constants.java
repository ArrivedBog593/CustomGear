package arrivedbog593.customgear;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Constants {

    /**
     * Must match mod_id in gradle.properties. Also the registry namespace of
     * every item, block and fluid, and the name of the content folder in the
     * game directory.
     */
    public static final String MOD_ID = "customgear";
    public static final String MOD_NAME = "Custom Gear";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private Constants() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
