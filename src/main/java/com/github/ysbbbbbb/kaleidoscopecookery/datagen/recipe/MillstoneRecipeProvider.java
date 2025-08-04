package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.MillstoneRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class MillstoneRecipeProvider extends ModRecipeProvider {
    public MillstoneRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> consumer) {
        MillstoneRecipeBuilder.builder()
                .setIngredient(Items.GRAVEL)
                .setResult(Items.FLINT, 3)
                .save(consumer);

        MillstoneRecipeBuilder.builder()
                .setIngredient(Items.COBBLESTONE)
                .setResult(Items.GRAVEL, 3)
                .setCarrier(Items.BOWL)
                .save(consumer);
    }
}
