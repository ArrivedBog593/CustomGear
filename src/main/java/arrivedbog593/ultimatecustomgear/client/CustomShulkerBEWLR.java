package arrivedbog593.ultimatecustomgear.client;

import arrivedbog593.ultimatecustomgear.resources.ContainerTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Draws a shulker container in the hand, in the inventory, on the ground and in
 * item frames.
 * <p>
 * Needed for the same reason as the chest's: the block reports
 * ENTITYBLOCK_ANIMATED, so its baked model is never drawn and the item model
 * inherits from it.
 * <p>
 * The lid is always closed — an item has no opener count, and vanilla's shulker
 * item does the same.
 */
@OnlyIn(Dist.CLIENT)
public class CustomShulkerBEWLR extends BlockEntityWithoutLevelRenderer {

    private ShulkerModel<?> model;

    public CustomShulkerBEWLR() {
        super(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
        );
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        super.onResourceManagerReload(resourceManager);
        bakeModel();
    }

    private void bakeModel() {
        this.model = new ShulkerModel<>(
                Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SHULKER));
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {
        // Resource reload order does not guarantee onResourceManagerReload ran
        // before the first render — same safety net the shield renderer keeps.
        if (model == null) bakeModel();

        poseStack.pushPose();
        // The same flip the block renderer does, minus the facing rotation: an
        // item has no direction to point at.
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(0.9995F, 0.9995F, 0.9995F);
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.0F, 0.0F);

        model.getLid().setPos(0.0F, 24.0F, 0.0F);
        model.getLid().yRot = 0.0F;

        ResourceLocation texture = stack.getItem() instanceof BlockItem blockItem
                ? ContainerTextures.forShulker(blockItem.getBlock())
                : ResourceLocation.withDefaultNamespace("textures/entity/shulker/shulker.png");

        VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(
                buffer, RenderType.entityCutoutNoCull(texture), true, stack.hasFoil());

        model.renderToBuffer(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}