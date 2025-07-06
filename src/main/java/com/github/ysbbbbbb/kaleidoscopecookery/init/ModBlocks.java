package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.*;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.ChiliCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.LettuceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.RiceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.*;
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
    public static RegistryObject<Block> STOCKPOT = BLOCKS.register("stockpot", StockpotBlock::new);
    public static RegistryObject<Block> FRUIT_BASKET = BLOCKS.register("fruit_basket", FruitBasketBlock::new);
    public static RegistryObject<Block> CHOPPING_BOARD = BLOCKS.register("chopping_board", ChoppingBoardBlock::new);
    public static RegistryObject<Block> OIL_BLOCK = BLOCKS.register("oil_block", OilBlock::new);
    public static RegistryObject<Block> ENAMEL_BASIN = BLOCKS.register("enamel_basin", EnamelBasinBlock::new);
    public static RegistryObject<Block> KITCHENWARE_RACKS = BLOCKS.register("kitchenware_racks", KitchenwareRacksBlock::new);
    public static RegistryObject<Block> CHILI_RISTRA = BLOCKS.register("chili_ristra", ChiliRistraBlock::new);

    public static RegistryObject<Block> TOMATO_CROP = BLOCKS.register("tomato_crop", () -> new BaseCropBlock(ModItems.TOMATO, ModItems.TOMATO_SEED));
    public static RegistryObject<Block> CHILI_CROP = BLOCKS.register("chili_crop", ChiliCropBlock::new);
    public static RegistryObject<Block> LETTUCE_CROP = BLOCKS.register("lettuce_crop", LettuceCropBlock::new);
    public static RegistryObject<Block> RICE_CROP = BLOCKS.register("rice_crop", RiceCropBlock::new);

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
    public static RegistryObject<BlockEntityType<StockpotBlockEntity>> STOCKPOT_BE = BLOCK_ENTITIES.register("stockpot",
            () -> BlockEntityType.Builder.of(StockpotBlockEntity::new, STOCKPOT.get()).build(null));
    public static RegistryObject<BlockEntityType<FruitBasketBlockEntity>> FRUIT_BASKET_BE = BLOCK_ENTITIES.register("fruit_basket",
            () -> BlockEntityType.Builder.of(FruitBasketBlockEntity::new, FRUIT_BASKET.get()).build(null));
    public static RegistryObject<BlockEntityType<ChoppingBoardBlockEntity>> CHOPPING_BOARD_BE = BLOCK_ENTITIES.register("chopping_board",
            () -> BlockEntityType.Builder.of(ChoppingBoardBlockEntity::new, CHOPPING_BOARD.get()).build(null));
    public static RegistryObject<BlockEntityType<KitchenwareRacksBlockEntity>> KITCHENWARE_RACKS_BE = BLOCK_ENTITIES.register("kitchenware_racks",
            () -> BlockEntityType.Builder.of(KitchenwareRacksBlockEntity::new, KITCHENWARE_RACKS.get()).build(null));
}
