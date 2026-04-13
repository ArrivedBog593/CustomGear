package com.github.arrivedbog593.commands;

import com.github.arrivedbog593.CustomGearMod;
import com.github.arrivedbog593.data.GearData;
import com.github.arrivedbog593.loader.GearParser;
import com.github.arrivedbog593.resources.TextureLoader;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
                                // Recarga los JSONs
                                List<GearData> gearList = GearParser.loadAll(
                                        Paths.get(".", "customgear")
                                );

                                // Limpia el pack dinámico anterior
                                CustomGearMod.DYNAMIC_PACK.clear();

                                // Recarga las texturas
                                TextureLoader.loadAll(CustomGearMod.DYNAMIC_PACK, gearList);
                                TextureLoader.generateLang(CustomGearMod.DYNAMIC_PACK, gearList);

                                // Notifica al usuario
                                source.sendSuccess(
                                        () -> Component.literal("§6[CustomGear] ✓ Items recargados correctamente"),
                                        false
                                );

                                return 1; // Comando exitoso

                            } catch (Exception e) {
                                source.sendFailure(
                                        Component.literal("§c[CustomGear] ✗ Error al recargar: " + e.getMessage())
                                );
                                return 0; // Comando falló
                            }
                        })
                )
        );
    }
}
