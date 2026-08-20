package arrivedbog593.ultimatecustomgear.client;

import arrivedbog593.ultimatecustomgear.client.render.CustomModelLayers;
import arrivedbog593.ultimatecustomgear.resources.ContainerTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
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
 * Draws a chest container in the hand, in the inventory, on the ground and in
 * item frames.
 * <p>
 * WHY THIS EXISTS AT ALL: the block reports ENTITYBLOCK_ANIMATED, so its baked
 * model is never drawn — and the item model inherits from the block. Without a
 * renderer here the item is simply invisible everywhere outside the world.
 * <p>
 * The lid is always closed: an item has no opener count, and vanilla's chest
 * item does the same.
 */
@OnlyIn(Dist.CLIENT)
public class CustomChestBEWLR extends BlockEntityWithoutLevelRenderer {

    private ModelPart bottom;
    private ModelPart lid;
    private ModelPart lock;

    public CustomChestBEWLR() {
        super(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
        );
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        super.onResourceManagerReload(resourceManager);
        bakeParts();
    }

    /**
     * The same mesh the block entity renderer uses, baked a second time because
     * a BEWLR has no access to that instance. Baking is cheap and happens once
     * per resource reload.
     */
    private void bakeParts() {
        ModelPart root = Minecraft.getInstance().getEntityModels()
                .bakeLayer(CustomModelLayers.CHEST_SINGLE);
        this.bottom = root.getChild("bottom");
        this.lid    = root.getChild("lid");
        this.lock   = root.getChild("lock");
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {
        // Resource reload order does not guarantee onResourceManagerReload ran
        // before the first render — same safety net the shield renderer keeps.
        if (lid == null) bakeParts();

        poseStack.pushPose();
        // No rotation: an item has no facing. Everything else matches the block
        // renderer, which is why neither of them scales or flips anything.
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        lid.xRot  = 0.0F;
        lock.xRot = 0.0F;

        // The item carries no definition, so the texture comes from the block it
        // places. A chest broken before its JSON changed therefore shows the NEW
        // texture, which is the same rule the placed block follows.
        ResourceLocation texture = stack.getItem() instanceof BlockItem blockItem
                ? ContainerTextures.forBlock(blockItem.getBlock())
                : ResourceLocation.withDefaultNamespace("textures/entity/chest/normal.png");

        VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(
                buffer, RenderType.entityCutout(texture), true, stack.hasFoil());

        lid.render(poseStack, consumer, packedLight, packedOverlay);
        lock.render(poseStack, consumer, packedLight, packedOverlay);
        bottom.render(poseStack, consumer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}