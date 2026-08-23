package arrivedbog593.ultimatecustomgear.client;

import arrivedbog593.ultimatecustomgear.client.render.ChestRenderer;
import arrivedbog593.ultimatecustomgear.registry.ContainerRegistry;
import arrivedbog593.ultimatecustomgear.registry.FluidRegistry;
import arrivedbog593.ultimatecustomgear.resources.FluidTextures;
import arrivedbog593.ultimatecustomgear.registry.MenuRegistry;
import arrivedbog593.ultimatecustomgear.network.CustomGearNetworking;
import arrivedbog593.ultimatecustomgear.network.OpenBackpackPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.fluid.FluidTintSources;

/**
 * The client-side wiring that still has to happen in Java.
 * <p>
 * WHAT USED TO BE HERE AND IS NOT ANY MORE. Up to 1.21.1 this class registered
 * an {@code ItemProperties} predicate per bow, crossbow and shield, so their
 * models could switch while drawing or blocking. Those predicates are gone: a
 * model that changes with the item's state is now declared in the item MODEL
 * itself, as a {@code minecraft:condition} or {@code minecraft:range_dispatch}
 * entry — see GearModelGenerator. That is strictly better here, because the old
 * code had to walk every registered item on every client start just to find the
 * few that needed a predicate.
 */
public class ClientSetup {

    // Registered via NeoForge.EVENT_BUS.register(ClientSetup.class) — GAME bus
    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (CustomGearNetworking.pendingMismatchWarning) {
            CustomGearNetworking.pendingMismatchWarning = false;
            event.getPlayer().sendSystemMessage(
                    Component.translatable("customgear.network.hash_mismatch_warn"));
        }
    }

    // ── Menu ────────────────────────────────────────────────────────────────

    /**
     * Without this the menu opens server-side and the client silently does
     * nothing: it receives the open packet, finds no screen bound to the menu
     * type, and drops it. No error, no window.
     */
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.CONTAINER_MENU.get(), CustomContainerScreen::new);
    }

    // ── Chest ────────────────────────────────────────────────────────────────

    // NO LAYER DEFINITIONS. The chest mesh used to be written out here because
    // vanilla exposed no chest model; ChestModel and its three ModelLayers are
    // public now, so the renderer bakes those instead. See ChestRenderer.

    /**
     * ONE renderer for the shared block entity type. It decides what to draw by
     * looking at the block, which is why a barrel passing through here draws
     * nothing at all.
     */
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ContainerRegistry.CONTAINER_BE.get(), ChestRenderer::new);
    }

    // ── Fluids ───────────────────────────────────────────────────────────────

    /**
     * A fluid's appearance is no longer something the FluidType answers. Its
     * still and flowing sprites, and the tint over them, are a FluidModel
     * registered here instead. Both halves of a fluid pair share one model,
     * which is why the source and the flowing form are registered together.
     */
    public static void onRegisterFluidModels(RegisterFluidModelsEvent event) {
        for (FluidRegistry.RenderEntry entry : FluidRegistry.RENDERED) {
            event.register(
                    new FluidModel.Unbaked(
                            new Material(FluidTextures.still(entry.data())),
                            new Material(FluidTextures.flowing(entry.data())),
                            null,
                            FluidTintSources.constant(FluidTextures.tint(entry.data()))),
                    entry.source().get(),
                    entry.flowing().get());
        }
    }

    public static void onRegisterTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ContainerTooltip.class, ContainerTooltipRenderer::new);
    }

    /**
     * Fires outside any screen. Inside one the container screen handles its own
     * bindings, and opening a backpack over an open chest would be confusing.
     */
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null) return;
        while (CustomGearKeys.OPEN_BACKPACK.consumeClick()) {
            net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(new OpenBackpackPayload());
        }
    }
}
