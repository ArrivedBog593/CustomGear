package com.github.arrivedbog593.commands;

import com.github.arrivedbog593.CustomGearMod;
import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.loader.GearParser;
import com.github.arrivedbog593.loader.GearRegistry;
import com.github.arrivedbog593.resources.TextureLoader;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.nio.file.Paths;
import java.util.List;

public class CustomGearCommandHandler {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("customgear")
                .then(Commands.literal("reload")
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();

                            try {
                                // 1. Recarga los JSONs
                                List<GearData> gearList = GearParser.loadAll(
                                        Paths.get(".", "customgear")
                                );

                                // 2. Limpia el pack dinámico anterior
                                CustomGearMod.DYNAMIC_PACK.clear();

                                // 3. Recarga las texturas y lenguajes
                                TextureLoader.loadAll(CustomGearMod.DYNAMIC_PACK, gearList);
                                TextureLoader.generateLang(CustomGearMod.DYNAMIC_PACK, gearList);

                                // 4. ACTUALIZA LOS DATOS EN EL REGISTRO
                                updateGearRegistry(gearList);

                                // 5. Notifica al usuario
                                source.sendSuccess(
                                        () -> Component.literal("§6[CustomGear] ✓ Items recargados correctamente"),
                                        true // 'true' para que aparezca el mensaje a todos los jugadores
                                );

                                return 1; // Comando exitoso

                            } catch (Exception e) {
                                source.sendFailure(
                                        Component.literal("§c[CustomGear] ✗ Error al recargar: " + e.getMessage())
                                );
                                e.printStackTrace();
                                return 0; // Comando falló
                            }
                        })
                )
        );
    }

    /**
     * Actualiza los datos de los items en el registro sin necesidad de reiniciar
     */
    private static void updateGearRegistry(List<GearData> gearList) {
        // Limpia el mapa anterior
        GearRegistry.GEAR_MAP.clear();
        GearRegistry.TOOL_TYPE_MAP.clear();

        // Re-registra todos los datos
        for (GearData data : gearList) {
            switch (data.type) {
                case "armor_set" -> {
                    if (data.pieces != null) {
                        String[] pieces = {"helmet", "chestplate", "leggings", "boots"};
                        for (String piece : pieces) {
                            if (!data.pieces.containsKey(piece)) continue;
                            String itemId = data.id + "_" + piece;
                            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("customgear", itemId);
                            GearRegistry.GEAR_MAP.put(loc, data);
                        }
                    }
                }
                case "sword", "pickaxe", "axe", "shovel", "hoe" -> {
                    ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("customgear", data.id);
                    GearRegistry.GEAR_MAP.put(loc, data);
                }
                case "tool_set" -> {
                    if (data.tools != null) {
                        String[] validTypes = {"pickaxe", "axe", "shovel", "hoe", "sword"};
                        for (String toolType : validTypes) {
                            if (!data.tools.containsKey(toolType)) continue;

                            String itemId = data.id + "_" + toolType;
                            GearData.ToolData toolData = data.tools.get(toolType);
                            GearData derived = buildDerived(data, toolType, toolData);

                            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("customgear", itemId);
                            GearRegistry.GEAR_MAP.put(loc, derived);
                            GearRegistry.TOOL_TYPE_MAP.put(loc, toolType);
                        }
                    }
                }
            }
        }
    }

    /**
     * Construye un GearData derivado (copiado de GearRegistry)
     */
    private static GearData buildDerived(GearData parent, String toolType, GearData.ToolData toolData) {
        GearData derived = new GearData();
        derived.id = parent.id + "_" + toolType;
        derived.type = toolType;
        derived.name = parent.name;
        derived.toolNames = parent.toolNames;
        derived.durability = toolData.durability > 0 ? toolData.durability : parent.durability;
        derived.attackDamage = toolData.attackDamage;
        derived.attackSpeed = toolData.attackSpeed;
        derived.miningSpeed = toolData.miningSpeed;
        derived.harvestLevel = toolData.harvestLevel;
        derived.enchantable = parent.enchantable;
        derived.enchantability = parent.enchantability;
        derived.heldEffects = toolData.heldEffects != null
                ? toolData.heldEffects
                : parent.heldEffects;
        derived.texture = parent.texture;
        return derived;
    }
}
