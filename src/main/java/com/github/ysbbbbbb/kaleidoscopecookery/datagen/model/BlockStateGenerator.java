package com.github.ysbbbbbb.kaleidoscopecookery.datagen.model;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.PotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.StoveBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

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
            if (blockState.getValue(PotBlock.HAS_OIL)) {
                return new ModelFile.UncheckedModelFile(modLoc("block/pot_has_oil"));
            } else {
                return new ModelFile.UncheckedModelFile(modLoc("block/pot"));
            }
        });

        simpleBlock(ModBlocks.SUSPICIOUS_STIR_FRY.get(), new ModelFile.UncheckedModelFile(modLoc("block/suspicious_stir_fry")));
    }
}
