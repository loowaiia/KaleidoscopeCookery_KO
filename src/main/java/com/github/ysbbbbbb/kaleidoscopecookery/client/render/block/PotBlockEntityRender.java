package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.PotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PotBlockEntityRender implements BlockEntityRenderer<PotBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public PotBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(PotBlockEntity pot, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        RandomSource source = RandomSource.create(pot.getSeed());
        PotBlockEntity.StirFryAnimationData data = pot.animationData;
        long time = System.currentTimeMillis() - data.timestamp;

        if (data.preSeed == -1L) {
            data.preSeed = pot.getSeed();
        }
        if (data.preSeed != pot.getSeed() && time > 1100) {
            data.preSeed = pot.getSeed();
            data.timestamp = System.currentTimeMillis();
            data.randomHeights = new float[9];
            for (int i = 0; i < 9; i++) {
                data.randomHeights[i] = 0.25f + source.nextFloat() * 1;
            }
        }

        ItemRenderer itemRenderer = this.context.getItemRenderer();
        int rotation = pot.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue() * 90;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.1, 0.5);
        poseStack.mulPose(Axis.YN.rotationDegrees(rotation));
        poseStack.mulPose(Axis.XN.rotationDegrees(90));
        poseStack.scale(0.5f, 0.5f, 0.5f);
        for (int i = 0; i < pot.getContainerSize(); i++) {
            ItemStack item = pot.getItem(i);
            if (!item.isEmpty()) {
                poseStack.pushPose();

                int count = 90 + source.nextInt(90);
                poseStack.mulPose(Axis.ZN.rotationDegrees(i * count));
                if (time < 1000) {
                    poseStack.translate(0, 0, data.randomHeights[i] * Mth.sin(Mth.PI * time / 1000f));
                    poseStack.mulPose(Axis.YN.rotationDegrees(720f / 1000 * time));
                }
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
