package arrivedbog593.ultimatecustomgear.client.render;

import arrivedbog593.ultimatecustomgear.items.containers.CustomChestBlock;
import arrivedbog593.ultimatecustomgear.items.containers.CustomChestBlockEntity;
import arrivedbog593.ultimatecustomgear.items.containers.CustomContainerBlockEntity;
import arrivedbog593.ultimatecustomgear.items.containers.CustomShulkerBlockEntity;
import arrivedbog593.ultimatecustomgear.resources.ContainerTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jetbrains.annotations.NotNull;

import com.mojang.math.Axis;

import java.util.Map;

/**
 * Draws the chest-shaped containers and animates their lid.
 * <p>
 * The mesh is built here in Java because that is the only place it can live: a
 * lid that rotates every frame is not expressible as a baked model. This is the
 * price of the chest shape, and the reason the barrel does not pay it.
 * <p>
 * NO ATLAS. Vanilla puts chest textures in a dedicated atlas and addresses them
 * through Material; Sheets.chestSheet() is just RenderType.entityCutout of that
 * atlas, so pointing entityCutout straight at the PNG gives the same result
 * without generating atlas definitions for every user texture. The UVs come
 * from the 64x64 declared in the LayerDefinition, which without an atlas maps
 * to the whole file — exactly vanilla's unwrap.
 */
public class ChestRenderer implements BlockEntityRenderer<CustomContainerBlockEntity> {

        /** One baked set of parts per chest type — see createSingleBodyLayer. */
    private record Parts(ModelPart bottom, ModelPart lid, ModelPart lock) {
        static Parts of(ModelPart root) {
            return new Parts(root.getChild("bottom"), root.getChild("lid"), root.getChild("lock"));
        }
    }

    private final Map<ChestType, Parts> parts;

    private final ShulkerRenderer shulkerRenderer;

    public ChestRenderer(BlockEntityRendererProvider.Context context) {
        this.shulkerRenderer = new ShulkerRenderer(context);
        this.parts = Map.of(
                ChestType.SINGLE, Parts.of(context.bakeLayer(CustomModelLayers.CHEST_SINGLE)),
                ChestType.LEFT,   Parts.of(context.bakeLayer(CustomModelLayers.CHEST_LEFT)),
                ChestType.RIGHT,  Parts.of(context.bakeLayer(CustomModelLayers.CHEST_RIGHT)));
    }

    /**
     * Vanilla's chest geometry. The numbers are not adjustable — they have to
     * match the 64x64 unwrap the texture uses.
     * <p>
     * A single chest is 14 wide and leaves a pixel of margin on both sides. The
     * halves of a double are 15 and start one pixel further out, so they meet
     * with no seam, and each carries HALF the latch — one pixel against its
     * inner edge. Put together they make the two-pixel latch centred on the
     * join, which is why neither half looks right on its own.
     */
    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("bottom",
                CubeListBuilder.create().texOffs(0, 19)
                        .addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("lid",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F),
                PartPose.offset(0.0F, 9.0F, 1.0F));
        root.addOrReplaceChild("lock",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition createDoubleBodyLeftLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("bottom",
                CubeListBuilder.create().texOffs(0, 19)
                        .addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("lid",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F),
                PartPose.offset(0.0F, 9.0F, 1.0F));
        root.addOrReplaceChild("lock",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(0.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 9.0F, 1.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition createDoubleBodyRightLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("bottom",
                CubeListBuilder.create().texOffs(0, 19)
                        .addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("lid",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F),
                PartPose.offset(0.0F, 9.0F, 1.0F));
        root.addOrReplaceChild("lock",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(15.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 9.0F, 1.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void render(@NotNull CustomContainerBlockEntity be, float partialTick,
                       @NotNull PoseStack pose, @NotNull MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        // One renderer serves the shared block entity type, so it dispatches:
        // a barrel draws nothing here at all, its model is baked.
        if (be instanceof CustomShulkerBlockEntity shulker) {
            shulkerRenderer.render(shulker, partialTick, pose, buffers, packedLight, packedOverlay);
            return;
        }

        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof CustomChestBlock)) return;

        pose.pushPose();

        // Centre, turn, un-centre. NOTHING ELSE — no scale and no flip. The
        // mesh is authored in the space ModelPart.render already draws in, so
        // any extra transform here is a bug, not a correction. Both vanilla's
        // ChestRenderer and Sophisticated's do exactly these three lines.
        pose.translate(0.5F, 0.5F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(-state.getValue(CustomChestBlock.FACING).toYRot()));
        pose.translate(-0.5F, -0.5F, -0.5F);

        Parts p = parts.get(state.getValue(CustomChestBlock.TYPE));

        // The lid lives on the chest's own block entity, not on the shared base.
        float openness = be instanceof CustomChestBlockEntity chest
                ? chest.getOpenNess(partialTick) : 0.0F;
        // 1 - (1-x)^3: the lid slows as it reaches the top instead of stopping
        // dead. Vanilla's easing, and the difference is very visible without it.
        openness = 1.0F - openness;
        openness = 1.0F - openness * openness * openness;
        p.lid().xRot  = -(openness * ((float) Math.PI / 2F));
        p.lock().xRot = p.lid().xRot;

        VertexConsumer buffer = buffers.getBuffer(RenderType.entityCutout(
                ContainerTextures.forBlock(state.getBlock(), state.getValue(CustomChestBlock.TYPE))));
        p.lid().render(pose, buffer, packedLight, packedOverlay);
        p.lock().render(pose, buffer, packedLight, packedOverlay);
        p.bottom().render(pose, buffer, packedLight, packedOverlay);

        pose.popPose();
    }

}