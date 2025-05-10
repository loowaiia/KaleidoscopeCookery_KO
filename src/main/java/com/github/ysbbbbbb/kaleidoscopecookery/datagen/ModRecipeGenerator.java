package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.ChoppingBoardBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.PotRecipeBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.function.Consumer;

public class ModRecipeGenerator extends RecipeProvider {
    public ModRecipeGenerator(DataGenerator generator) {
        super(generator.getPackOutput());
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.STOVE.get())
                .pattern("###")
                .pattern("#F#")
                .pattern("###")
                .define('#', Tags.Items.COBBLESTONE)
                .define('F', Items.CAMPFIRE)
                .unlockedBy("has_campfire", has(Items.CAMPFIRE))
                .save(consumer, "kaleidoscope_cookery:stove_campfire");

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.STOVE.get())
                .pattern("###")
                .pattern("#F#")
                .pattern("###")
                .define('#', Tags.Items.COBBLESTONE)
                .define('F', Items.SOUL_CAMPFIRE)
                .unlockedBy("has_soul_campfire", has(Items.SOUL_CAMPFIRE))
                .save(consumer, "kaleidoscope_cookery:stove_soul_campfire");

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.FRUIT_BASKET.get())
                .pattern(" S ")
                .pattern("#C#")
                .pattern("###")
                .define('S', Items.STICK)
                .define('#', ItemTags.PLANKS)
                .define('C', Items.CHEST)
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.SCARECROW.get())
                .pattern(" P ")
                .pattern("#H#")
                .pattern(" # ")
                .define('P', Items.PUMPKIN)
                .define('#', Items.STICK)
                .define('H', Items.HAY_BLOCK)
                .unlockedBy("has_pumpkin", has(Items.PUMPKIN))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.POT.get())
                .pattern("###")
                .pattern("###")
                .pattern(" # ")
                .define('#', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.KITCHEN_KNIFE.get())
                .pattern("##")
                .pattern("#S")
                .define('#', Tags.Items.INGOTS_IRON)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.KITCHEN_SHOVEL.get())
                .pattern("#")
                .pattern("S")
                .define('#', Tags.Items.INGOTS_IRON)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        addCookStool(ModItems.COOK_STOOL_OAK, Blocks.OAK_PLANKS).save(consumer);
        addCookStool(ModItems.COOK_STOOL_SPRUCE, Blocks.SPRUCE_PLANKS).save(consumer);
        addCookStool(ModItems.COOK_STOOL_ACACIA, Blocks.ACACIA_PLANKS).save(consumer);
        addCookStool(ModItems.COOK_STOOL_BAMBOO, Blocks.BAMBOO_BLOCK).save(consumer);
        addCookStool(ModItems.COOK_STOOL_BIRCH, Blocks.BIRCH_PLANKS).save(consumer);
        addCookStool(ModItems.COOK_STOOL_CHERRY, Blocks.CHERRY_PLANKS).save(consumer);
        addCookStool(ModItems.COOK_STOOL_CRIMSON, Blocks.CRIMSON_PLANKS).save(consumer);
        addCookStool(ModItems.COOK_STOOL_DARK_OAK, Blocks.DARK_OAK_PLANKS).save(consumer);
        addCookStool(ModItems.COOK_STOOL_JUNGLE, Blocks.JUNGLE_PLANKS).save(consumer);
        addCookStool(ModItems.COOK_STOOL_MANGROVE, Blocks.MANGROVE_PLANKS).save(consumer);
        addCookStool(ModItems.COOK_STOOL_WARPED, Blocks.WARPED_PLANKS).save(consumer);

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

        PotRecipeBuilder.builder().addInput(ModItems.FRIED_EGG.get(), ModItems.FRIED_EGG.get(), ModItems.TOMATO.get(), ModItems.TOMATO.get())
                .setNeedBowl(true).setResult(ModItems.SCRAMBLE_EGG_WITH_TOMATOES.get()).save(consumer);
        PotRecipeBuilder.builder().addInput(Items.HONEY_BOTTLE, Items.SUGAR, Items.COOKIE, Items.COOKIE, Items.COOKIE)
                .setNeedBowl(true).setResult(ModItems.CARAMEL_HONEY_COOKIE_FRAGMENTS.get()).save(consumer);

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

    private ShapedRecipeBuilder addCookStool(RegistryObject<Item> result, Block wood) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, result.get())
                .pattern("   ")
                .pattern("###")
                .pattern("# #")
                .define('#', wood)
                .unlockedBy("has_wood", has(wood));
    }

    public ResourceLocation modLoc(String path) {
        return new ResourceLocation(KaleidoscopeCookery.MOD_ID, path);
    }
}
