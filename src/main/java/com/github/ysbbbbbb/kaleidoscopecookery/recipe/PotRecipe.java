package com.github.ysbbbbbb.kaleidoscopecookery.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;

import java.util.List;

public class PotRecipe implements Recipe<PotBlockEntity> {
    public static final int RECIPES_SIZE = 9;
    private final ResourceLocation id;
    private final int time;
    private final int stirFryCount;
    private final boolean needBowl;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;

    public PotRecipe(ResourceLocation id, int time, int stirFryCount, boolean needBowl, List<Ingredient> ingredients, ItemStack result) {
        this.id = id;
        this.time = time;
        this.stirFryCount = stirFryCount;
        this.needBowl = needBowl;
        this.ingredients = NonNullList.of(Ingredient.EMPTY, fillInputs(ingredients));
        this.result = result;
    }

    @Override
    public boolean matches(PotBlockEntity pContainer, Level pLevel) {
        return RecipeMatcher.findMatches(pContainer.getItems(), ingredients) != null;
    }

    @Override
    public ItemStack assemble(PotBlockEntity pContainer, RegistryAccess pRegistryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    public int getTime() {
        return time;
    }

    public int getStirFryCount() {
        return stirFryCount;
    }

    public boolean isNeedBowl() {
        return needBowl;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.POT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.POT_RECIPE;
    }

    @Override
    public boolean isSpecial() {
        return true;
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
