package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.item.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, KaleidoscopeCookery.MOD_ID);

    public static RegistryObject<Item> STOVE = ITEMS.register("stove", () -> new BlockItem(ModBlocks.STOVE.get(), new Item.Properties()));
    public static RegistryObject<Item> POT = ITEMS.register("pot", () -> new BlockItem(ModBlocks.POT.get(), new Item.Properties()));
    public static RegistryObject<Item> OIL = ITEMS.register("oil", () -> new Item(new Item.Properties()));
    public static RegistryObject<Item> KITCHEN_KNIFE = ITEMS.register("kitchen_knife", KitchenKnifeItem::new);
    public static RegistryObject<Item> KITCHEN_SHOVEL = ITEMS.register("kitchen_shovel", KitchenShovelItem::new);
    public static RegistryObject<Item> SUSPICIOUS_STIR_FRY = ITEMS.register("suspicious_stir_fry", () -> new BowlFoodBlockItem(ModBlocks.SUSPICIOUS_STIR_FRY.get(), ModFoods.SUSPICIOUS_STIR_FRY));
    public static RegistryObject<Item> DARK_CUISINE = ITEMS.register("dark_cuisine", () -> new BowlFoodBlockItem(ModBlocks.DARK_CUISINE.get(), ModFoods.DARK_CUISINE));
    public static RegistryObject<Item> FRUIT_BASKET = ITEMS.register("fruit_basket", FruitBasketItem::new);
    public static RegistryObject<Item> SCARECROW = ITEMS.register("scarecrow", ScarecrowItem::new);
    public static RegistryObject<Item> CHOPPING_BOARD = ITEMS.register("chopping_board", () -> new BlockItem(ModBlocks.CHOPPING_BOARD.get(), new Item.Properties()));

    public static RegistryObject<Item> COOK_STOOL_OAK = ITEMS.register("cook_stool_oak", () -> new BlockItem(ModBlocks.COOK_STOOL_OAK.get(), new Item.Properties()));
    public static RegistryObject<Item> COOK_STOOL_SPRUCE = ITEMS.register("cook_stool_spruce", () -> new BlockItem(ModBlocks.COOK_STOOL_SPRUCE.get(), new Item.Properties()));
    public static RegistryObject<Item> COOK_STOOL_ACACIA = ITEMS.register("cook_stool_acacia", () -> new BlockItem(ModBlocks.COOK_STOOL_ACACIA.get(), new Item.Properties()));
    public static RegistryObject<Item> COOK_STOOL_BAMBOO = ITEMS.register("cook_stool_bamboo", () -> new BlockItem(ModBlocks.COOK_STOOL_BAMBOO.get(), new Item.Properties()));
    public static RegistryObject<Item> COOK_STOOL_BIRCH = ITEMS.register("cook_stool_birch", () -> new BlockItem(ModBlocks.COOK_STOOL_BIRCH.get(), new Item.Properties()));
    public static RegistryObject<Item> COOK_STOOL_CHERRY = ITEMS.register("cook_stool_cherry", () -> new BlockItem(ModBlocks.COOK_STOOL_CHERRY.get(), new Item.Properties()));
    public static RegistryObject<Item> COOK_STOOL_CRIMSON = ITEMS.register("cook_stool_crimson", () -> new BlockItem(ModBlocks.COOK_STOOL_CRIMSON.get(), new Item.Properties()));
    public static RegistryObject<Item> COOK_STOOL_DARK_OAK = ITEMS.register("cook_stool_dark_oak", () -> new BlockItem(ModBlocks.COOK_STOOL_DARK_OAK.get(), new Item.Properties()));
    public static RegistryObject<Item> COOK_STOOL_JUNGLE = ITEMS.register("cook_stool_jungle", () -> new BlockItem(ModBlocks.COOK_STOOL_JUNGLE.get(), new Item.Properties()));
    public static RegistryObject<Item> COOK_STOOL_MANGROVE = ITEMS.register("cook_stool_mangrove", () -> new BlockItem(ModBlocks.COOK_STOOL_MANGROVE.get(), new Item.Properties()));
    public static RegistryObject<Item> COOK_STOOL_WARPED = ITEMS.register("cook_stool_warped", () -> new BlockItem(ModBlocks.COOK_STOOL_WARPED.get(), new Item.Properties()));

    public static RegistryObject<Item> TOMATO = ITEMS.register("tomato", () -> new Item(new Item.Properties().food(ModFoods.TOMATO)));
    public static RegistryObject<Item> FRIED_EGG = ITEMS.register("fried_egg", () -> new Item(new Item.Properties().food(ModFoods.FRIED_EGG)));
    public static RegistryObject<Item> SCRAMBLE_EGG_WITH_TOMATOES = ITEMS.register("scramble_egg_with_tomatoes", () -> new Item(new Item.Properties().food(ModFoods.SCRAMBLE_EGG_WITH_TOMATOES)));
    public static RegistryObject<Item> CARAMEL_HONEY_COOKIE_FRAGMENTS = ITEMS.register("caramel_honey_cookie_fragments", () -> new BowlFoodBlockItem(ModBlocks.CARAMEL_HONEY_COOKIE_FRAGMENTS.get(), ModFoods.CARAMEL_HONEY_COOKIE_FRAGMENTS_ITEM));
    public static RegistryObject<Item> SLIME_BALL_MEAL = ITEMS.register("slime_ball_meal", () -> new BowlFoodBlockItem(ModBlocks.SLIME_BALL_MEAL.get(), ModFoods.SLIME_BALL_MEAL_ITEM));
}