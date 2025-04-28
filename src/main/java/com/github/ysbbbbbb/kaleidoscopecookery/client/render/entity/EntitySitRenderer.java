package com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity;


import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.EntitySit;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class EntitySitRenderer extends EntityRenderer<EntitySit> {
    private static final ResourceLocation EMPTY = new ResourceLocation(KaleidoscopeCookery.MOD_ID, "textures/entity/empty.png");

    public EntitySitRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntitySit entitySit, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
    }

    @Override
    public ResourceLocation getTextureLocation(EntitySit entitySit) {
        return EMPTY;
    }
}
