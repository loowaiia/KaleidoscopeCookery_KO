package com.github.ysbbbbbb.kaleidoscopecookery.recipe.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.recipe.StockpotRecipe;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class StockpotRecipeSerializer implements RecipeSerializer<StockpotRecipe> {
    public static final ResourceLocation DEFAULT_COOKING_TEXTURE = new ResourceLocation(KaleidoscopeCookery.MOD_ID, "stockpot/default_cooking");
    public static final ResourceLocation DEFAULT_FINISHED_TEXTURE = new ResourceLocation(KaleidoscopeCookery.MOD_ID, "stockpot/default_finished");
    public static final int DEFAULT_TIME = 300;
    public static final Fluid DEFAULT_SOUP_BASE = Fluids.WATER;
    public static final int DEFAULT_COOKING_BUBBLE_COLOR = 0xCDBDA3;
    public static final int DEFAULT_FINISHED_BUBBLE_COLOR = 0xCB8A6E;

    @Override
    public StockpotRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
        List<Ingredient> inputs = Lists.newArrayList();
        for (JsonElement e : ingredients) {
            inputs.add(Ingredient.fromJson(e));
        }
        EntityType<?> inputEntityType = null;
        if (json.has("input_entity_type")) {
            String entityTypeId = GsonHelper.getAsString(json, "input_entity_type");
            inputEntityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityTypeId));
        }
        ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true, true);
        int time = GsonHelper.getAsInt(json, "time", DEFAULT_TIME);
        Fluid soupBase = getSoupBaseFromJson(json);
        ResourceLocation cookingTexture = new ResourceLocation(GsonHelper.getAsString(json, "cooking_texture", DEFAULT_COOKING_TEXTURE.toString()));
        ResourceLocation finishedTexture = new ResourceLocation(GsonHelper.getAsString(json, "finished_texture", DEFAULT_FINISHED_TEXTURE.toString()));
        int cookingBubbleColor = GsonHelper.getAsInt(json, "cooking_bubble_color", DEFAULT_COOKING_BUBBLE_COLOR);
        int finishedBubbleColor = GsonHelper.getAsInt(json, "finished_bubble_color", DEFAULT_FINISHED_BUBBLE_COLOR);
        return new StockpotRecipe(recipeId, inputs, inputEntityType, result, time, soupBase, cookingTexture, finishedTexture, cookingBubbleColor, finishedBubbleColor);
    }

    @Override
    public @Nullable StockpotRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buf) {
        int ingredientsSize = buf.readVarInt();
        List<Ingredient> inputs = Lists.newArrayList();
        for (int i = 0; i < ingredientsSize; i++) {
            inputs.add(Ingredient.fromNetwork(buf));
        }
        @Nullable EntityType<?> inputEntityType = null;
        if (buf.readBoolean()) {
            ResourceLocation entityTypeId = buf.readResourceLocation();
            inputEntityType = ForgeRegistries.ENTITY_TYPES.getValue(entityTypeId);
        }
        ItemStack result = buf.readItem();
        int time = buf.readVarInt();
        Fluid soupBase = getSoupBaseFromNetwork(buf);
        ResourceLocation cookingTexture = buf.readResourceLocation();
        ResourceLocation finishedTexture = buf.readResourceLocation();
        int cookingBubbleColor = buf.readVarInt();
        int finishedBubbleColor = buf.readVarInt();
        return new StockpotRecipe(recipeId, inputs, inputEntityType, result, time, soupBase, cookingTexture, finishedTexture,
                cookingBubbleColor, finishedBubbleColor);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, StockpotRecipe recipe) {
        buffer.writeVarInt(recipe.getIngredients().size());
        recipe.getIngredients().forEach(i -> i.toNetwork(buffer));
        if (recipe.getInputEntityType() != null) {
            buffer.writeBoolean(true);
            buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(recipe.getInputEntityType())));
        } else {
            buffer.writeBoolean(false);
        }
        buffer.writeItem(recipe.getResult());
        buffer.writeVarInt(recipe.getTime());
        buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(recipe.getSoupBase())));
        buffer.writeResourceLocation(recipe.getCookingTexture());
        buffer.writeResourceLocation(recipe.getFinishedTexture());
        buffer.writeVarInt(recipe.getCookingBubbleColor());
        buffer.writeVarInt(recipe.getFinishedBubbleColor());
    }

    private Fluid getSoupBaseFromJson(JsonObject json) {
        if (!json.has("soup_base")) {
            return DEFAULT_SOUP_BASE;
        }
        ResourceLocation fluidId = new ResourceLocation(GsonHelper.getAsString(json, "soup_base"));
        return Objects.requireNonNullElse(ForgeRegistries.FLUIDS.getValue(fluidId), DEFAULT_SOUP_BASE);
    }

    private Fluid getSoupBaseFromNetwork(FriendlyByteBuf buf) {
        ResourceLocation fluidId = buf.readResourceLocation();
        return Objects.requireNonNullElse(ForgeRegistries.FLUIDS.getValue(fluidId), DEFAULT_SOUP_BASE);
    }
}
