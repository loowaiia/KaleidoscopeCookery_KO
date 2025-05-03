package com.github.ysbbbbbb.kaleidoscopecookery.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.ItemHandlerHelper;

public class BowlFoodBlockItem extends BlockItem {
    public BowlFoodBlockItem(Block pBlock, FoodProperties properties) {
        super(pBlock, new Item.Properties().stacksTo(16).food(properties));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack itemStack = super.finishUsingItem(stack, level, entity);
        if (stack.getCount() <= 1) {
            return new ItemStack(Items.BOWL);
        }
        if (entity instanceof Player player) {
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.BOWL));
        } else {
            if (!level.isClientSide()) {
                ItemEntity itemEntity = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Items.BOWL));
                level.addFreshEntity(itemEntity);
            }
        }
        return itemStack;
    }
}
