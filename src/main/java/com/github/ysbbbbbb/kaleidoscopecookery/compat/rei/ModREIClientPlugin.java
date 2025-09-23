package com.github.ysbbbbbb.kaleidoscopecookery.compat.rei;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.category.ReiChoppingBoardRecipeCategory;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.category.ReiMillstoneRecipeCategory;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.category.ReiPotRecipeCategory;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.category.ReiStockpotRecipeCategory;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public class ModREIClientPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        ReiChoppingBoardRecipeCategory.registerCategories(registry);
        ReiMillstoneRecipeCategory.registerCategories(registry);
        ReiPotRecipeCategory.registerCategories(registry);
        ReiStockpotRecipeCategory.registerCategories(registry);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        ReiChoppingBoardRecipeCategory.registerDisplays(registry);
        ReiMillstoneRecipeCategory.registerDisplays(registry);
        ReiPotRecipeCategory.registerDisplays(registry);
        ReiStockpotRecipeCategory.registerDisplays(registry);
    }
}