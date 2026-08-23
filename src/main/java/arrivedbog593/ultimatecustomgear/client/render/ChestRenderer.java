package arrivedbog593.ultimatecustomgear.client.render;

import arrivedbog593.ultimatecustomgear.items.containers.CustomChestBlock;
import arrivedbog593.ultimatecustomgear.items.containers.CustomChestBlockEntity;
import arrivedbog593.ultimatecustomgear.items.containers.CustomContainerBlockEntity;
import arrivedbog593.ultimatecustomgear.items.containers.CustomShulkerBlock;
import arrivedbog593.ultimatecustomgear.items.containers.CustomShulkerBlockEntity;
import arrivedbog593.ultimatecustomgear.resources.ContainerTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Draws the chest- and shulker-shaped containers and animates their lids.
 * <p>
 * ONE renderer for the shared block entity type. It decides what to draw from
 * the state's shape, which is why a barrel passing through here draws nothing at
 * all — its model is baked like any block's.
 * <p>
 * NO HAND-WRITTEN MESH ANY MORE. This class used to build three chest
 * LayerDefinitions itself, because vanilla exposed no chest model. It does now:
 * ChestModel and ModelLayers.CHEST / DOUBLE_CHEST_LEFT / DOUBLE_CHEST_RIGHT are
 * public, with the geometry this file was copying by hand. Using them deletes
 * ~60 lines of cube coordinates that could only ever drift from vanilla's.
 * <p>
 * THE TEXTURE COMES FROM AN ATLAS NOW, not from a path. A submitted model is
 * addressed by SpriteId, so the user's PNG has to be stitched into the chest or
 * shulker atlas — which happens on its own, since both are built from a
 * directory source that scans every namespace. See ContainerTextures.
 */
public class ChestRenderer implements BlockEntityRenderer<CustomContainerBlockEntity, ContainerRenderState> {

    private final SpriteGetter sprites;
    private final Map<ChestType, ChestModel> chestModels;
    private final ShulkerBoxRenderer shulkerRenderer;

    public ChestRenderer(BlockEntityRendererProvider.Context context) {
        this.sprites = context.sprites();
        this.chestModels = Map.of(
                ChestType.SINGLE, new ChestModel(context.bakeLayer(ModelLayers.CHEST)),
                ChestType.LEFT,   new ChestModel(context.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT)),
                ChestType.RIGHT,  new ChestModel(context.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT)));
        // Vanilla's shulker renderer, reused rather than reimplemented: the lid
        // rises AND turns 270 degrees, and that easing is worth not copying.
        this.shulkerRenderer = new ShulkerBoxRenderer(context);
    }

    @Override
    public @NotNull ContainerRenderState createRenderState() {
        return new ContainerRenderState();
    }

    @Override
    public void extractRenderState(@NotNull CustomContainerBlockEntity be,
                                   @NotNull ContainerRenderState state,
                                   float partialTicks,
                                   @NotNull Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTicks, cameraPosition, breakProgress);

        BlockState blockState = be.getBlockState();

        if (be instanceof CustomShulkerBlockEntity shulker
                && blockState.getBlock() instanceof CustomShulkerBlock) {
            state.shape = ContainerRenderState.Shape.SHULKER;
            state.facing = blockState.getValueOrElse(CustomShulkerBlock.FACING, net.minecraft.core.Direction.UP);
            state.openness = shulker.getProgress(partialTicks);
            state.sprite = Sheets.SHULKER_MAPPER.apply(
                    ContainerTextures.forShulker(blockState.getBlock()));
            return;
        }

        if (!(blockState.getBlock() instanceof CustomChestBlock)) {
            state.shape = ContainerRenderState.Shape.NONE;
            return;
        }

        state.shape = ContainerRenderState.Shape.CHEST;
        state.chestType = blockState.getValue(CustomChestBlock.TYPE);
        state.facing = blockState.getValue(CustomChestBlock.FACING);
        // The lid lives on the chest's own block entity, not on the shared base.
        state.openness = be instanceof CustomChestBlockEntity chest
                ? chest.getOpenNess(partialTicks) : 0.0F;
        state.sprite = Sheets.CHEST_MAPPER.apply(
                ContainerTextures.forBlock(blockState.getBlock(), state.chestType));
    }

    @Override
    public void submit(@NotNull ContainerRenderState state, @NotNull PoseStack pose,
                       @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState camera) {
        if (state.sprite == null) return;

        switch (state.shape) {
            case NONE -> { }
            case SHULKER -> {
                pose.pushPose();
                pose.mulPose(ShulkerBoxRenderer.modelTransform(state.facing));
                shulkerRenderer.submit(pose, collector, state.lightCoords,
                        OverlayTexture.NO_OVERLAY, state.openness, state.breakProgress,
                        state.sprite, 0);
                pose.popPose();
            }
            case CHEST -> {
                pose.pushPose();
                // Centre, turn, un-centre. NOTHING ELSE — no scale and no flip.
                // The mesh is authored in the space the model already draws in.
                pose.translate(0.5F, 0.5F, 0.5F);
                pose.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
                pose.translate(-0.5F, -0.5F, -0.5F);

                // 1 - (1-x)^3: the lid slows as it reaches the top instead of
                // stopping dead. Vanilla's easing, very visible without it.
                float open = 1.0F - state.openness;
                open = 1.0F - open * open * open;

                ChestModel model = chestModels.get(state.chestType);
                collector.submitModel(model, open, pose, state.lightCoords,
                        OverlayTexture.NO_OVERLAY, -1, state.sprite, sprites, 0,
                        state.breakProgress);
                pose.popPose();
            }
        }
    }

    /** A double chest reaches into the neighbouring block, so its box has to too. */
    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull CustomContainerBlockEntity be) {
        BlockPos pos = be.getBlockPos();
        return AABB.encapsulatingFullBlocks(pos.offset(-1, 0, -1), pos.offset(1, 1, 1));
    }
}
