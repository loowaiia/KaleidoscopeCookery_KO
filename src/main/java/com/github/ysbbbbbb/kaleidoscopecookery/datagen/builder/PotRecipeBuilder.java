package com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class PotRecipeBuilder implements RecipeBuilder {
    private static final String NAME = "pot";
    private int time = 200;
    private int stirFryCount = 3;
    private List<Ingredient> ingredients = Lists.newArrayList();
    private ItemStack result = ItemStack.EMPTY;

    public static PotRecipeBuilder builder() {
        return new PotRecipeBuilder();
    }

    public PotRecipeBuilder setTime(int time) {
        this.time = time;
        return this;
    }

    public PotRecipeBuilder setStirFryCount(int stirFryCount) {
        this.stirFryCount = stirFryCount;
        return this;
    }

    public PotRecipeBuilder addInput(ItemLike... itemLikes) {
        for (ItemLike itemLike : itemLikes) {
            this.ingredients.add(Ingredient.of(itemLike));
        }
        return this;
    }

    public PotRecipeBuilder addInput(Ingredient... ingredients) {
        this.ingredients.addAll(Arrays.asList(ingredients));
        return this;
    }

    public PotRecipeBuilder setResult(Item result) {
        this.result = new ItemStack(result);
        return this;
    }

    public PotRecipeBuilder setResult(ItemStack result) {
        this.result = result;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String criterionName, CriterionTriggerInstance criterionTrigger) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> output) {
        String path = RecipeBuilder.getDefaultRecipeId(this.getResult()).getPath();
        ResourceLocation filePath = new ResourceLocation(KaleidoscopeCookery.MOD_ID, NAME + "/" + path);
        this.save(output, filePath);
    }

    @Override
    public void save(Consumer<FinishedRecipe> output, String recipeId) {
        ResourceLocation filePath = new ResourceLocation(KaleidoscopeCookery.MOD_ID, NAME + "/" + recipeId);
        this.save(output, filePath);
    }

    @Override
    public void save(Consumer<FinishedRecipe> recipeOutput, ResourceLocation id) {
        recipeOutput.accept(new PotFinishedRecipe(id, this.time, this.stirFryCount, this.ingredients, this.result));
    }

    public class PotFinishedRecipe implements FinishedRecipe {
        private final ResourceLocation id;
        private final int time;
        private final int stirFryCount;
        private final List<Ingredient> ingredients;
        private final ItemStack result;

        public PotFinishedRecipe(ResourceLocation id, int time, int stirFryCount, List<Ingredient> ingredients, ItemStack result) {
            this.id = id;
            this.time = time;
            this.stirFryCount = stirFryCount;
            this.ingredients = ingredients;
            this.result = result;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("time", this.time);

            json.addProperty("stir_fry_count", this.stirFryCount);

            JsonArray ingredientsJson = new JsonArray();
            this.ingredients.stream().filter(i -> i != Ingredient.EMPTY).forEach(i -> ingredientsJson.add(i.toJson()));
            json.add("ingredients", ingredientsJson);

            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(this.result.getItem())).toString());
            if (this.result.getCount() > 1) {
                itemJson.addProperty("count", this.result.getCount());
            }
            json.add("result", itemJson);
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.POT_SERIALIZER.get();
        }

        @Override
        @Nullable
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        @Nullable
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
