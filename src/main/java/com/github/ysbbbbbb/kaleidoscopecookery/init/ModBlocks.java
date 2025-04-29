package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.CookStool;
import com.github.ysbbbbbb.kaleidoscopecookery.block.FoodBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.PotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.StoveBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.PotBlockEntity;
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
    public static RegistryObject<Block> SUSPICIOUS_STIR_FRY = BLOCKS.register("suspicious_stir_fry", FoodBlock::new);
    public static RegistryObject<Block> DARK_CUISINE = BLOCKS.register("dark_cuisine", FoodBlock::new);

    public static RegistryObject<Block> COOK_STOOL_OAK = BLOCKS.register("cook_stool_oak", CookStool::new);
    public static RegistryObject<Block> COOK_STOOL_SPRUCE = BLOCKS.register("cook_stool_spruce", CookStool::new);
    public static RegistryObject<Block> COOK_STOOL_ACACIA = BLOCKS.register("cook_stool_acacia", CookStool::new);
    public static RegistryObject<Block> COOK_STOOL_BAMBOO = BLOCKS.register("cook_stool_bamboo", CookStool::new);
    public static RegistryObject<Block> COOK_STOOL_BIRCH = BLOCKS.register("cook_stool_birch", CookStool::new);
    public static RegistryObject<Block> COOK_STOOL_CHERRY = BLOCKS.register("cook_stool_cherry", CookStool::new);
    public static RegistryObject<Block> COOK_STOOL_CRIMSON = BLOCKS.register("cook_stool_crimson", CookStool::new);
    public static RegistryObject<Block> COOK_STOOL_DARK_OAK = BLOCKS.register("cook_stool_dark_oak", CookStool::new);
    public static RegistryObject<Block> COOK_STOOL_JUNGLE = BLOCKS.register("cook_stool_jungle", CookStool::new);
    public static RegistryObject<Block> COOK_STOOL_MANGROVE = BLOCKS.register("cook_stool_mangrove", CookStool::new);
    public static RegistryObject<Block> COOK_STOOL_WARPED = BLOCKS.register("cook_stool_warped", CookStool::new);

    public static RegistryObject<BlockEntityType<PotBlockEntity>> POT_BE = BLOCK_ENTITIES.register("pot",
            () -> BlockEntityType.Builder.of(PotBlockEntity::new, POT.get()).build(null));
}
