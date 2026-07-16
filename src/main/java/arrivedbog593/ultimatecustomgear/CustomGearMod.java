package arrivedbog593.ultimatecustomgear;

import arrivedbog593.ultimatecustomgear.client.ClientSetup;
import arrivedbog593.ultimatecustomgear.commands.CustomGearCommandHandler;
import arrivedbog593.ultimatecustomgear.config.CustomGearConfig;
import arrivedbog593.ultimatecustomgear.data.GearData;
import arrivedbog593.ultimatecustomgear.events.*;
import arrivedbog593.ultimatecustomgear.loader.*;
import arrivedbog593.ultimatecustomgear.network.CustomGearNetworking;
import arrivedbog593.ultimatecustomgear.resources.DynamicResourcePack;
import arrivedbog593.ultimatecustomgear.resources.TextureLoader;
import arrivedbog593.ultimatecustomgear.util.ContentHasher;
import arrivedbog593.ultimatecustomgear.util.GlobalIdValidator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Mod("ultimatecustomgear")
public class CustomGearMod {

    public static final String MOD_ID = "ultimatecustomgear";
    public static DynamicResourcePack DYNAMIC_PACK;

    /**
     * The ultimatecustomgear config folder, resolved against the actual game
     * directory. Paths.get(".") depends on the process working directory,
     * which some launchers do NOT set to .minecraft — FMLPaths.GAMEDIR is
     * always the real game dir on both client and dedicated server.
     */
    public static Path configFolder() {
        return FMLPaths.GAMEDIR.get().resolve(MOD_ID);
    }

    public CustomGearMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the config FIRST, before anything might read it
        modContainer.registerConfig(ModConfig.Type.COMMON, CustomGearConfig.SPEC);

        Path configFolder = configFolder();

        List<GearData> gearList;
        UniversalParser.LoadResult universalResult;
        GlobalIdValidator.Result validated;

        try (ContentRoots roots = ContentRoots.open(configFolder)) {
            // 1. Read all JSONs (loose folder + every zip in packs/)
            gearList = GearParser.loadAll(roots);
            universalResult = UniversalParser.loadAll(roots);

            // Capture the content hash of what THIS instance just loaded
            // (across ALL roots, zips included) — the handshake compares
            // these captured values, never the disk at join time
            ContentHasher.capture(roots);

            // 1.5. Global final-ID validation — drops colliding entries instead of crashing
            validated = GlobalIdValidator.validate(
                    gearList, universalResult.items, universalResult.blocks, universalResult.fluids);

            // 2. Register all items, blocks and fluids (validated lists only)
            GearRegistry.register(modEventBus, validated.gear);
            ItemRegistry.register(modEventBus, validated.items);
            BlockRegistry.register(modEventBus, validated.blocks);
            CustomGearTab.register(modEventBus);
            FluidRegistry.register(modEventBus, validated.fluids);

            // 3. Create the dynamic resource pack — igual que lo tienes
            DYNAMIC_PACK = new DynamicResourcePack(
                    new PackLocationInfo(
                            "customgear_dynamic",
                            Component.literal("CustomGear Dynamic Pack"),
                            PackSource.BUILT_IN,
                            Optional.empty()
                    )
            );

            // 4. Load textures and lang (client resources)
            TextureLoader.loadAll(DYNAMIC_PACK, validated.gear,
                    validated.items, validated.blocks, validated.fluids);
            TextureLoader.generateLang(DYNAMIC_PACK, validated.gear,
                    validated.items, validated.blocks, validated.fluids);

            // 5. Generate recipe JSONs (server data)
            RecipeLoader.loadAll(DYNAMIC_PACK, validated.gear,
                    validated.items, validated.blocks);

            // 5.6. Generate block tags (mineable tool + harvest level) — server data
            BlockTagLoader.loadAll(DYNAMIC_PACK, validated.blocks);

            // 5.7. Generate loot tables (server data)
            BlockLootLoader.loadAll(DYNAMIC_PACK, validated.blocks);

            // 5.8. Generate user-declared item/block/fluid tags — server data
            ItemTagLoader.loadAll(DYNAMIC_PACK, validated.items, validated.blocks, validated.fluids);
        }

        // 6. Register the pack, client setup, event handlers and networking
        modEventBus.addListener(this::onAddPackFinders);
        modEventBus.addListener(CustomGearNetworking::registerPayloads);
        modEventBus.addListener(CustomGearNetworking::registerConfigurationTasks);
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(ClientSetup::onClientSetup);
            NeoForge.EVENT_BUS.register(ClientSetup.class);
        }
        NeoForge.EVENT_BUS.register(CustomGearCommandHandler.class);
        NeoForge.EVENT_BUS.register(SetBonusHandler.class);
        NeoForge.EVENT_BUS.register(HeldEffectHandler.class);
        NeoForge.EVENT_BUS.register(ArrowDamageHandler.class);
        NeoForge.EVENT_BUS.register(MobDropHandler.class);
    }

    private void onAddPackFinders(AddPackFindersEvent event) {
        // Register for both CLIENT_RESOURCES (textures, models, lang)
        // and SERVER_DATA (recipes) using the same dynamic pack instance
        if (event.getPackType() == PackType.CLIENT_RESOURCES
                || event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(consumer -> {
                // DYNAMIC_PACK.location() already carries the content-hash
                // KnownPack, recomputed automatically after any pack mutation.
                // This lambda runs on every pack-repository rescan, so after
                // /customgear reload + /reload the version reflects the new
                // contents with no extra work here.
                Pack pack = Pack.readMetaAndCreate(
                        DYNAMIC_PACK.location(),
                        new Pack.ResourcesSupplier() {
                            @Override
                            @NotNull
                            public PackResources openPrimary(@NotNull PackLocationInfo info) {
                                return DYNAMIC_PACK;
                            }

                            @Override
                            @NotNull
                            public PackResources openFull(@NotNull PackLocationInfo info,
                                                          Pack.@NotNull Metadata metadata) {
                                return DYNAMIC_PACK;
                            }
                        },
                        event.getPackType(),
                        new PackSelectionConfig(true, Pack.Position.TOP, false)
                );
                if (pack != null) consumer.accept(pack);
            });
        }
    }
}