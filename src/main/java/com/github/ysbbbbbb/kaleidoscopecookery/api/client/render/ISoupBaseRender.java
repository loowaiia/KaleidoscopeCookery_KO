package com.github.ysbbbbbb.kaleidoscopecookery.api.client.render;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public interface ISoupBaseRender {
    static void renderSurface(TextureAtlasSprite sprite, int color, PoseStack poseStack, MultiBufferSource buffer, int light, float y) {
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.translucentNoCrumbling());
        Matrix4f matrix = poseStack.last().pose();

        // 锅内水面的位置和大小（根据实际锅模型调整）
        float min = 3 / 16f, max = 1 - 3 / 16f;

        // 渲染一个平面
        vertexConsumer.vertex(matrix, min, y, min)
                .color(color)
                .uv(sprite.getU0(), sprite.getV0())
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(0, 1, 0)
                .endVertex();
        vertexConsumer.vertex(matrix, min, y, max)
                .color(color)
                .uv(sprite.getU0(), sprite.getV(10))
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(0, 1, 0)
                .endVertex();
        vertexConsumer.vertex(matrix, max, y, max)
                .color(color)
                .uv(sprite.getU(10), sprite.getV(10))
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(0, 1, 0)
                .endVertex();
        vertexConsumer.vertex(matrix, max, y, min)
                .color(color)
                .uv(sprite.getU(10), sprite.getV0())
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(0, 1, 0)
                .endVertex();
    }

    void renderWhenPutIngredient(StockpotBlockEntity stockpot, float partialTick, PoseStack poseStack,
                                 MultiBufferSource buffer, int packedLight, int packedOverlay,
                                 float soupHeight);

    void renderWhenCooking(StockpotBlockEntity stockpot, float partialTick, PoseStack poseStack,
                           MultiBufferSource buffer, int packedLight, int packedOverlay,
                           ResourceLocation cookingTexture, float soupHeight);

    void renderWhenFinished(StockpotBlockEntity stockpot, float partialTick, PoseStack poseStack,
                            MultiBufferSource buffer, int packedLight, int packedOverlay,
                            ResourceLocation finishedTexture, float soupHeight);
}
