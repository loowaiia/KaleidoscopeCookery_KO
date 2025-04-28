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
    }
}
