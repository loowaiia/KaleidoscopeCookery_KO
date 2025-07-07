package com.github.ysbbbbbb.kaleidoscopecookery.datagen.model;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
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

        cross("block/chili_ristra/head", modLoc("block/chili_ristra/head")).renderType("cutout");
        cross("block/chili_ristra/body", modLoc("block/chili_ristra/body")).renderType("cutout");
        cross("block/chili_ristra/head_sheared", modLoc("block/chili_ristra/head_sheared")).renderType("cutout");
        cross("block/chili_ristra/body_sheared", modLoc("block/chili_ristra/body_sheared")).renderType("cutout");

        cubeColumn("straw_block", modLoc("block/straw_block_side"), modLoc("block/straw_block_end"));

        wood("oak");
        wood("spruce");
        wood("acacia");
        wood("bamboo");
        wood("birch");
        wood("cherry");
        wood("crimson");
        wood("dark_oak");
        wood("jungle");
        wood("mangrove");
        wood("warped");

        crop("tomato", 8);
        crop("chili", 8);
        crop("lettuce", 8);

        riceCrop();
        carpet();
    }

    public void wood(String name) {
        String cookStool = "block/cook_stool/" + name;
        withExistingParent(cookStool, modLoc("block/cook_stool/cook_stool")).texture("particle", modLoc(cookStool));

        String chair = "block/chair/" + name;
        withExistingParent(chair, modLoc("block/chair/chair")).texture("particle", modLoc(chair));
    }

    public void carpet() {
        for (DyeColor color : DyeColor.values()) {
            String name = color.getName();
            String carpet = "block/carpet/chair/" + name;
            withExistingParent(carpet, modLoc("block/carpet/chair/chair_carpet")).texture("particle", modLoc(carpet));
        }
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
