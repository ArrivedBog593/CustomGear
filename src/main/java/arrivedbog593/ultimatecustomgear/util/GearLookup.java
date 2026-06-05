package arrivedbog593.ultimatecustomgear.util;

import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.loader.GearRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Centralizes the two patterns that were copy-pasted across every custom item class:
 * <p>
 * 1. {@link #getGearData(Item, GearData)} — looks up the current GearData from
 *    the registry, falling back to the initial data stored at construction time.
 *    Using the registry means that a {@code /customgear reload} is reflected
 *    immediately without restarting the game.
 * <p>
 * 2. {@link #getCurrentLang()} — safely returns the active client language code,
 *    or {@code "en_us"} on a dedicated server where Minecraft client classes are absent.
 */
public final class GearLookup {

    private GearLookup() {}

    /**
     * Returns the most up-to-date {@link GearData} for the given item.
     * Falls back to {@code initialData} if the item is not (yet) in the registry,
     * which can happen during the first tick after registration.
     */
    public static GearData getGearData(Item item, GearData initialData) {
        ResourceLocation loc = BuiltInRegistries.ITEM.getKey(item);
        GearData found = GearRegistry.lookupGear(loc);
        return found != null ? found : initialData;
    }

    /**
     * Returns the currently selected Minecraft language code (e.g. {@code "es_mx"}),
     * or {@code "en_us"} as a safe fallback on dedicated servers.
     */
    public static String getCurrentLang() {
        if (FMLEnvironment.dist.isClient()) {
            try {
                return net.minecraft.client.Minecraft.getInstance()
                        .getLanguageManager().getSelected();
            } catch (Exception ignored) {
                // Minecraft instance not yet available (rare edge case at startup)
            }
        }
        return "en_us";
    }
}
