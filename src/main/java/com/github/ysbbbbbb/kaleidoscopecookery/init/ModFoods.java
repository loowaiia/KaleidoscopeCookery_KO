package com.github.ysbbbbbb.kaleidoscopecookery.init;

import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties SUSPICIOUS_STIR_FRY = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.1F).build();
    public static final FoodProperties DARK_CUISINE = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.1F).build();
    public static final FoodProperties FRIED_EGG = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).build();
    public static final FoodProperties SLIME_BALL_MEAL_ITEM = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.2F).build();
    public static final FoodProperties SLIME_BALL_MEAL_BLOCK = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).build();
}
