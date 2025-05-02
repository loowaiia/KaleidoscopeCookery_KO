package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.*;
import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.DarkCuisineBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.SlimeBallMealBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.SuspiciousStirFryBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, KaleidoscopeCookery.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, KaleidoscopeCookery.MOD_ID);

    public static RegistryObject<Block> STOVE = BLOCKS.register("stove", StoveBlock::new);
    public static RegistryObject<Block> POT = BLOCKS.register("pot", PotBlock::new);
    public static RegistryObject<Block> SUSPICIOUS_STIR_FRY = BLOCKS.register("suspicious_stir_fry", SuspiciousStirFryBlock::new);
    public static RegistryObject<Block> DARK_CUISINE = BLOCKS.register("dark_cuisine", DarkCuisineBlock::new);
    public static RegistryObject<Block> SLIME_BALL_MEAL = BLOCKS.register("slime_ball_meal", SlimeBallMealBlock::new);
    public static RegistryObject<Block> FRUIT_BASKET = BLOCKS.register("fruit_basket", FruitBasketBlock::new);

    public static RegistryObject<Block> COOK_STOOL_OAK = BLOCKS.register("cook_stool_oak", CookStoolBlock::new);
    public static RegistryObject<Block> COOK_STOOL_SPRUCE = BLOCKS.register("cook_stool_spruce", CookStoolBlock::new);
    public static RegistryObject<Block> COOK_STOOL_ACACIA = BLOCKS.register("cook_stool_acacia", CookStoolBlock::new);
    public static RegistryObject<Block> COOK_STOOL_BAMBOO = BLOCKS.register("cook_stool_bamboo", CookStoolBlock::new);
    public static RegistryObject<Block> COOK_STOOL_BIRCH = BLOCKS.register("cook_stool_birch", CookStoolBlock::new);
    public static RegistryObject<Block> COOK_STOOL_CHERRY = BLOCKS.register("cook_stool_cherry", CookStoolBlock::new);
    public static RegistryObject<Block> COOK_STOOL_CRIMSON = BLOCKS.register("cook_stool_crimson", CookStoolBlock::new);
    public static RegistryObject<Block> COOK_STOOL_DARK_OAK = BLOCKS.register("cook_stool_dark_oak", CookStoolBlock::new);
    public static RegistryObject<Block> COOK_STOOL_JUNGLE = BLOCKS.register("cook_stool_jungle", CookStoolBlock::new);
    public static RegistryObject<Block> COOK_STOOL_MANGROVE = BLOCKS.register("cook_stool_mangrove", CookStoolBlock::new);
    public static RegistryObject<Block> COOK_STOOL_WARPED = BLOCKS.register("cook_stool_warped", CookStoolBlock::new);

    public static RegistryObject<BlockEntityType<PotBlockEntity>> POT_BE = BLOCK_ENTITIES.register("pot",
            () -> BlockEntityType.Builder.of(PotBlockEntity::new, POT.get()).build(null));
    public static RegistryObject<BlockEntityType<FruitBasketBlockEntity>> FRUIT_BASKET_BE = BLOCK_ENTITIES.register("fruit_basket",
            () -> BlockEntityType.Builder.of(FruitBasketBlockEntity::new, FRUIT_BASKET.get()).build(null));
}
