package arrivedbog593.customgear.neoforge.client;

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
import arrivedbog593.customgear.neoforge.platform.NeoForgeFluidHelper;
import arrivedbog593.customgear.registry.ContainerRegistry;
import arrivedbog593.customgear.registry.FluidRegistry;
import arrivedbog593.customgear.registry.MenuRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

/**
 * Client glue for NeoForge: what common/ needs registered on the client, the
 * NeoForge way. Only loaded on the physical client.
 */
public final class NeoForgeClient {

    private NeoForgeClient() {}

    public static void init(IEventBus modBus) {
        modBus.addListener(FMLClientSetupEvent.class, event ->
                event.enqueueWork(ClientSetup::registerItemProperties));

        // Without this the menu opens server-side and the client silently does
        // nothing: it receives the open packet, finds no screen bound to the
        // menu type, and drops it. No error, no window.
        modBus.addListener(RegisterMenuScreensEvent.class, event ->
                event.register(MenuRegistry.CONTAINER_MENU.get(), CustomContainerScreen::new));

        // Bakes the fixed chest mesh once, not once per container definition.
        modBus.addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, event -> {
            event.registerLayerDefinition(CustomModelLayers.CHEST_SINGLE, ChestRenderer::createSingleBodyLayer);
            event.registerLayerDefinition(CustomModelLayers.CHEST_LEFT, ChestRenderer::createDoubleBodyLeftLayer);
            event.registerLayerDefinition(CustomModelLayers.CHEST_RIGHT, ChestRenderer::createDoubleBodyRightLayer);
        });

        // ONE renderer for the shared block entity type. It decides what to
        // draw by looking at the block, which is why a barrel passing through
        // here draws nothing at all.
        modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, event ->
                event.registerBlockEntityRenderer(ContainerRegistry.CONTAINER_BE.get(), ChestRenderer::new));

        modBus.addListener(RegisterClientTooltipComponentFactoriesEvent.class, event ->
                event.register(ContainerTooltip.class, ContainerTooltipRenderer::new));

        modBus.addListener(RegisterKeyMappingsEvent.class, NeoForgeClient::registerKeys);
        modBus.addListener(RegisterClientExtensionsEvent.class, NeoForgeClient::registerExtensions);

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event ->
                ClientSetup.onClientTick(Minecraft.getInstance()));
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingIn.class, event ->
                ClientSetup.onClientJoin(event.getPlayer()));
    }

    private static void registerKeys(RegisterKeyMappingsEvent event) {
        for (KeyMapping key : CustomGearKeys.GUI_ONLY) {
            key.setKeyConflictContext(KeyConflictContext.GUI);
        }
        for (KeyMapping key : CustomGearKeys.ALL) {
            event.register(key);
        }
    }

    private static void registerExtensions(RegisterClientExtensionsEvent event) {
        // Chests, shulkers and shields draw through a BEWLR. Resolved on first
        // render, not here: the renderers need a running client to be built.
        for (Item item : BuiltInRegistries.ITEM) {
            if (!Constants.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(item).getNamespace())) continue;
            if (!ItemRenderers.hasRenderer(item)) continue;
            event.registerItem(new IClientItemExtensions() {
                @Override
                public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return ItemRenderers.forItem(item);
                }
            }, item);
        }

        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.REGISTERED) {
            FluidData data = fluid.data();
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public @NotNull ResourceLocation getStillTexture() {
                    return FluidRegistry.resolveStillTexture(data);
                }

                @Override
                public @NotNull ResourceLocation getFlowingTexture() {
                    return FluidRegistry.resolveFlowingTexture(data);
                }

                @Override
                public int getTintColor() {
                    return FluidRegistry.tintColor(data);
                }
            }, NeoForgeFluidHelper.typeOf(fluid.properties()));
        }
    }
}
