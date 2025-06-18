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

    public static final FoodProperties DARK_CUISINE_BLOCK = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.1F).alwaysEat()
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 60), 0.3F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 60), 0.5F).build();

    public static final FoodProperties DARK_CUISINE_ITEM = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.02F).alwaysEat()
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 15), 0.3F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 15), 0.5F).build();

    public static final FoodProperties TOMATO = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).alwaysEat().build();
    public static final FoodProperties CHILI = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).alwaysEat().build();
    public static final FoodProperties LETTUCE = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).alwaysEat().build();
    public static final FoodProperties RICE = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).alwaysEat().build();
    public static final FoodProperties CATERPILLAR = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).alwaysEat().build();
    public static final FoodProperties FRIED_EGG = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).alwaysEat().build();
    public static final FoodProperties SCRAMBLE_EGG_WITH_TOMATOES = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.8F).alwaysEat().build();
    public static final FoodProperties SASHIMI = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).alwaysEat().build();
    public static final FoodProperties RAW_LAMB_CHOPS = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).alwaysEat().build();
    public static final FoodProperties RAW_COW_OFFAL = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).alwaysEat().build();
    public static final FoodProperties RAW_PORK_BELLY = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).alwaysEat().build();

    public static final FoodProperties COOKED_LAMB_CHOPS = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.8F).alwaysEat().build();
    public static final FoodProperties COOKED_COW_OFFAL = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.8F).alwaysEat().build();
    public static final FoodProperties COOKED_PORK_BELLY = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.8F).alwaysEat().build();
    public static final FoodProperties COOKED_RICE = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.8F).alwaysEat().build();
}
