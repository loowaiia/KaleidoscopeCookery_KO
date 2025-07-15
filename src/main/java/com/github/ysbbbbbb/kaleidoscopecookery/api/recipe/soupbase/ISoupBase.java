package com.github.ysbbbbbb.kaleidoscopecookery.api.recipe.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.api.client.render.ISoupBaseRender;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ISoupBase {
    ResourceLocation getName();

    int getBubbleColor();

    ItemStack getDisplayStack();

    boolean isSoupBase(ItemStack stack);

    ItemStack getReturnContainer(Level level, LivingEntity user, ItemStack soupBase);

    boolean isContainer(ItemStack stack);

    ItemStack getReturnSoupBase(Level level, LivingEntity user, ItemStack container);

    @OnlyIn(Dist.CLIENT)
    ISoupBaseRender getRender();
}
