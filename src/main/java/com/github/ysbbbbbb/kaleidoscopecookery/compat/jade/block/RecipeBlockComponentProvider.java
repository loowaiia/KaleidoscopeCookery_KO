package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.RecipeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public enum RecipeBlockComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig pluginConfig) {
        BlockEntity blockEntity = accessor.getBlockEntity();
        if (!(blockEntity instanceof RecipeBlockEntity recipeBlock)) {
            return;
        }
        ItemStack stackInSlot = recipeBlock.getItems().getStackInSlot(0);
        if (stackInSlot.isEmpty()) {
            return;
        }
        RecipeItem.RecipeRecord recipe = RecipeItem.getRecipe(stackInSlot);
        if (recipe == null) {
            return;
        }
        ItemStack stack = recipe.output();
        tooltip.add(IElementHelper.get().item(stack));
        tooltip.add(stack.getHoverName());
    }

    @Override
    public ResourceLocation getUid() {
        return ModPlugin.RECIPE_BLOCK;
    }
}
