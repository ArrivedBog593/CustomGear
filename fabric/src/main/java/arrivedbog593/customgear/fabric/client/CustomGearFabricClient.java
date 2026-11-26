package arrivedbog593.customgear.fabric.client;

import arrivedbog593.customgear.Constants;
import arrivedbog593.customgear.client.ClientSetup;
import arrivedbog593.customgear.client.ContainerTooltip;
import arrivedbog593.customgear.client.ContainerTooltipRenderer;
import arrivedbog593.customgear.client.CustomContainerScreen;
import arrivedbog593.customgear.client.CustomGearKeys;
import arrivedbog593.customgear.client.ItemRenderers;
import arrivedbog593.customgear.client.render.ChestRenderer;
import arrivedbog593.customgear.client.render.CustomModelLayers;
import arrivedbog593.customgear.data.FluidData;
import arrivedbog593.customgear.network.CustomGearNetworking;
import arrivedbog593.customgear.network.HashCheckPayload;
import arrivedbog593.customgear.registry.ContainerRegistry;
import arrivedbog593.customgear.registry.FluidRegistry;
import arrivedbog593.customgear.registry.MenuRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

/**
 * The "client" entry point in fabric.mod.json: what common/ needs registered
 * on the client, the Fabric way. Runs after the main entry point, so every
 * item, block and fluid is already registered.
 */
public class CustomGearFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientSetup.registerItemProperties();

        // Without this the menu opens server-side and the client silently does
        // nothing: no screen bound to the menu type, no window.
        MenuScreens.register(MenuRegistry.CONTAINER_MENU.get(), CustomContainerScreen::new);

        // Bakes the fixed chest mesh once, not once per container definition.
        EntityModelLayerRegistry.registerModelLayer(CustomModelLayers.CHEST_SINGLE, ChestRenderer::createSingleBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(CustomModelLayers.CHEST_LEFT, ChestRenderer::createDoubleBodyLeftLayer);
        EntityModelLayerRegistry.registerModelLayer(CustomModelLayers.CHEST_RIGHT, ChestRenderer::createDoubleBodyRightLayer);
        BlockEntityRenderers.register(ContainerRegistry.CONTAINER_BE.get(), ChestRenderer::new);

        TooltipComponentCallback.EVENT.register(data ->
                data instanceof ContainerTooltip tooltip ? new ContainerTooltipRenderer(tooltip) : null);

        for (KeyMapping key : CustomGearKeys.ALL) {
            KeyBindingHelper.registerKeyBinding(key);
        }

        registerItemRenderers();
        registerFluidRenderers();

        ClientTickEvents.END_CLIENT_TICK.register(ClientSetup::onClientTick);
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null) ClientSetup.onClientJoin(client.player);
        });

        ClientConfigurationNetworking.registerGlobalReceiver(HashCheckPayload.TYPE, (payload, context) -> {
            Component reason = CustomGearNetworking.onHashCheck(payload);
            if (reason != null) {
                context.responseSender().disconnect(reason);
                return;
            }
            context.responseSender().sendPacket(CustomGearNetworking.createHashAck());
        });
    }

    /**
     * Chests, shulkers and shields draw through a BEWLR. Resolved on first
     * render, not here: the renderers need a running client to be built.
     */
    private static void registerItemRenderers() {
        for (Item item : BuiltInRegistries.ITEM) {
            if (!Constants.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(item).getNamespace())) continue;
            if (!ItemRenderers.hasRenderer(item)) continue;
            BuiltinItemRendererRegistry.INSTANCE.register(item,
                    (stack, mode, pose, buffers, light, overlay) ->
                            ItemRenderers.forItem(item).renderByItem(stack, mode, pose, buffers, light, overlay));
        }
    }

    private static void registerFluidRenderers() {
        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.REGISTERED) {
            FluidData data = fluid.data();
            FluidRenderHandlerRegistry.INSTANCE.register(fluid.source().get(), fluid.flowing().get(),
                    new SimpleFluidRenderHandler(
                            FluidRegistry.resolveStillTexture(data),
                            FluidRegistry.resolveFlowingTexture(data),
                            FluidRegistry.tintColor(data)));
            BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(),
                    fluid.source().get(), fluid.flowing().get());
        }
    }
}
