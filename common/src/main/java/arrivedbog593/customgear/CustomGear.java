package arrivedbog593.customgear;

import arrivedbog593.customgear.config.CustomGearConfig;
import arrivedbog593.customgear.data.BlockData;
import arrivedbog593.customgear.data.ContainerContentData;
import arrivedbog593.customgear.data.GearData;
import arrivedbog593.customgear.datapack.*;
import arrivedbog593.customgear.events.RecipePatchHandler;
import arrivedbog593.customgear.parser.GearParser;
import arrivedbog593.customgear.parser.UniversalParser;
import arrivedbog593.customgear.platform.Services;
import arrivedbog593.customgear.registry.*;
import arrivedbog593.customgear.resources.DynamicResourcePack;
import arrivedbog593.customgear.resources.TextureLoader;
import arrivedbog593.customgear.util.ContentHasher;
import arrivedbog593.customgear.util.ContentRoots;
import arrivedbog593.customgear.util.GlobalIdValidator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Loads the content and registers everything, the same on every loader.
 * Each loader's entry point calls {@link #init()} as early as it can register
 * objects, then wires its own events to the handlers in this mod.
 */
public final class CustomGear {

    /** The pack holding every generated texture, model, lang, recipe, tag and loot table. */
    public static DynamicResourcePack DYNAMIC_PACK;

    /** Where Ultimate Custom Gear kept its content, read once to migrate it. */
    private static final String LEGACY_FOLDER = "ultimatecustomgear";

    private CustomGear() {}

    /**
     * The content folder (.minecraft/customgear), resolved against the actual
     * game directory, which some launchers do not make the working directory.
     */
    public static Path configFolder() {
        return Services.PLATFORM.getGameDir().resolve(Constants.MOD_ID);
    }

    public static void init() {
        CustomGearConfig.load();
        migrateLegacyFolder();

        List<GearData> gearList;
        UniversalParser.LoadResult universalResult;
        GlobalIdValidator.Result validated;

        try (ContentRoots roots = ContentRoots.open(configFolder())) {
            // 1. Read all JSONs (loose folder + every zip in packs/)
            gearList = GearParser.loadAll(roots);
            universalResult = UniversalParser.loadAll(roots);

            // 1.5. Global final-ID validation — drops colliding entries instead of crashing
            validated = GlobalIdValidator.validate(
                    gearList, universalResult.items, universalResult.blocks,
                    universalResult.fluids, universalResult.containers);

            // Capture the content hash of what THIS instance just loaded
            // (across ALL roots, zips included) — the handshake compares
            // these captured values, never the disk at join time. After the
            // validation, so the files it dropped stay out of the hash.
            roots.excludeEntries(validated.dropped);
            ContentHasher.capture(roots);

            // A placeable container IS a block for every resource purpose:
            // model, blockstate, lang, loot table, tags and recipe all come from
            // the same loaders. A BACKPACK is not — it has no blockstate, no
            // loot table, and its lang key is item.* — so feeding it to the
            // block loaders produces a stone-textured model and an untranslated
            // name.
            List<BlockData> allBlocks = new ArrayList<>(validated.blocks);
            List<ContainerContentData> backpacks = new ArrayList<>();
            for (ContainerContentData c : validated.containers) {
                if (c.container != null && c.container.isBlock()) allBlocks.add(c);
                else backpacks.add(c);
            }

            // 2. Register all items, blocks and fluids (validated lists only)
            GearRegistry.register(validated.gear);
            ItemRegistry.register(validated.items);
            BlockRegistry.register(validated.blocks);
            ContainerRegistry.register(validated.containers);
            CustomGearTab.register();
            FluidRegistry.register(validated.fluids);
            MenuRegistry.register();
            ComponentRegistry.register();

            // 3. Create the dynamic resource pack
            DYNAMIC_PACK = new DynamicResourcePack(
                    new PackLocationInfo(
                            "customgear_dynamic",
                            Component.literal("Custom Gear Dynamic Pack"),
                            PackSource.BUILT_IN,
                            Optional.empty()
                    )
            );

            // 4. Load textures and lang (client resources)
            TextureLoader.loadAll(DYNAMIC_PACK, validated.gear,
                    validated.items, allBlocks, validated.fluids, backpacks);
            TextureLoader.generateLang(DYNAMIC_PACK, validated.gear,
                    validated.items, allBlocks, validated.fluids, backpacks);

            // 5. Generate recipe JSONs (server data)
            RecipeLoader.loadAll(DYNAMIC_PACK, validated.gear,
                    validated.items, allBlocks);

            // 5.1. recipe_patch: additions are files, removals run after every datapack load
            RecipePatchLoader.loadAll(DYNAMIC_PACK, universalResult.recipePatches);
            RecipePatchHandler.setPatches(universalResult.recipePatches);

            // 5.6. Tag files: one shared builder, emitted once at 5.9
            TagFileBuilder tagFiles = new TagFileBuilder();
            BlockTagLoader.loadAll(tagFiles, allBlocks);
            GearTagLoader.loadAll(tagFiles, validated.gear);

            // 5.7. Generate loot tables (server data)
            BlockLootLoader.loadAll(DYNAMIC_PACK, allBlocks);

            // 5.8. User-declared item/block/fluid tags — same builder
            ItemTagLoader.loadAll(tagFiles, validated.items, allBlocks, validated.fluids);

            // 5.85. Foreign content declared into tags — same builder
            TagPatchLoader.loadAll(tagFiles, universalResult.tagPatches);
            AccessorySlotLoader.loadAll(DYNAMIC_PACK, tagFiles, validated.containers);

            // 5.9. Emit every tag file — must come after ALL tag sources
            tagFiles.emit(DYNAMIC_PACK);
        }
    }

    /**
     * The dynamic pack as a Pack of the given type, for each loader to add to
     * its pack repository. Both CLIENT_RESOURCES (textures, models, lang) and
     * SERVER_DATA (recipes, tags, loot) are served by the same instance.
     * <p>
     * Called on every pack-repository rescan. DYNAMIC_PACK.location() already
     * carries the content-hash KnownPack, recomputed after any pack mutation,
     * so after /customgear reload + /reload the version reflects the new
     * contents with no extra work here.
     */
    public static @Nullable Pack createPack(PackType type) {
        if (DYNAMIC_PACK == null) return null;
        return Pack.readMetaAndCreate(
                DYNAMIC_PACK.location(),
                new Pack.ResourcesSupplier() {
                    @Override
                    public @NotNull PackResources openPrimary(@NotNull PackLocationInfo info) {
                        return DYNAMIC_PACK;
                    }

                    @Override
                    public @NotNull PackResources openFull(@NotNull PackLocationInfo info,
                                                           Pack.@NotNull Metadata metadata) {
                        return DYNAMIC_PACK;
                    }
                },
                type,
                new PackSelectionConfig(true, Pack.Position.TOP, false)
        );
    }

    /**
     * Copies Ultimate Custom Gear's content folder into this mod's the first
     * time it runs, so a world switched over from that mod keeps its items.
     * The old folder is left as it was; nothing happens once customgear/
     * exists.
     */
    private static void migrateLegacyFolder() {
        Path target = configFolder();
        Path legacy = Services.PLATFORM.getGameDir().resolve(LEGACY_FOLDER);
        if (Files.exists(target) || !Files.isDirectory(legacy)) return;

        try {
            Files.walkFileTree(legacy, new SimpleFileVisitor<>() {
                @Override
                public @NotNull FileVisitResult preVisitDirectory(@NotNull Path dir,
                                                                  @NotNull BasicFileAttributes attrs)
                        throws IOException {
                    Files.createDirectories(target.resolve(legacy.relativize(dir).toString()));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public @NotNull FileVisitResult visitFile(@NotNull Path file,
                                                          @NotNull BasicFileAttributes attrs)
                        throws IOException {
                    Files.copy(file, target.resolve(legacy.relativize(file).toString()));
                    return FileVisitResult.CONTINUE;
                }
            });
            Constants.LOG.info("[CustomGear] Copied the content of {}/ into {}/. The old folder "
                    + "was left untouched and can be deleted.", LEGACY_FOLDER, Constants.MOD_ID);
        } catch (IOException e) {
            Constants.LOG.error("[CustomGear] Could not copy {}/ into {}/: copy it by hand.",
                    LEGACY_FOLDER, Constants.MOD_ID, e);
        }
    }
}
