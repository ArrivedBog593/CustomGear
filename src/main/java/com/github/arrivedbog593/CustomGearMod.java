package com.github.arrivedbog593;

import com.github.arrivedbog593.commands.CustomGearCommandHandler;
import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.events.ClientResourceLoadHandler;
import com.github.arrivedbog593.loader.GearParser;
import com.github.arrivedbog593.loader.GearRegistry;
import com.github.arrivedbog593.resources.DynamicResourcePack;
import com.github.arrivedbog593.resources.TextureLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.minecraft.server.packs.repository.PackSource;
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

        // 1. Lee los JSONs
        List<GearData> gearList = GearParser.loadAll(configFolder);

        // 2. Registra los ítems
        GearRegistry.register(modEventBus, gearList);
        CustomGearTab.register(modEventBus);

        // 3. Crea el resource pack dinámico
        DYNAMIC_PACK = new DynamicResourcePack(
                new PackLocationInfo(
                        "customgear_dynamic",
                        Component.literal("CustomGear Dynamic Pack"),
                        PackSource.BUILT_IN,
                        Optional.of(new KnownPack("customgear", "dynamic", "1.0"))
                )
        );

        // 4. Carga las texturas en el pack
        TextureLoader.loadAll(DYNAMIC_PACK, gearList);
        TextureLoader.generateLang(DYNAMIC_PACK, gearList);

        // 5. Registra el pack
        modEventBus.addListener(this::onAddPackFinders);
        NeoForge.EVENT_BUS.register(CustomGearCommandHandler.class);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientResourceLoadHandler.register();
        }
    }

    private void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
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
                            public PackResources openFull(@NotNull PackLocationInfo info, Pack.@NotNull Metadata metadata) {
                                return DYNAMIC_PACK;
                            }
                        },
                        PackType.CLIENT_RESOURCES,
                        new PackSelectionConfig(true, Pack.Position.TOP, false)
                );
                if (pack != null) consumer.accept(pack);
            });
        }
    }
}