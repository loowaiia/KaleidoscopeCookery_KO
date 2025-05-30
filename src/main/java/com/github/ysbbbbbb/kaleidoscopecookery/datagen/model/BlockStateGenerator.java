package com.github.ysbbbbbb.kaleidoscopecookery.datagen.model;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.PotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.StoveBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class BlockStateGenerator extends BlockStateProvider {
    public BlockStateGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, KaleidoscopeCookery.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(ModBlocks.STOVE.get(), blockState -> {
            if (blockState.getValue(StoveBlock.LIT)) {
                return new ModelFile.UncheckedModelFile(modLoc("block/stove_lit"));
            } else {
                return new ModelFile.UncheckedModelFile(modLoc("block/stove"));
            }
        });

        horizontalBlock(ModBlocks.POT.get(), blockState -> {
            if (blockState.getValue(PotBlock.HAS_OIL) && blockState.getValue(PotBlock.SHOW_OIL)) {
                return new ModelFile.UncheckedModelFile(modLoc("block/pot_has_oil"));
            } else {
                return new ModelFile.UncheckedModelFile(modLoc("block/pot"));
            }
        });

        addFoodBiteBlock(ModBlocks.SLIME_BALL_MEAL, "slime_ball_meal");
        addFoodBiteBlock(ModBlocks.FONDANT_PIE, "fondant_pie");

        horizontalBlock(ModBlocks.FRUIT_BASKET.get(), new ModelFile.UncheckedModelFile(modLoc("block/fruit_basket")));
        horizontalBlock(ModBlocks.CHOPPING_BOARD.get(), new ModelFile.UncheckedModelFile(modLoc("block/chopping_board")));

        simpleBlock(ModBlocks.SUSPICIOUS_STIR_FRY.get(), new ModelFile.UncheckedModelFile(modLoc("block/suspicious_stir_fry")));
        simpleBlock(ModBlocks.DARK_CUISINE.get(), new ModelFile.UncheckedModelFile(modLoc("block/dark_cuisine")));

        cookStool(ModBlocks.COOK_STOOL_OAK, "oak");
        cookStool(ModBlocks.COOK_STOOL_SPRUCE, "spruce");
        cookStool(ModBlocks.COOK_STOOL_ACACIA, "acacia");
        cookStool(ModBlocks.COOK_STOOL_BAMBOO, "bamboo");
        cookStool(ModBlocks.COOK_STOOL_BIRCH, "birch");
        cookStool(ModBlocks.COOK_STOOL_CHERRY, "cherry");
        cookStool(ModBlocks.COOK_STOOL_CRIMSON, "crimson");
        cookStool(ModBlocks.COOK_STOOL_DARK_OAK, "dark_oak");
        cookStool(ModBlocks.COOK_STOOL_JUNGLE, "jungle");
        cookStool(ModBlocks.COOK_STOOL_MANGROVE, "mangrove");
        cookStool(ModBlocks.COOK_STOOL_WARPED, "warped");


        getVariantBuilder(ModBlocks.TOMATO_CROP.get()).forAllStates(state -> {
            int age = state.getValue(CropBlock.AGE);
            ResourceLocation file = modLoc("block/crop/tomato_stage" + age);
            return ConfiguredModel.builder()
                    .modelFile(new ModelFile.UncheckedModelFile(file))
                    .build();
        });
    }

    public void cookStool(RegistryObject<Block> block, String name) {
        horizontalBlock(block.get(), new ModelFile.UncheckedModelFile(modLoc("block/cook_stool/" + name)));
    }

    public void addFoodBiteBlock(RegistryObject<Block> block, String name) {
        horizontalBlock(block.get(), blockState -> {
            int bites = blockState.getValue(FoodBiteBlock.BITES);
            if (bites >= FoodBiteBlock.MAX_BITES) {
                return new ModelFile.UncheckedModelFile(modLoc("block/plate"));
            }
            return new ModelFile.UncheckedModelFile(modLoc("block/%s/%s_%d".formatted(name, name, bites)));
        });
    }
}
