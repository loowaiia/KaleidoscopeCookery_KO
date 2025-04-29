package com.github.ysbbbbbb.kaleidoscopecookery.datagen.model;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider {
    public ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, KaleidoscopeCookery.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent("stove", modLoc("block/stove"));
        withExistingParent("pot", modLoc("block/pot"));
        withExistingParent("oil", "item/generated").texture("layer0", modLoc("item/oil"));
        withExistingParent("kitchen_knife", "item/handheld").texture("layer0", modLoc("item/kitchen_knife"));
        withExistingParent("kitchen_shovel", "item/handheld").texture("layer0", modLoc("item/kitchen_shovel"));
        withExistingParent("suspicious_stir_fry", "item/generated").texture("layer0", modLoc("item/suspicious_stir_fry"));
        withExistingParent("dark_cuisine", "item/generated").texture("layer0", modLoc("item/dark_cuisine"));
        withExistingParent("cook_stool_oak", modLoc("block/cook_stool/oak"));
        withExistingParent("cook_stool_spruce", modLoc("block/cook_stool/spruce"));
        withExistingParent("cook_stool_acacia", modLoc("block/cook_stool/acacia"));
        withExistingParent("cook_stool_bamboo", modLoc("block/cook_stool/bamboo"));
        withExistingParent("cook_stool_birch", modLoc("block/cook_stool/birch"));
        withExistingParent("cook_stool_cherry", modLoc("block/cook_stool/cherry"));
        withExistingParent("cook_stool_crimson", modLoc("block/cook_stool/crimson"));
        withExistingParent("cook_stool_dark_oak", modLoc("block/cook_stool/dark_oak"));
        withExistingParent("cook_stool_jungle", modLoc("block/cook_stool/jungle"));
        withExistingParent("cook_stool_mangrove", modLoc("block/cook_stool/mangrove"));
        withExistingParent("cook_stool_warped", modLoc("block/cook_stool/warped"));
    }
}
