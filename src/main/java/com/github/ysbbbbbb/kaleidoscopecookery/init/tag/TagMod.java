package com.github.ysbbbbbb.kaleidoscopecookery.init.tag;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface TagMod {
    /**
     * 任意可以点燃本模组炉灶的物品
     */
    TagKey<Item> LIT_STOVE = itemTag("lit_stove");
    /**
     * 任意可以熄灭本模组炉灶的物品
     */
    TagKey<Item> EXTINGUISH_STOVE = itemTag("extinguish_stove");
    /**
     * 任意可以用作炒锅油的物品
     */
    TagKey<Item> OIL = itemTag("oil");
    /**
     * 可以当做草帽的物品
     */
    TagKey<Item> STRAW_HAT = itemTag("straw_hat");
    /**
     * 可以当本模组菜刀的物品
     */
    TagKey<Item> KITCHEN_KNIFE = itemTag("kitchen_knife");

    static TagKey<Item> itemTag(String name) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(KaleidoscopeCookery.MOD_ID, name));
    }

    static TagKey<Block> blockTag(String name) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(KaleidoscopeCookery.MOD_ID, name));
    }
}
