package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.PotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PotBlockEntityRender implements BlockEntityRenderer<PotBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public PotBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(PotBlockEntity pot, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = this.context.getItemRenderer();
        RandomSource source = RandomSource.create(pot.getSeed());
        poseStack.pushPose();
        poseStack.mulPose(Axis.XN.rotationDegrees(90));
        poseStack.translate(0.5, -0.5, 0.1);
        poseStack.scale(0.3f, 0.3f, 0.3f);
        for (int i = 0; i < pot.getContainerSize(); i++) {
            ItemStack item = pot.getItem(i);
            if (!item.isEmpty()) {
                source.setSeed(pot.getSeed() + i);
                int zRot = -15 + source.nextInt(30);
                int yRot = 5 + source.nextInt(3);
                int xRot = 5 + source.nextInt(3);

                double totalOffset = pot.getStirFryCount() % 2 == 0 ? -0.25 : 0.25;
                double offsetX = i * 0.1 + source.nextDouble() * 0.05;
                offsetX = i % 2 == 0 ? -offsetX : offsetX;
                double offsetY = -0.45 + source.nextDouble() * 0.9;

                poseStack.pushPose();
                poseStack.mulPose(Axis.ZN.rotationDegrees(zRot));
                poseStack.mulPose(Axis.YN.rotationDegrees(yRot));
                poseStack.mulPose(Axis.XN.rotationDegrees(xRot));
                poseStack.translate(0, totalOffset, 0);
                poseStack.translate(offsetX, offsetY, 0);

                // 焦糊程度，菜变黑
                int light = OverlayTexture.u(16 - pot.getBurntLevel());
                itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, light, packedOverlay, poseStack, buffer, pot.getLevel(), 0);
                poseStack.popPose();

                poseStack.translate(0, 0, 0.025);
            }
        }
        poseStack.popPose();
    }
}
