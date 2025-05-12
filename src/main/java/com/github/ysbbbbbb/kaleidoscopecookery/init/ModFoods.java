package com.github.ysbbbbbb.kaleidoscopecookery.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties SUSPICIOUS_STIR_FRY = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.1F).alwaysEat()
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60), 0.2F)
            .effect(() -> new MobEffectInstance(MobEffects.JUMP, 60), 0.2F)
            .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 60), 0.2F)
            .effect(() -> new MobEffectInstance(MobEffects.LUCK, 60), 0.2F).build();

    public static final FoodProperties DARK_CUISINE = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.1F).alwaysEat()
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 60), 0.3F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 60), 0.5F).build();

    public static final FoodProperties TOMATO = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).alwaysEat().build();
    public static final FoodProperties FRIED_EGG = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).alwaysEat().build();
    public static final FoodProperties SCRAMBLE_EGG_WITH_TOMATOES = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.8F).alwaysEat().build();

    public static final FoodProperties FONDANT_PIE_ITEM = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.8F).alwaysEat().build();
    public static final FoodProperties FONDANT_PIE_BLOCK = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).alwaysEat().build();

    public static final FoodProperties SLIME_BALL_MEAL_ITEM = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.2F).alwaysEat().build();
    public static final FoodProperties SLIME_BALL_MEAL_BLOCK = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).alwaysEat().build();
}
