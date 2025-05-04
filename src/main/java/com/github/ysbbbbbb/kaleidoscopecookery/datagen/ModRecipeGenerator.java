package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.ChoppingBoardBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.PotRecipeBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
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

        addSingleItemRecipe(Items.EGG, ModItems.FRIED_EGG.get(), consumer);
        addSingleItemRecipe(Items.TURTLE_EGG, ModItems.FRIED_EGG.get(), consumer);

        addSameItemRecipe(Items.SLIME_BALL, 4, ModItems.SLIME_BALL_MEAL.get().getDefaultInstance(), true, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 5, ModItems.SLIME_BALL_MEAL.get().getDefaultInstance(), true, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 6, ModItems.SLIME_BALL_MEAL.get().getDefaultInstance(), true, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 7, ModItems.SLIME_BALL_MEAL.get().getDefaultInstance(), true, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 8, new ItemStack(ModItems.SLIME_BALL_MEAL.get(), 2), true, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 9, new ItemStack(ModItems.SLIME_BALL_MEAL.get(), 2), true, consumer);

        ChoppingBoardBuilder.builder().setIngredient(Items.MUTTON).setResult(Items.COOKED_MUTTON, 3)
                .setCutCount(4).setModelId(modLoc("mutton")).save(consumer);
    }

    private void addSingleItemRecipe(Item inputItem, Item outputItem, Consumer<FinishedRecipe> consumer) {
        this.addSingleItemRecipe(inputItem, outputItem, false, consumer);
    }

    private void addSingleItemRecipe(Item inputItem, Item outputItem, boolean needBowl, Consumer<FinishedRecipe> consumer) {
        for (int i = 1; i <= 9; i++) {
            ItemLike[] inputs = this.getItemsWithCount(inputItem, i);
            ItemStack output = new ItemStack(outputItem, i);
            String idInput = this.getRecipeIdWithCount(inputItem, i);
            String idOutput = this.getRecipeIdWithCount(outputItem, i);
            String id = String.format("%s_to_%s", idInput, idOutput);
            PotRecipeBuilder.builder().addInput(inputs).setResult(output).setNeedBowl(needBowl).save(consumer, id);
        }
    }

    private void addSameItemRecipe(Item inputItem, int count, ItemStack output, Consumer<FinishedRecipe> consumer) {
        this.addSameItemRecipe(inputItem, count, output, false, consumer);
    }

    private void addSameItemRecipe(Item inputItem, int count, ItemStack output, boolean needBowl, Consumer<FinishedRecipe> consumer) {
        ItemLike[] inputs = this.getItemsWithCount(inputItem, count);
        String idInput = this.getRecipeIdWithCount(inputItem, count);
        String idOutput = this.getRecipeIdWithCount(output.getItem(), output.getCount());
        String id = String.format("%s_to_%s", idInput, idOutput);
        PotRecipeBuilder.builder().addInput(inputs).setResult(output).setNeedBowl(needBowl).save(consumer, id);
    }

    private String getRecipeIdWithCount(ItemLike itemLike, int count) {
        return RecipeBuilder.getDefaultRecipeId(itemLike.asItem()).getPath() + "_" + count;
    }

    public ItemLike[] getItemsWithCount(ItemLike itemLike, int count) {
        ItemLike[] items = new ItemLike[count];
        Arrays.fill(items, itemLike);
        return items;
    }

    public ResourceLocation modLoc(String path) {
        return new ResourceLocation(KaleidoscopeCookery.MOD_ID, path);
    }
}
