package com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.ScarecrowModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity.layer.ScarecrowHandLayer;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ScarecrowEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class ScarecrowRender extends LivingEntityRenderer<ScarecrowEntity, ScarecrowModel> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(KaleidoscopeCookery.MOD_ID, "textures/entity/scarecrow.png");

    public ScarecrowRender(EntityRendererProvider.Context context) {
        super(context, new ScarecrowModel(context.bakeLayer(ScarecrowModel.LAYER_LOCATION)), 0);
        this.addLayer(new ScarecrowHandLayer(this, context.getItemInHandRenderer(), context.getBlockRenderDispatcher()));
    }

    @Override
    protected void setupRotations(ScarecrowEntity scarecrow, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - rotationYaw));
    }

    @Override
    protected boolean shouldShowName(ScarecrowEntity scarecrow) {
        double distance = this.entityRenderDispatcher.distanceToSqr(scarecrow);
        float range = scarecrow.isCrouching() ? 32.0F : 64.0F;
        return distance < (range * range) && scarecrow.isCustomNameVisible();
    }

    @Override
    public ResourceLocation getTextureLocation(ScarecrowEntity pEntity) {
        return TEXTURE;
    }
}
