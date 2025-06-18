package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.StockpotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.StockpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Matrix4f;

public class StockpotBlockEntityRender implements BlockEntityRenderer<StockpotBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public StockpotBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(StockpotBlockEntity stockpot, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (stockpot.getBlockState().getValue(StockpotBlock.HAS_LID)) {
            return;
        }
        int status = stockpot.getStatus();
        var atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        if (status == StockpotBlockEntity.PUT_INGREDIENT) {
            Fluid fluid = stockpot.getSoupBase();
            renderSurface(getStillFluidSprite(fluid), getFluidColor(fluid), poseStack, buffer, packedLight, 0.38f);
            renderInputEntity(stockpot, poseStack, buffer, packedLight, level);
            renderItems(stockpot, poseStack, buffer, packedLight, packedOverlay);
        } else if (status == StockpotBlockEntity.COOKING) {
            TextureAtlasSprite sprite = atlas.apply(stockpot.getCookingTexture());
            renderSurface(sprite, 0xFFFFFFFF, poseStack, buffer, packedLight, 0.38f);
            renderInputEntity(stockpot, poseStack, buffer, packedLight, level);
            renderItems(stockpot, poseStack, buffer, packedLight, packedOverlay);
        } else if (status == StockpotBlockEntity.FINISHED) {
            int takeoutCount = stockpot.getTakeoutCount();
            TextureAtlasSprite sprite = atlas.apply(stockpot.getFinishedTexture());
            renderSurface(sprite, 0xFFFFFFFF, poseStack, buffer, packedLight, 0.38f / 3 * takeoutCount);
        }
    }

    private void renderInputEntity(StockpotBlockEntity stockpot, PoseStack poseStack, MultiBufferSource buffer, int packedLight, ClientLevel level) {
        EntityType<?> inputEntityType = stockpot.getInputEntityType();
        if (inputEntityType != null) {
            Entity renderEntity = stockpot.renderEntity;
            boolean shouldRefreshCache = renderEntity == null || renderEntity.getType() != inputEntityType;
            if (shouldRefreshCache) {
                stockpot.renderEntity = inputEntityType.create(level);
                if (stockpot.renderEntity != null) {
                    stockpot.renderEntity.setOnGround(true);
                }
            }

            if (stockpot.renderEntity != null) {
                int random = stockpot.renderEntity.hashCode();
                float entityY = (float) (Math.sin(random + System.currentTimeMillis() * 0.0005) * 0.25);

                poseStack.pushPose();
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(random % 360));
                poseStack.translate(-0.5, -0.5, -0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                Minecraft.getInstance().getEntityRenderDispatcher().render(stockpot.renderEntity, 1, 0.375f + entityY, 1,
                        0, 0, poseStack, buffer, packedLight);
                poseStack.popPose();
            }
        }
    }

    private void renderItems(StockpotBlockEntity stockpot, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        NonNullList<ItemStack> items = stockpot.getItems();
        items.forEach(stack -> {
            if (!stack.isEmpty()) {
                int random = stack.hashCode();
                long time = random + System.currentTimeMillis();
                float offsetX = (random % 100) * 0.002f;
                float offsetZ = (float) (Math.sin(time * 0.0005) * 0.25);
                float offsetY = random % 50 * 0.004f;
                float yRot = (random % 2 == 0 ? -1 : 1) * 20 + random % 10;

                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(85 + random % 10));
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.translate(0.9 + offsetX, 0.9 + offsetY, -0.4 + offsetZ);
                poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
                poseStack.mulPose(Axis.ZP.rotationDegrees(random % 360));
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED,
                        packedLight, packedOverlay, poseStack, buffer, stockpot.getLevel(), 0);
                poseStack.popPose();
            }
        });
    }

    public void renderSurface(TextureAtlasSprite sprite, int color, PoseStack poseStack, MultiBufferSource buffer, int light, float y) {
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

    private TextureAtlasSprite getStillFluidSprite(Fluid fluid) {
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation fluidStill = renderProperties.getStillTexture();
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
    }

    private int getFluidColor(Fluid fluid) {
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid);
        return ext.getTintColor();
    }
}
