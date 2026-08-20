package arrivedbog593.ultimatecustomgear.client.render;

import arrivedbog593.ultimatecustomgear.items.containers.CustomShulkerBlock;
import arrivedbog593.ultimatecustomgear.items.containers.CustomShulkerBlockEntity;
import arrivedbog593.ultimatecustomgear.resources.ContainerTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Draws the shulker-shaped containers.
 * <p>
 * NO MESH TO BUILD, unlike the chest: vanilla exposes ShulkerModel and
 * ModelLayers.SHULKER as public API, so this only bakes what is already
 * registered. The chest needed three LayerDefinitions written by hand because
 * no equivalent exists for it.
 * <p>
 * NO ATLAS either — entityCutoutNoCull straight at the PNG. NoCull rather than
 * plain cutout because the lid turns 270 degrees as it rises and shows its own
 * inner faces on the way.
 */
public class ShulkerRenderer {

    private final ShulkerModel<?> model;

    public ShulkerRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new ShulkerModel<>(context.bakeLayer(ModelLayers.SHULKER));
    }

    public void render(CustomShulkerBlockEntity be, float partialTick,
                       @NotNull PoseStack pose, @NotNull MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        Direction facing = state.hasProperty(CustomShulkerBlock.FACING)
                ? state.getValue(CustomShulkerBlock.FACING) : Direction.UP;

        pose.pushPose();

        // Five transforms, and they are NOT the chest's. ShulkerModel comes from
        // entity rendering, where Y grows downward, so this one does need the
        // flip and the step back up that the chest does not.
        //
        // The 0.9995 shrinks the model a hair so its faces do not fight with the
        // neighboring blocks' for the same pixels.
        pose.translate(0.5F, 0.5F, 0.5F);
        pose.scale(0.9995F, 0.9995F, 0.9995F);
        pose.mulPose(facing.getRotation());
        pose.scale(1.0F, -1.0F, -1.0F);
        pose.translate(0.0F, -1.0F, 0.0F);

        // The lid rises eight pixels AND turns 270 degrees on the way, which is
        // why this is not a hinge like the chest's.
        float progress = be.getProgress(partialTick);
        model.getLid().setPos(0.0F, 24.0F - progress * 0.5F * 16.0F, 0.0F);
        model.getLid().yRot = 270.0F * progress * ((float) Math.PI / 180F);

        VertexConsumer buffer = buffers.getBuffer(RenderType.entityCutoutNoCull(
                ContainerTextures.forShulker(state.getBlock())));
        model.renderToBuffer(pose, buffer, packedLight, packedOverlay);

        pose.popPose();
    }
}