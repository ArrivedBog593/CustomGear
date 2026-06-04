package com.github.arrivedbog593;

import com.github.arrivedbog593.client.ClientSetup;
import com.github.arrivedbog593.commands.CustomGearCommandHandler;
import com.github.arrivedbog593.events.ArrowDamageHandler;
import com.github.arrivedbog593.events.HeldEffectHandler;
import com.github.arrivedbog593.events.SetBonusHandler;
import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.loader.*;
import com.github.arrivedbog593.resources.DynamicResourcePack;
import com.github.arrivedbog593.resources.TextureLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Mod("customgear")
public class CustomGearMod {

    @SuppressWarnings("unused")
    public static final String MOD_ID = "customgear";
    public static DynamicResourcePack DYNAMIC_PACK;

    public CustomGearMod(IEventBus modEventBus) {
        Path configFolder = Paths.get(".", "customgear");

        // 1. Read all JSONs
        List<GearData> gearList = GearParser.loadAll(configFolder);
        UniversalParser.LoadResult universalResult = UniversalParser.loadAll(configFolder);

        // 2. Register all items, blocks and fluids
        GearRegistry.register(modEventBus, gearList);
        ItemRegistry.register(modEventBus, universalResult.items);
        BlockRegistry.register(modEventBus, universalResult.blocks);
        FluidRegistry.register(modEventBus, universalResult.fluids);
        CustomGearTab.register(modEventBus);

        // 3. Create the dynamic resource pack (handles both client resources and server data)
        DYNAMIC_PACK = new DynamicResourcePack(
                new PackLocationInfo(
                        "customgear_dynamic",
                        Component.literal("CustomGear Dynamic Pack"),
                        PackSource.BUILT_IN,
                        Optional.of(new KnownPack("customgear", "dynamic", "1.0"))
                )
        );

        // 4. Load textures and lang (client resources)
        TextureLoader.loadAll(DYNAMIC_PACK, gearList,
                universalResult.items, universalResult.blocks, universalResult.fluids);
        TextureLoader.generateLang(DYNAMIC_PACK, gearList,
                universalResult.items, universalResult.blocks, universalResult.fluids);

        // 5. Generate recipe JSONs (server data) — injected into the dynamic pack
        // so they are loaded by the normal data pack system and visible to JEI
        RecipeLoader.loadAll(DYNAMIC_PACK, gearList,
                universalResult.items, universalResult.blocks);

        // 6. Register the pack for both client resources and server data
        modEventBus.addListener(this::onAddPackFinders);
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(ClientSetup::onClientSetup);
        }
        NeoForge.EVENT_BUS.register(CustomGearCommandHandler.class);
        NeoForge.EVENT_BUS.register(SetBonusHandler.class);
        NeoForge.EVENT_BUS.register(HeldEffectHandler.class);
        NeoForge.EVENT_BUS.register(ArrowDamageHandler.class);
    }

    private void onAddPackFinders(AddPackFindersEvent event) {
        // Register for both CLIENT_RESOURCES (textures, models, lang)
        // and SERVER_DATA (recipes) using the same dynamic pack instance
        if (event.getPackType() == PackType.CLIENT_RESOURCES
                || event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(consumer -> {
                PackLocationInfo info = new PackLocationInfo(
                        "customgear_dynamic",
                        Component.literal("CustomGear Dynamic Pack"),
                        PackSource.BUILT_IN,
                        Optional.of(new KnownPack("customgear", "dynamic", "1.0"))
                );
                Pack pack = Pack.readMetaAndCreate(
                        info,
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