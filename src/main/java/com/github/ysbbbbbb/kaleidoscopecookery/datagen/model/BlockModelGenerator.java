package com.github.ysbbbbbb.kaleidoscopecookery.datagen.model;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.stream.IntStream;

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

        crop("tomato", 8);
        crop("chili", 8);
        crop("lettuce", 8);

        riceCrop();
    }

    public void cookStool(String name) {
        String path = "block/cook_stool/" + name;
        withExistingParent(path, modLoc("block/cook_stool/cook_stool")).texture("particle", modLoc(path));
    }

    public void crop(String name, int stage) {
        IntStream.range(0, stage).forEach(i -> {
            String id = "block/crop/%s/stage%d".formatted(name, i);
            cross(id, modLoc(id)).renderType("cutout");
        });
    }

    public void riceCrop() {
        IntStream.range(0, 8).forEach(i -> {
            String down = "block/crop/rice/stage%d_down".formatted(i);
            String middle = "block/crop/rice/stage%d_middle".formatted(i);
            String up = "block/crop/rice/stage%d_up".formatted(i);
            cross(down, modLoc(down)).renderType("cutout");
            cross(middle, modLoc(middle)).renderType("cutout");
            cross(up, modLoc(up)).renderType("cutout");
        });
    }
}
