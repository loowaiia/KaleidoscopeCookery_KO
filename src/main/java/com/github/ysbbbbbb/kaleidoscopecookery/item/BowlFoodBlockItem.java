package com.github.ysbbbbbb.kaleidoscopecookery.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BowlFoodBlockItem extends BlockItem {
    public BowlFoodBlockItem(Block pBlock, FoodProperties properties) {
        super(pBlock, new Item.Properties().food(properties));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack itemstack = super.finishUsingItem(stack, level, entity);
        return entity instanceof Player player && player.getAbilities().instabuild ? itemstack : new ItemStack(Items.BOWL);
    }
}
