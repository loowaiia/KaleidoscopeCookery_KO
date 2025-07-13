package com.github.ysbbbbbb.kaleidoscopecookery.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.RecipeMatcher;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class StockpotRecipe implements Recipe<StockpotBlockEntity> {
    public static final int RECIPES_SIZE = 9;

    private final ResourceLocation id;
    private final int time;
    private final NonNullList<Ingredient> ingredients;
    private final @Nullable EntityType<?> inputEntityType;
    private final ItemStack result;
    private final Fluid soupBase;
    private final ResourceLocation cookingTexture;
    private final ResourceLocation finishedTexture;
    private final int cookingBubbleColor;
    private final int finishedBubbleColor;

    public StockpotRecipe(ResourceLocation id, List<Ingredient> ingredients, @Nullable EntityType<?> inputEntityType, ItemStack result,
                          int time, Fluid soupBase, ResourceLocation cookingTexture, ResourceLocation finishedTexture,
                          int cookingBubbleColor, int finishedBubbleColor) {
        this.id = id;
        this.ingredients = NonNullList.of(Ingredient.EMPTY, fillInputs(ingredients));
        this.inputEntityType = inputEntityType;
        this.result = result;
        this.time = time;
        this.soupBase = soupBase;
        this.cookingTexture = cookingTexture;
        this.finishedTexture = finishedTexture;
        this.cookingBubbleColor = cookingBubbleColor;
        this.finishedBubbleColor = finishedBubbleColor;
    }

    @Override
    public boolean matches(StockpotBlockEntity container, Level level) {
        return container.getSoupBase().equals(this.soupBase) && Objects.equals(container.getInputEntityType(), this.inputEntityType)
               && RecipeMatcher.findMatches(container.getItems(), ingredients) != null;
    }

    @Override
    public ItemStack assemble(StockpotBlockEntity container, RegistryAccess registryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.STOCKPOT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.STOCKPOT_RECIPE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public int getTime() {
        return time;
    }

    public ItemStack getResult() {
        return result;
    }

    public Fluid getSoupBase() {
        return soupBase;
    }

    public ResourceLocation getCookingTexture() {
        return cookingTexture;
    }

    public ResourceLocation getFinishedTexture() {
        return finishedTexture;
    }

    @Nullable
    public EntityType<?> getInputEntityType() {
        return inputEntityType;
    }

    public int getCookingBubbleColor() {
        return cookingBubbleColor;
    }

    public int getFinishedBubbleColor() {
        return finishedBubbleColor;
    }

    private Ingredient[] fillInputs(List<Ingredient> inputs) {
        Ingredient[] newInputs = new Ingredient[RECIPES_SIZE];
        for (int i = 0; i < RECIPES_SIZE; i++) {
            if (i < inputs.size()) {
                newInputs[i] = inputs.get(i);
            } else {
                newInputs[i] = Ingredient.EMPTY;
            }
        }
        return newInputs;
    }
}
