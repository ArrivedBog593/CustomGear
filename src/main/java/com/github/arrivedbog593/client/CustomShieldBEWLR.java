package com.github.arrivedbog593.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.github.arrivedbog593.items.weapons.CustomShieldItem;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class CustomShieldBEWLR extends BlockEntityWithoutLevelRenderer {

    private ShieldModel shieldModel;

    public CustomShieldBEWLR() {
        super(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
        );
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        super.onResourceManagerReload(resourceManager);
        this.shieldModel = new ShieldModel(
                Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SHIELD)
        );
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {
        if (shieldModel == null) {
            shieldModel = new ShieldModel(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SHIELD)
            );
        }
        if (stack.getItem() instanceof CustomShieldItem) {
            // Render exactly like vanilla shield
            BannerPatternLayers patterns = stack.getOrDefault(
                    DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
            DyeColor baseColor = stack.get(DataComponents.BASE_COLOR);
            boolean hasPattern = !patterns.layers().isEmpty() || baseColor != null;

            poseStack.pushPose();
            poseStack.scale(1.0F, -1.0F, -1.0F);

            Material material = hasPattern
                    ? ModelBakery.SHIELD_BASE
                    : ModelBakery.NO_PATTERN_SHIELD;

            VertexConsumer vertexConsumer = material.sprite().wrap(
                    ItemRenderer.getFoilBufferDirect(
                            buffer,
                            this.shieldModel.renderType(material.atlasLocation()),
                            true,
                            stack.hasFoil()
                    )
            );

            this.shieldModel.handle().render(poseStack, vertexConsumer, packedLight, packedOverlay);

            if (hasPattern) {
                BannerRenderer.renderPatterns(poseStack, buffer, packedLight, packedOverlay,
                        this.shieldModel.plate(), material, false,
                        Objects.requireNonNullElse(baseColor, DyeColor.WHITE),
                        patterns, stack.hasFoil());
            } else {
                this.shieldModel.plate().render(poseStack, vertexConsumer, packedLight, packedOverlay);
            }

            poseStack.popPose();
        } else {
            super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
        }
    }
}