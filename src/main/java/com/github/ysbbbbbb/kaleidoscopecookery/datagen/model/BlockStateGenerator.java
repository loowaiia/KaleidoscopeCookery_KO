package com.github.ysbbbbbb.kaleidoscopecookery.datagen.model;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.PotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.StoveBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.RiceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
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

        FoodBiteRegistry.FOOD_DATA_MAP.forEach((key, value) -> {
            Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block != null) {
                addFoodBiteBlock(block, key);
            }
        });

        horizontalBlock(ModBlocks.FRUIT_BASKET.get(), new ModelFile.UncheckedModelFile(modLoc("block/fruit_basket")));
        horizontalBlock(ModBlocks.CHOPPING_BOARD.get(), new ModelFile.UncheckedModelFile(modLoc("block/chopping_board")));

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

        simpleBlock(ModBlocks.OIL_BLOCK.get());

        crop(ModBlocks.TOMATO_CROP, "tomato");
        crop(ModBlocks.CHILI_CROP, "chili");
        crop(ModBlocks.LETTUCE_CROP, "lettuce");

        riceCrop();
    }

    public void crop(RegistryObject<Block> block, String name) {
        getVariantBuilder(block.get()).forAllStates(state -> {
            int age = state.getValue(CropBlock.AGE);
            ResourceLocation file = modLoc("block/crop/%s/stage%d".formatted(name, age));
            return ConfiguredModel.builder()
                    .modelFile(new ModelFile.UncheckedModelFile(file))
                    .build();
        });
    }

    public void riceCrop() {
        getVariantBuilder(ModBlocks.RICE_CROP.get()).forAllStates(state -> {
            int age = state.getValue(CropBlock.AGE);
            int location = state.getValue(RiceCropBlock.LOCATION);
            ResourceLocation file;
            if (location == RiceCropBlock.DOWN) {
                file = modLoc("block/crop/rice/stage%d_down".formatted(age));
            } else if (location == RiceCropBlock.MIDDLE) {
                file = modLoc("block/crop/rice/stage%d_middle".formatted(age));
            } else {
                file = modLoc("block/crop/rice/stage%d_up".formatted(age));
            }
            return ConfiguredModel.builder()
                    .modelFile(new ModelFile.UncheckedModelFile(file))
                    .build();
        });
    }

    public void cookStool(RegistryObject<Block> block, String name) {
        horizontalBlock(block.get(), new ModelFile.UncheckedModelFile(modLoc("block/cook_stool/" + name)));
    }

    public void addFoodBiteBlock(Block block, ResourceLocation id) {
        horizontalBlock(block, blockState -> {
            if (!(blockState.getBlock() instanceof FoodBiteBlock foodBiteBlock)) {
                throw new IllegalArgumentException("Block must be an instance of FoodBiteBlock");
            }
            int bites = blockState.getValue(foodBiteBlock.getBites());
            ResourceLocation model = new ResourceLocation(id.getNamespace(), "block/food/%s/%s_%d".formatted(id.getPath(), id.getPath(), bites));
            return new ModelFile.UncheckedModelFile(model);
        });
    }
}
