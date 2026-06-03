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
import java.util.List;

public class CustomGearCommandHandler {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {

        final Logger LOGGER = LogManager.getLogger("CustomGear");

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("customgear")
                .then(Commands.literal("reload")
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();

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

                                // 3.5 UPDATE: Reload fluid, item and block data in registries
                                FluidRegistry.updateFluidData(universalResult.fluids);
                                ItemRegistry.updateItemData(universalResult.items);
                                BlockRegistry.updateBlockData(universalResult.blocks);

                                // 4. Update item data in the registry
                                updateGearRegistry(gearList);

                                // 5. Notify user
                                source.sendSuccess(
                                        () -> Component.translatable("customgear.command.reload.success"),
                                        true
                                );

                                return 1;
                            } catch (Exception e) {
                                source.sendFailure(
                                        Component.translatable("customgear.command.reload.error")
                                                .append(Component.literal(": " + e.getMessage()))
                                );
                                LOGGER.error("Error reloading CustomGear data", e);
                                return 0;
                            }
                        })
                )
        );
    }

    /**
     * Updates item data in the registry without restarting the game.
     * Rebuilds GEAR_MAP and TOOL_TYPE_MAP from the provided gear data.
     */
    private static void updateGearRegistry(List<GearData> gearList) {
        GearRegistry.GEAR_MAP.clear();
        GearRegistry.TOOL_TYPE_MAP.clear();

        for (GearData data : gearList) {
            switch (data.type) {
                case "armor_set" -> {
                    if (data.pieces != null) {
                        String[] pieces = {"helmet", "chestplate", "leggings", "boots"};
                        for (String piece : pieces) {
                            if (!data.pieces.containsKey(piece)) continue;
                            String itemId = data.id + "_" + piece;
                            GearRegistry.GEAR_MAP.put(
                                    ResourceLocation.fromNamespaceAndPath("customgear", itemId), data);
                        }
                    }
                }
                case "sword", "bow", "crossbow", "shield",
                     "pickaxe", "axe", "shovel", "hoe" -> GearRegistry.GEAR_MAP.put(
                             ResourceLocation.fromNamespaceAndPath("customgear", data.id), data);
                case "tool_set" -> {
                    if (data.tools != null) {
                        String[] validTypes = {"pickaxe", "axe", "shovel", "hoe"};
                        for (String toolType : validTypes) {
                            if (!data.tools.containsKey(toolType)) continue;
                            GearData.ToolData toolData = data.tools.get(toolType);
                            GearData derived = GearRegistry.buildDerived(data, toolType, toolData);
                            String itemId = data.id + "_" + toolType;
                            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("customgear", itemId);
                            GearRegistry.GEAR_MAP.put(loc, derived);
                            GearRegistry.TOOL_TYPE_MAP.put(loc, toolType);
                        }
                    }
                }
                case "weapon_set" -> {
                    if (data.weapons != null) {
                        String[] validTypes = {"sword", "bow", "crossbow", "shield"};
                        for (String weaponType : validTypes) {
                            if (!data.weapons.containsKey(weaponType)) continue;
                            GearData.WeaponData weaponData = data.weapons.get(weaponType);
                            GearData derived = GearRegistry.buildWeaponDerived(data, weaponType, weaponData);
                            String itemId = data.id + "_" + weaponType;
                            GearRegistry.GEAR_MAP.put(
                                    ResourceLocation.fromNamespaceAndPath("customgear", itemId), derived);
                        }
                    }
                }
            }
        }
    }
}