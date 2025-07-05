package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.KitchenwareRacksBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class KitchenwareRacksBlockEntityRender implements BlockEntityRenderer<KitchenwareRacksBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public KitchenwareRacksBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(KitchenwareRacksBlockEntity racks, float pPartialTick, PoseStack poseStack,
                       MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ItemStack itemLeft = racks.getItemLeft();
        ItemStack itemRight = racks.getItemRight();

        int rotation = racks.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue() * 90;
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YN.rotationDegrees(rotation));

        if (!itemLeft.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(-0.2, 0.25, -0.3);
            poseStack.scale(0.75f, 0.75f, 0.75f);
            poseStack.mulPose(Axis.XN.rotationDegrees(180));
            poseStack.mulPose(Axis.YN.rotationDegrees(-25));
            poseStack.mulPose(Axis.ZN.rotationDegrees(40));
            context.getItemRenderer().renderStatic(itemLeft, ItemDisplayContext.FIXED, pPackedLight, pPackedOverlay, poseStack, pBuffer, racks.getLevel(), 0);
            poseStack.popPose();
        }
        if (!itemRight.isEmpty()) {
            poseStack.translate(0.2, 0.25, -0.3);
            poseStack.scale(0.75f, 0.75f, 0.75f);
            poseStack.mulPose(Axis.XN.rotationDegrees(180));
            poseStack.mulPose(Axis.YN.rotationDegrees(-25));
            poseStack.mulPose(Axis.ZN.rotationDegrees(40));
            context.getItemRenderer().renderStatic(itemRight, ItemDisplayContext.FIXED, pPackedLight, pPackedOverlay, poseStack, pBuffer, racks.getLevel(), 0);
        }

        poseStack.popPose();
    }
}
