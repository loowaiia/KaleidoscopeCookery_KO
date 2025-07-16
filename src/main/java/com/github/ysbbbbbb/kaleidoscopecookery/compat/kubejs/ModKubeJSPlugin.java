package com.github.ysbbbbbb.kaleidoscopecookery.compat.kubejs;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.kubejs.recipe.ChoppingBoardRecipeSchema;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.kubejs.recipe.PotRecipeSchema;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.kubejs.recipe.StockpotRecipeSchema;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;

public class ModKubeJSPlugin extends KubeJSPlugin {
    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        event.namespace(KaleidoscopeCookery.MOD_ID).register(ModRecipes.POT_SERIALIZER.getId().getPath(), PotRecipeSchema.SCHEMA);
        event.namespace(KaleidoscopeCookery.MOD_ID).register(ModRecipes.CHOPPING_BOARD_SERIALIZER.getId().getPath(), ChoppingBoardRecipeSchema.SCHEMA);
        event.namespace(KaleidoscopeCookery.MOD_ID).register(ModRecipes.STOCKPOT_SERIALIZER.getId().getPath(), StockpotRecipeSchema.SCHEMA);
    }
}
