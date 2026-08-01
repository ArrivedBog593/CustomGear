package arrivedbog593.ultimatecustomgear.resources;

import arrivedbog593.ultimatecustomgear.data.BlockData;
import arrivedbog593.ultimatecustomgear.data.FluidData;
import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.data.ItemData;
import arrivedbog593.ultimatecustomgear.loader.ContentRoots;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static arrivedbog593.ultimatecustomgear.resources.ModelConstants.DEFAULT_ITEM;
import static arrivedbog593.ultimatecustomgear.resources.ModelConstants.itemTextureLoc;

/**
 * Public coordinator for all resource generation.
 * <p>
 * Delegates to:
 *   - GearModelGenerator  → armor, tools, weapons, bows, shields
 *   - BlockModelGenerator → blocks and fluid buckets
 *   - LangGenerator       → lang files for all types
 * <p>
 * This class only contains the public API used by CustomGearMod.
 * <p>
 * NOTE: custom texture paths are resolved through {@link ContentRoots},
 * which searches the loose config folder AND every mounted content zip —
 * a JSON inside a zip can reference textures inside the same zip. This also
 * fixed a legacy bug where custom textures resolved against "./customgear"
 * (the mod's OLD folder name) instead of the real config folder.
 */
public final class TextureLoader {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    private TextureLoader() {}

    /**
     * Loads all textures and models into the dynamic resource pack.
     * Must be called while a ContentRoots session is open (see
     * CustomGearMod / the reload command).
     */
    public static void loadAll(DynamicResourcePack pack, List<GearData> gearList,
                               List<ItemData> itemList, List<BlockData> blockList,
                               List<FluidData> fluidList) {
        // Load vanilla shield entity texture into the item atlas
        GearModelGenerator.loadVanillaShieldTexture(pack);

        // Gear (armor, tools, weapons and individual items)
        for (GearData data : gearList) {
            GearModelGenerator.load(pack, data);
        }

        // Simple items and food items
        for (ItemData data : itemList) {
            loadItem(pack, data);
        }

        // Blocks
        for (BlockData data : blockList) {
            BlockModelGenerator.loadBlock(pack, data);
        }

        // Fluid buckets
        for (FluidData data : fluidList) {
            BlockModelGenerator.generateBucketModel(pack, data);
        }

        // Pack logo
        loadPackIcon(pack);
    }

    /**
     * Generates lang files for all registered types.
     */
    public static void generateLang(DynamicResourcePack pack, List<GearData> gearList,
                                    List<ItemData> itemList, List<BlockData> blockList,
                                    List<FluidData> fluidList) {
        LangGenerator.generateLang(pack, gearList, itemList, blockList, fluidList);
    }

    /**
     * Resolves a user-referenced resource file (custom texture, etc.) against
     * the loose config folder and every mounted content zip, in precedence
     * order. Empty if no root contains it or no session is open.
     * <p>
     * Shared helper for this class and the model generators — every place
     * that used to do GEAR_FOLDER.resolve(path) + Files.exists should go
     * through here instead.
     */
    public static Optional<Path> resolveUserResource(String relativePath) {
        ContentRoots session = ContentRoots.current();
        if (session == null) {
            LOGGER.error("[CustomGear] resolveUserResource('{}') called outside a content session — "
                    + "this is a bug, falling back to defaults", relativePath);
            return Optional.empty();
        }
        return session.resolveResource(relativePath);
    }

    // ── Item loading ──────────────────────────────────────────────────────────

    private static void loadItem(DynamicResourcePack pack, ItemData data) {
        LOGGER.info("[CustomGear] Loading item texture: {}", data.id);
        if (data.texture == null || data.texture.mode == null) {
            GearModelGenerator.generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
            return;
        }
        switch (data.texture.mode) {
            case "reference" -> {
                String ref = data.texture.refs != null ? data.texture.refs.get("item") : null;
                if (ref != null) {
                    GearModelGenerator.generateItemModelWithRef(pack, data.id, ref);
                } else {
                    LOGGER.error("[CustomGear] 'refs.item' missing in reference mode: {}", data.id);
                    GearModelGenerator.generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
                }
            }
            case "custom" -> {
                String path = data.texture.refs != null ? data.texture.refs.get("item") : null;
                if (path != null) {
                    Optional<Path> texPath = resolveUserResource(path);
                    if (texPath.isPresent()) {
                        pack.addTexture(itemTextureLoc(data.id), texPath.get());
                        GearModelGenerator.generateGeneratedItemModel(pack, data.id);
                    } else {
                        LOGGER.error("[CustomGear] Item texture not found in config folder or any pack zip: {}", path);
                        GearModelGenerator.generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
                    }
                } else {
                    LOGGER.error("[CustomGear] 'refs.item' missing in custom mode: {}", data.id);
                    GearModelGenerator.generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
                }
            }
            default -> GearModelGenerator.generateItemModelWithRef(pack, data.id, DEFAULT_ITEM);
        }
    }

    // ── Pack logo ──────────────────────────────────────────────────────────────

    /**
     * Injects the dynamic pack's own pack.png — the icon shown next to
     * "CustomGear Dynamic Pack" in the resource pack screen.
     * <p>
     * Precedence: a pack.png in the user's ultimatecustomgear/ folder wins, so
     * a server owner can brand their content pack. Falling back to the icon
     * bundled in the jar otherwise, and to nothing at all if that is missing
     * too — a pack with no icon renders a default, it does not break.
     * <p>
     * Only the LOOSE config folder is checked, not pack zips: the icon belongs
     * to the whole dynamic pack, which merges every root, so picking one zip's
     * icon over another's would be arbitrary.
     */
    public static void loadPackIcon(DynamicResourcePack pack) {
        // 1. User override
        ContentRoots session = ContentRoots.current();
        if (session != null) {
            Path userIcon = session.configFolder().resolve("dynamic_pack_icon.png");
            if (Files.isRegularFile(userIcon)) {
                try {
                    pack.addRootFile("pack.png", Files.readAllBytes(userIcon));
                    LOGGER.info("[CustomGear] Using custom dynamic_pack_icon.png from the config folder");
                    return;
                } catch (IOException e) {
                    LOGGER.warn("[CustomGear] Could not read dynamic_pack_icon.png: {} — "
                            + "falling back to the bundled icon", e.getMessage());
                }
            }
        }

        // 2. Bundled default
        try (InputStream in = TextureLoader.class.getResourceAsStream("/pack_icon.png")) {
            if (in == null) {
                LOGGER.debug("[CustomGear] No bundled pack_icon.png — dynamic pack will show "
                        + "the default icon");
                return;
            }
            pack.addRootFile("pack.png", in.readAllBytes());
        } catch (IOException e) {
            LOGGER.warn("[CustomGear] Could not load the bundled pack icon: {}", e.getMessage());
        }
    }
}