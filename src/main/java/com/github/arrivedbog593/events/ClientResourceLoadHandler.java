package com.github.arrivedbog593.events;

import com.github.arrivedbog593.CustomGearMod;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.io.InputStream;

@OnlyIn(Dist.CLIENT)
public class ClientResourceLoadHandler {

    private static boolean loaded = false;

    public static void register() {
        NeoForge.EVENT_BUS.register(ClientResourceLoadHandler.class);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (loaded) return;

        loaded = true;
        loadModTextures();
    }


    private static void loadModTextures() {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            var resourceManager = minecraft.getResourceManager();

            // Busca todas las texturas de mekanismtools
            ResourceLocation pickaxe = ResourceLocation.fromNamespaceAndPath("mekanismtools", "textures/item/steel_pickaxe.png");
            ResourceLocation axe = ResourceLocation.fromNamespaceAndPath("mekanismtools", "textures/item/steel_axe.png");
            ResourceLocation shovel = ResourceLocation.fromNamespaceAndPath("mekanismtools", "textures/item/steel_shovel.png");
            ResourceLocation hoe = ResourceLocation.fromNamespaceAndPath("mekanismtools", "textures/item/steel_hoe.png");
            ResourceLocation sword = ResourceLocation.fromNamespaceAndPath("mekanismtools", "textures/item/steel_sword.png");

            ResourceLocation[] textures = {pickaxe, axe, shovel, hoe, sword};

            for (ResourceLocation loc : textures) {
                try {
                    var resource = resourceManager.getResource(loc);
                    if (resource.isPresent()) {
                        try (InputStream is = resource.get().open()) {
                            byte[] data = is.readAllBytes();
                            CustomGearMod.DYNAMIC_PACK.addRaw(loc, data);
                            System.out.println("[CustomGear] Textura cargada: " + loc);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[CustomGear] No se pudo cargar: " + loc);
                }
            }
        } catch (Exception e) {
            System.err.println("[CustomGear] Error al cargar texturas de mods: " + e.getMessage());
        }
    }
}
