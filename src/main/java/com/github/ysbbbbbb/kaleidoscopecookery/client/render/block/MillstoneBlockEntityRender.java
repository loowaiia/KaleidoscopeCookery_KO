package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.MillstoneBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.MillstoneModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class MillstoneBlockEntityRender implements BlockEntityRenderer<MillstoneBlockEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(KaleidoscopeCookery.MOD_ID, "textures/block/millstone.png");

    private final BlockEntityRendererProvider.Context context;
    private final MillstoneModel bodyModel;

    public MillstoneBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.context = context;
        this.bodyModel = new MillstoneModel(context.bakeLayer(MillstoneModel.LAYER_LOCATION));
    }

    @Override
    public void render(MillstoneBlockEntity millstone, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Direction facing = millstone.getBlockState().getValue(MillstoneBlock.FACING);
        int facingDeg = facing.get2DDataValue() * 90;

        Level level = millstone.getLevel();
        if (level != null) {
            if (millstone.hasEntity()) {
                float rot = facingDeg + millstone.getRotation(level, partialTick);
                this.bodyModel.getWheel().yRot = -rot * Mth.DEG_TO_RAD;
                this.bodyModel.getWheel().xRot = -millstone.getLiftAngle();
                this.bodyModel.getRoll().zRot = rot * Mth.DEG_TO_RAD;
            } else {
                float rot = facingDeg + millstone.getCacheRot();
                this.bodyModel.getWheel().yRot = -rot * Mth.DEG_TO_RAD;
            }
        }

        this.renderBody(poseStack, buffer, packedLight, packedOverlay, facing);

        this.bodyModel.getWheel().yRot = 0;
        this.bodyModel.getWheel().xRot = 0;
        this.bodyModel.getRoll().zRot = 0;
    }

    private void renderBody(PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, Direction facing) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(180 - facing.get2DDataValue() * 90));
        VertexConsumer checkerBoardBuff = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        bodyModel.renderToBuffer(poseStack, checkerBoardBuff, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(MillstoneBlockEntity millstone) {
        return true;
    }
}
