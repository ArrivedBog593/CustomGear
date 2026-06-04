package com.github.arrivedbog593.commands;

import com.github.arrivedbog593.CustomGearMod;
import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.loader.BlockRegistry;
import com.github.arrivedbog593.loader.FluidRegistry;
import com.github.arrivedbog593.loader.GearParser;
import com.github.arrivedbog593.loader.GearRegistry;
import com.github.arrivedbog593.loader.ItemRegistry;
import com.github.arrivedbog593.loader.UniversalParser;
import com.github.arrivedbog593.resources.TextureLoader;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class CustomGearCommandHandler {

    private static final Logger LOGGER = LogManager.getLogger("CustomGear");

    private static final long RELOAD_COOLDOWN_MS = 5_000L;
    private static final AtomicLong lastReloadTime = new AtomicLong(0L);

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("customgear")
                .then(Commands.literal("reload")
                        .requires(src -> src.hasPermission(2))
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();

                            // FIX: cooldown anti-spam
                            long now = System.currentTimeMillis();
                            long last = lastReloadTime.get();
                            long remaining = RELOAD_COOLDOWN_MS - (now - last);
                            if (remaining > 0) {
                                source.sendFailure(Component.literal(
                                        "[CustomGear] Reload on cooldown. Wait " + (remaining / 1000 + 1) + "s."));
                                return 0;
                            }
                            lastReloadTime.set(now);

                            try {
                                // 1. Reload all JSONs
                                Path customgear = Paths.get(".", "customgear");
                                List<GearData> gearList = GearParser.loadAll(customgear);
                                UniversalParser.LoadResult universalResult = UniversalParser.loadAll(customgear);

                                // 2. Clear the previous dynamic pack
                                CustomGearMod.DYNAMIC_PACK.clear();

                                // 3. Reload textures and languages
                                TextureLoader.loadAll(CustomGearMod.DYNAMIC_PACK, gearList,
                                        universalResult.items, universalResult.blocks, universalResult.fluids);
                                TextureLoader.generateLang(CustomGearMod.DYNAMIC_PACK, gearList,
                                        universalResult.items, universalResult.blocks, universalResult.fluids);

                                // 4. Reload fluid, item and block data in registries
                                FluidRegistry.updateFluidData(universalResult.fluids);
                                ItemRegistry.updateItemData(universalResult.items);
                                BlockRegistry.updateBlockData(universalResult.blocks);

                                // 5. Atomic swap of the GEAR_MAP — removes the inconsistency window
                                updateGearRegistryAtomic(gearList);

                                // 6. Notify user
                                source.sendSuccess(
                                        () -> Component.translatable("customgear.command.reload.success"),
                                        true
                                );

                                return 1;

                            } catch (Exception e) {
                                // FIX: no exponer rutas internas ni stack traces al jugador
                                source.sendFailure(
                                        Component.translatable("customgear.command.reload.error"));
                                // El detalle completo solo va al log del servidor
                                LOGGER.error("[CustomGear] Reload failed", e);
                                return 0;
                            }
                        })
                )
        );
    }

    /**
     * Builds new maps from scratch and swaps them in atomically via
     * {@link GearRegistry#atomicSwap}. This eliminates the clear() + put() window
     * where the map was temporarily empty and could cause NPEs in server ticks.
     */
    private static void updateGearRegistryAtomic(List<GearData> gearList) {
        Map<ResourceLocation, GearData> newGear     = new HashMap<>();
        Map<ResourceLocation, String>   newToolType = new HashMap<>();

        for (GearData data : gearList) {
            switch (data.type) {
                case "armor_set" -> {
                    if (data.pieces != null) {
                        for (String piece : new String[]{"helmet", "chestplate", "leggings", "boots"}) {
                            if (!data.pieces.containsKey(piece)) continue;
                            newGear.put(rl(data.id + "_" + piece), data);
                        }
                    }
                }
                case "sword", "bow", "crossbow", "shield",
                     "pickaxe", "axe", "shovel", "hoe" -> newGear.put(rl(data.id), data);

                case "tool_set" -> {
                    if (data.tools != null) {
                        for (String toolType : new String[]{"pickaxe", "axe", "shovel", "hoe"}) {
                            if (!data.tools.containsKey(toolType)) continue;
                            GearData derived = GearRegistry.buildDerived(data, toolType, data.tools.get(toolType));
                            ResourceLocation loc = rl(data.id + "_" + toolType);
                            newGear.put(loc, derived);
                            newToolType.put(loc, toolType);
                        }
                    }
                }
                case "weapon_set" -> {
                    if (data.weapons != null) {
                        for (String weaponType : new String[]{"sword", "bow", "crossbow", "shield"}) {
                            if (!data.weapons.containsKey(weaponType)) continue;
                            GearData derived = GearRegistry.buildWeaponDerived(data, weaponType, data.weapons.get(weaponType));
                            newGear.put(rl(data.id + "_" + weaponType), derived);
                        }
                    }
                }
            }
        }

        GearRegistry.atomicSwap(newGear, newToolType);
    }

    private static ResourceLocation rl(String id) {
        return ResourceLocation.fromNamespaceAndPath("customgear", id);
    }
}
