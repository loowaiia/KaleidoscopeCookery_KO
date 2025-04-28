package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.item.ItemKitchenKnife;
import com.github.ysbbbbbb.kaleidoscopecookery.item.ItemKitchenShovel;
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
    public static RegistryObject<Item> KITCHEN_KNIFE = ITEMS.register("kitchen_knife", ItemKitchenKnife::new);
    public static RegistryObject<Item> KITCHEN_SHOVEL = ITEMS.register("kitchen_shovel", ItemKitchenShovel::new);
    public static RegistryObject<Item> SUSPICIOUS_STIR_FRY = ITEMS.register("suspicious_stir_fry", () ->
            new BlockItem(ModBlocks.SUSPICIOUS_STIR_FRY.get(), new Item.Properties().food(ModFoods.SUSPICIOUS_STIR_FRY)));
}