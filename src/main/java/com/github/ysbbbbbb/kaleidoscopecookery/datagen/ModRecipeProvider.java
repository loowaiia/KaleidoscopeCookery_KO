package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.PotRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator generator) {
        super(generator.getPackOutput());
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        PotRecipeBuilder.builder().setTime(200).setStirFryCount(5).setNeedBowl(true)
                .addInput(Items.CARROT, Items.CARROT, Items.CARROT, Items.CARROT)
                .setResult(Items.GOLDEN_CARROT).save(consumer);

        PotRecipeBuilder.builder().setTime(200).setStirFryCount(3).setNeedBowl(false)
                .addInput(Items.BEEF)
                .setResult(Items.COOKED_BEEF).save(consumer);
    }
}
