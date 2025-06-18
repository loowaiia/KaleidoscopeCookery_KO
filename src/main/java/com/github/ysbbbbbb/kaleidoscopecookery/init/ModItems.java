package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.item.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, KaleidoscopeCookery.MOD_ID);

    public static RegistryObject<Item> STOVE = ITEMS.register("stove", () -> new BlockItem(ModBlocks.STOVE.get(), new Item.Properties()));
    public static RegistryObject<Item> POT = ITEMS.register("pot", () -> new BlockItem(ModBlocks.POT.get(), new Item.Properties()));
    public static RegistryObject<Item> STOCKPOT = ITEMS.register("stockpot", () -> new BlockItem(ModBlocks.STOCKPOT.get(), new Item.Properties()));
    public static RegistryObject<Item> STOCKPOT_LID = ITEMS.register("stockpot_lid", () -> new Item(new Item.Properties().stacksTo(1)));
    public static RegistryObject<Item> OIL = ITEMS.register("oil", () -> new Item(new Item.Properties()));
    public static RegistryObject<Item> OIL_BLOCK = ITEMS.register("oil_block", () -> new BlockItem(ModBlocks.OIL_BLOCK.get(), new Item.Properties()));

    public static RegistryObject<Item> IRON_KITCHEN_KNIFE = ITEMS.register("iron_kitchen_knife", () -> new KitchenKnifeItem(Tiers.IRON));
    public static RegistryObject<Item> GOLD_KITCHEN_KNIFE = ITEMS.register("gold_kitchen_knife", () -> new KitchenKnifeItem(Tiers.GOLD));
    public static RegistryObject<Item> DIAMOND_KITCHEN_KNIFE = ITEMS.register("diamond_kitchen_knife", () -> new KitchenKnifeItem(Tiers.DIAMOND));
    public static RegistryObject<Item> NETHERITE_KITCHEN_KNIFE = ITEMS.register("netherite_kitchen_knife", () -> new KitchenKnifeItem(Tiers.NETHERITE));

    public static RegistryObject<Item> KITCHEN_SHOVEL = ITEMS.register("kitchen_shovel", KitchenShovelItem::new);
    public static RegistryObject<Item> FRUIT_BASKET = ITEMS.register("fruit_basket", FruitBasketItem::new);
    public static RegistryObject<Item> SCARECROW = ITEMS.register("scarecrow", ScarecrowItem::new);
    public static RegistryObject<Item> STRAW_HAT = ITEMS.register("straw_hat", () -> new StrawHatItem(false));
    public static RegistryObject<Item> STRAW_HAT_FLOWER = ITEMS.register("straw_hat_flower", () -> new StrawHatItem(true));
    public static RegistryObject<Item> CHOPPING_BOARD = ITEMS.register("chopping_board", () -> new BlockItem(ModBlocks.CHOPPING_BOARD.get(), new Item.Properties()));
    public static RegistryObject<Item> TOMATO_SEED = ITEMS.register("tomato_seed", () -> new ItemNameBlockItem(ModBlocks.TOMATO_CROP.get(), new Item.Properties()));
    public static RegistryObject<Item> CHILI_SEED = ITEMS.register("chili_seed", () -> new ItemNameBlockItem(ModBlocks.CHILI_CROP.get(), new Item.Properties()));
    public static RegistryObject<Item> LETTUCE_SEED = ITEMS.register("lettuce_seed", () -> new ItemNameBlockItem(ModBlocks.LETTUCE_CROP.get(), new Item.Properties()));
    public static RegistryObject<Item> RICE_SEED = ITEMS.register("rice", () -> new ItemNameBlockItem(ModBlocks.RICE_CROP.get(), new Item.Properties()));

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
    public static RegistryObject<Item> RED_CHILI = ITEMS.register("red_chili", () -> new Item(new Item.Properties().food(ModFoods.CHILI)));
    public static RegistryObject<Item> GREEN_CHILI = ITEMS.register("green_chili", () -> new Item(new Item.Properties().food(ModFoods.CHILI)));
    public static RegistryObject<Item> LETTUCE = ITEMS.register("lettuce", () -> new Item(new Item.Properties().food(ModFoods.LETTUCE)));
    public static RegistryObject<Item> RICE_PANICLE = ITEMS.register("rice_panicle", () -> new Item(new Item.Properties()));
    public static RegistryObject<Item> CATERPILLAR = ITEMS.register("caterpillar", () -> new Item(new Item.Properties().food(ModFoods.CATERPILLAR)));
    public static RegistryObject<Item> FRIED_EGG = ITEMS.register("fried_egg", () -> new Item(new Item.Properties().food(ModFoods.FRIED_EGG)));
    public static RegistryObject<Item> SCRAMBLE_EGG_WITH_TOMATOES = ITEMS.register("scramble_egg_with_tomatoes", () -> new Item(new Item.Properties().food(ModFoods.SCRAMBLE_EGG_WITH_TOMATOES)));

    public static RegistryObject<Item> SASHIMI = ITEMS.register("sashimi", () -> new Item(new Item.Properties().food(ModFoods.SASHIMI)));
    public static RegistryObject<Item> RAW_LAMB_CHOPS = ITEMS.register("raw_lamb_chops", () -> new Item(new Item.Properties().food(ModFoods.RAW_LAMB_CHOPS)));
    public static RegistryObject<Item> RAW_COW_OFFAL = ITEMS.register("raw_cow_offal", () -> new Item(new Item.Properties().food(ModFoods.RAW_COW_OFFAL)));
    public static RegistryObject<Item> RAW_PORK_BELLY = ITEMS.register("raw_pork_belly", () -> new Item(new Item.Properties().food(ModFoods.RAW_PORK_BELLY)));
    public static RegistryObject<Item> COOKED_LAMB_CHOPS = ITEMS.register("cooked_lamb_chops", () -> new Item(new Item.Properties().food(ModFoods.COOKED_LAMB_CHOPS)));
    public static RegistryObject<Item> COOKED_COW_OFFAL = ITEMS.register("cooked_cow_offal", () -> new Item(new Item.Properties().food(ModFoods.COOKED_COW_OFFAL)));
    public static RegistryObject<Item> COOKED_PORK_BELLY = ITEMS.register("cooked_pork_belly", () -> new Item(new Item.Properties().food(ModFoods.COOKED_PORK_BELLY)));
    public static RegistryObject<Item> COOKED_RICE = ITEMS.register("cooked_rice", () -> new Item(new Item.Properties().food(ModFoods.COOKED_RICE)));
}