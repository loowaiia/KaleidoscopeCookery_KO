package com.github.ysbbbbbb.kaleidoscopecookery.datagen.model;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockModelGenerator extends BlockModelProvider {
    public BlockModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, KaleidoscopeCookery.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ResourceLocation stoveSide = modLoc("block/stove_side");
        ResourceLocation stoveTop = modLoc("block/stove_top");
        ResourceLocation stoveTopLit = modLoc("block/stove_top_lit");

        cube("stove", stoveTop, stoveTop, modLoc("block/stove_front"), stoveSide, stoveSide, stoveSide).texture("particle", stoveSide);
        cube("stove_lit", stoveTopLit, stoveTopLit, modLoc("block/stove_front_lit"), stoveSide, stoveSide, stoveSide).texture("particle", stoveSide);

        cookStool("oak");
        cookStool("spruce");
        cookStool("acacia");
        cookStool("bamboo");
        cookStool("birch");
        cookStool("cherry");
        cookStool("crimson");
        cookStool("dark_oak");
        cookStool("jungle");
        cookStool("mangrove");
        cookStool("warped");
    }

    public void cookStool(String name) {
        String path = "block/cook_stool/" + name;
        withExistingParent(path, modLoc("block/cook_stool/cook_stool")).texture("particle", modLoc(path));
    }
}
