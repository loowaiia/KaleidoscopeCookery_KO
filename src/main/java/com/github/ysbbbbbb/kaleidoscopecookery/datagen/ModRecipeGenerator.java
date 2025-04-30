package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.PotRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ModRecipeGenerator extends RecipeProvider {
    public ModRecipeGenerator(DataGenerator generator) {
        super(generator.getPackOutput());
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        PotRecipeBuilder.builder().addInput(Items.POTATO).setResult(Items.BAKED_POTATO).save(consumer);
        PotRecipeBuilder.builder().addInput(Items.KELP).setResult(Items.DRIED_KELP).save(consumer);
        PotRecipeBuilder.builder().addInput(Items.CHORUS_FRUIT).setResult(Items.POPPED_CHORUS_FRUIT).save(consumer);
        PotRecipeBuilder.builder().addInput(Items.BEEF).setResult(Items.COOKED_BEEF).save(consumer);
        PotRecipeBuilder.builder().addInput(Items.CHICKEN).setResult(Items.COOKED_CHICKEN).save(consumer);
        PotRecipeBuilder.builder().addInput(Items.COD).setResult(Items.COOKED_COD).save(consumer);
        PotRecipeBuilder.builder().addInput(Items.SALMON).setResult(Items.COOKED_SALMON).save(consumer);
        PotRecipeBuilder.builder().addInput(Items.MUTTON).setResult(Items.COOKED_MUTTON).save(consumer);
        PotRecipeBuilder.builder().addInput(Items.PORKCHOP).setResult(Items.COOKED_PORKCHOP).save(consumer);
        PotRecipeBuilder.builder().addInput(Items.RABBIT).setResult(Items.COOKED_RABBIT).save(consumer);
    }
}
