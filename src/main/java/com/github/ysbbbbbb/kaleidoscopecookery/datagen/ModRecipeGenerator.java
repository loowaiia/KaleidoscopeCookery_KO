package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.ChoppingBoardBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.PotRecipeBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.StockpotRecipeBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.tag.TagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
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
                .pattern(" H ")
                .pattern("SPS")
                .pattern(" # ")
                .define('H', TagItem.STRAW_HAT)
                .define('S', Items.STICK)
                .define('P', Items.PUMPKIN)
                .define('#', Items.HAY_BLOCK)
                .unlockedBy("has_pumpkin", has(Items.PUMPKIN))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.POT.get())
                .pattern("###")
                .pattern("###")
                .pattern(" # ")
                .define('#', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.IRON_KITCHEN_KNIFE.get())
                .pattern("##")
                .pattern("#S")
                .define('#', Tags.Items.INGOTS_IRON)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.GOLD_KITCHEN_KNIFE.get())
                .pattern("##")
                .pattern("#S")
                .define('#', Tags.Items.INGOTS_GOLD)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.DIAMOND_KITCHEN_KNIFE.get())
                .pattern("##")
                .pattern("#S")
                .define('#', Tags.Items.GEMS_DIAMOND)
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

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.STRAW_HAT.get())
                .pattern(" W ")
                .pattern(" S ")
                .pattern("WWW")
                .define('W', Items.WHEAT)
                .define('S', Items.STRING)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.STRAW_HAT_FLOWER.get())
                .pattern("FFF")
                .pattern("FHF")
                .pattern("FFF")
                .define('F', ItemTags.FLOWERS)
                .define('H', ModItems.STRAW_HAT.get())
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.OIL_BLOCK.get())
                .pattern("OOO")
                .pattern("OOO")
                .pattern("OOO")
                .define('O', ModItems.OIL.get())
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModItems.OIL.get(), 9)
                .requires(ModItems.OIL_BLOCK.get())
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RICE_PANICLE.get(), 1)
                .requires(ModItems.RICE_SEED.get())
                .unlockedBy("has_rice_seed", has(ModItems.RICE_SEED.get()))
                .save(consumer);

        netheriteSmithing(consumer, ModItems.DIAMOND_KITCHEN_KNIFE.get(), RecipeCategory.TOOLS, ModItems.NETHERITE_KITCHEN_KNIFE.get());

        simpleCookingRecipe(ModItems.RAW_LAMB_CHOPS.get(), ModItems.COOKED_LAMB_CHOPS.get(), 0.35F, consumer);
        simpleCookingRecipe(ModItems.RAW_COW_OFFAL.get(), ModItems.COOKED_COW_OFFAL.get(), 0.35F, consumer);
        simpleCookingRecipe(ModItems.RAW_PORK_BELLY.get(), ModItems.COOKED_PORK_BELLY.get(), 0.35F, consumer);

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

        ChoppingBoardBuilder.builder().setIngredient(Items.MUTTON).setResult(ModItems.RAW_LAMB_CHOPS.get(), 2)
                .setCutCount(4).setModelId(modLoc("raw_lamb_chops")).save(consumer);
        ChoppingBoardBuilder.builder().setIngredient(Items.TROPICAL_FISH).setResult(ModItems.SASHIMI.get(), 2)
                .setCutCount(3).setModelId(modLoc("sashimi")).save(consumer);
        ChoppingBoardBuilder.builder().setIngredient(Items.BEEF).setResult(ModItems.RAW_COW_OFFAL.get(), 2)
                .setCutCount(4).setModelId(modLoc("raw_cow_offal")).save(consumer);
        ChoppingBoardBuilder.builder().setIngredient(Items.PORKCHOP).setResult(ModItems.RAW_PORK_BELLY.get(), 2)
                .setCutCount(4).setModelId(modLoc("raw_pork_belly")).save(consumer);

        StockpotRecipeBuilder.builder().addInput(ModItems.RICE_SEED.get(), ModItems.RICE_SEED.get(), ModItems.RICE_SEED.get())
                .setFinishedTexture(modLoc("stockpot/rice_finished")).setResult(ModItems.COOKED_RICE.get()).save(consumer);

        this.addFoodBiteRecipe(consumer);
    }

    public void simpleCookingRecipe(ItemLike input, ItemLike output, float experience, Consumer<FinishedRecipe> consumer) {
        simpleCookingRecipe(consumer, "smoking", RecipeSerializer.SMOKING_RECIPE, 100, input, output, experience);
        simpleCookingRecipe(consumer, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, 600, input, output, experience);
        simpleCookingRecipe(consumer, "smelting", RecipeSerializer.SMELTING_RECIPE, 200, input, output, experience);
        PotRecipeBuilder.builder().addInput(input).setResult(output.asItem()).save(consumer);
    }

    private void addFoodBiteRecipe(Consumer<FinishedRecipe> consumer) {
        Item slimeBallMeal = FoodBiteRegistry.getItem(FoodBiteRegistry.SLIME_BALL_MEAL);
        addSameItemRecipe(Items.SLIME_BALL, 4, slimeBallMeal.getDefaultInstance(), true, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 5, slimeBallMeal.getDefaultInstance(), true, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 6, slimeBallMeal.getDefaultInstance(), true, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 7, slimeBallMeal.getDefaultInstance(), true, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 8, new ItemStack(slimeBallMeal, 2), true, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 9, new ItemStack(slimeBallMeal, 2), true, consumer);

        PotRecipeBuilder.builder().addInput(Items.HONEY_BOTTLE, Items.HONEY_BOTTLE, Items.SUGAR, Items.SUGAR, Items.PUMPKIN_PIE)
                .setNeedBowl(true).setResult(FoodBiteRegistry.FONDANT_PIE).save(consumer);

        PotRecipeBuilder.builder().addInput(Items.PORKCHOP, Items.PORKCHOP, Items.PORKCHOP, Items.BAMBOO, Items.BAMBOO)
                .setNeedBowl(true).setResult(FoodBiteRegistry.DONGPO_PORK).save(consumer);

        PotRecipeBuilder.builder().addInput(Items.SUGAR, Items.SUGAR, Items.SUGAR, Items.SPIDER_EYE, Items.SPIDER_EYE, Items.SPIDER_EYE)
                .setNeedBowl(true).setResult(FoodBiteRegistry.FONDANT_SPIDER_EYE).save(consumer);

        PotRecipeBuilder.builder().addInput(Items.CHORUS_FRUIT, ModItems.FRIED_EGG.get())
                .setNeedBowl(true).setResult(FoodBiteRegistry.CHORUS_FRIED_EGG, 3).save(consumer);

        PotRecipeBuilder.builder().addInput(Items.COD, Items.COD)
                .setNeedBowl(true).setResult(FoodBiteRegistry.BRAISED_FISH).save(consumer, "braised_fish_cod");
        PotRecipeBuilder.builder().addInput(Items.SALMON, Items.SALMON)
                .setNeedBowl(true).setResult(FoodBiteRegistry.BRAISED_FISH).save(consumer, "braised_fish_salmon");
        PotRecipeBuilder.builder().addInput(Items.COD, Items.SALMON)
                .setNeedBowl(true).setResult(FoodBiteRegistry.BRAISED_FISH).save(consumer, "braised_fish_cod_salmon");

        PotRecipeBuilder.builder().addInput(Items.GOLDEN_APPLE, Items.GOLDEN_APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_CARROT, Items.GLISTERING_MELON_SLICE, Items.GLISTERING_MELON_SLICE)
                .setNeedBowl(true).setResult(FoodBiteRegistry.GOLDEN_SALAD).save(consumer, "golden_salad_golden_apple");
        PotRecipeBuilder.builder().addInput(Items.ENCHANTED_GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_CARROT, Items.GLISTERING_MELON_SLICE, Items.GLISTERING_MELON_SLICE)
                .setNeedBowl(true).setResult(FoodBiteRegistry.GOLDEN_SALAD).save(consumer, "golden_salad_enchanted_golden_apple");

        PotRecipeBuilder.builder().addInput(Items.AMETHYST_SHARD, Items.AMETHYST_SHARD, Items.AMETHYST_SHARD, Items.MUTTON)
                .setNeedBowl(true).setResult(FoodBiteRegistry.CRYSTAL_LAMB_CHOP).save(consumer);

        Item frogspawnJelly = FoodBiteRegistry.getItem(FoodBiteRegistry.FROGSPAWN_JELLY);
        addSameItemRecipe(Items.FROGSPAWN, 3, frogspawnJelly.getDefaultInstance(), true, consumer);
        addSameItemRecipe(Items.FROGSPAWN, 4, frogspawnJelly.getDefaultInstance(), true, consumer);
        addSameItemRecipe(Items.FROGSPAWN, 5, frogspawnJelly.getDefaultInstance(), true, consumer);
        addSameItemRecipe(Items.FROGSPAWN, 6, new ItemStack(frogspawnJelly, 2), true, consumer);
        addSameItemRecipe(Items.FROGSPAWN, 7, new ItemStack(frogspawnJelly, 2), true, consumer);
        addSameItemRecipe(Items.FROGSPAWN, 8, new ItemStack(frogspawnJelly, 2), true, consumer);
        addSameItemRecipe(Items.FROGSPAWN, 9, new ItemStack(frogspawnJelly, 3), true, consumer);

        PotRecipeBuilder.builder().addInput(Items.CRIMSON_FUNGUS, Items.CRIMSON_FUNGUS, Items.WARPED_FUNGUS, Items.WARPED_FUNGUS, Items.TROPICAL_FISH, Items.TROPICAL_FISH)
                .setNeedBowl(true).setResult(FoodBiteRegistry.NETHER_STYLE_SASHIMI).save(consumer);

        PotRecipeBuilder.builder().addInput(Items.BONE, Items.BONE, Items.BONE, Items.BEEF, Items.BEEF, Items.SWEET_BERRIES, Items.SWEET_BERRIES, Items.SWEET_BERRIES)
                .setNeedBowl(true).setResult(FoodBiteRegistry.PAN_SEARED_KNIGHT_STEAK).save(consumer);

        PotRecipeBuilder.builder().addInput(Items.PUMPKIN_PIE, Items.COD, Items.COD, Items.COD, Items.COD, Items.COD)
                .setNeedBowl(true).setResult(FoodBiteRegistry.STARGAZY_PIE).save(consumer, "stargazy_pie_cod");
        PotRecipeBuilder.builder().addInput(Items.PUMPKIN_PIE, Items.SALMON, Items.SALMON, Items.SALMON, Items.SALMON, Items.SALMON)
                .setNeedBowl(true).setResult(FoodBiteRegistry.STARGAZY_PIE).save(consumer, "stargazy_pie_salmon");

        PotRecipeBuilder.builder().addInput(Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_EYE)
                .setNeedBowl(true).setResult(FoodBiteRegistry.SWEET_AND_SOUR_ENDER_PEARLS).save(consumer, "sweet_and_sour_ender_pearls_1");
        PotRecipeBuilder.builder().addInput(Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL,
                        Items.ENDER_EYE, Items.ENDER_EYE)
                .setNeedBowl(true).setResult(FoodBiteRegistry.SWEET_AND_SOUR_ENDER_PEARLS, 2).save(consumer, "sweet_and_sour_ender_pearls_2");
        PotRecipeBuilder.builder().addInput(Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL,
                        Items.ENDER_EYE, Items.ENDER_EYE, Items.ENDER_EYE)
                .setNeedBowl(true).setResult(FoodBiteRegistry.SWEET_AND_SOUR_ENDER_PEARLS, 3).save(consumer, "sweet_and_sour_ender_pearls_3");

        PotRecipeBuilder.builder().addInput(Ingredient.of(TagItem.CHILI), Ingredient.of(TagItem.CHILI),
                        Ingredient.of(Items.CHICKEN), Ingredient.of(Items.CHICKEN), Ingredient.of(Items.CHICKEN), Ingredient.of(Items.CHICKEN),
                        Ingredient.of(Items.BLAZE_POWDER))
                .setNeedBowl(true).setResult(FoodBiteRegistry.SPICY_CHICKEN).save(consumer, "spicy_chicken_blaze_powder");
        PotRecipeBuilder.builder().addInput(Ingredient.of(TagItem.CHILI), Ingredient.of(TagItem.CHILI), Ingredient.of(TagItem.CHILI),
                        Ingredient.of(Items.CHICKEN), Ingredient.of(Items.CHICKEN), Ingredient.of(Items.CHICKEN), Ingredient.of(Items.CHICKEN))
                .setNeedBowl(true).setResult(FoodBiteRegistry.SPICY_CHICKEN).save(consumer, "spicy_chicken");

        PotRecipeBuilder.builder().addInput(Ingredient.of(TagItem.CHILI), Ingredient.of(TagItem.CHILI),
                        Ingredient.of(Items.CHICKEN), Ingredient.of(Items.CHICKEN), Ingredient.of(Items.CHICKEN), Ingredient.of(Items.CHICKEN))
                .setNeedBowl(true).setResult(FoodBiteRegistry.YAKITORI).save(consumer);
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
