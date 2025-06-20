package com.github.ysbbbbbb.kaleidoscopecookery.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;

public class BowlFoodOnlyItem extends Item {
    public BowlFoodOnlyItem(FoodProperties properties) {
        super(new Item.Properties().food(properties));
    }

    public BowlFoodOnlyItem(int nutrition, float saturation) {
        super(new Item.Properties().food((new FoodProperties.Builder())
                .nutrition(nutrition)
                .saturationMod(saturation)
                .alwaysEat()
                .build()
        ));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack itemStack = super.finishUsingItem(stack, level, entity);
        ItemStack bowl = new ItemStack(Items.BOWL);
        if (itemStack.isEmpty()) {
            return bowl;
        }
        if (entity instanceof Player player) {
            ItemHandlerHelper.giveItemToPlayer(player, bowl);
        } else {
            ItemEntity itemEntity = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), bowl);
            level.addFreshEntity(itemEntity);
        }
        return itemStack;
    }
}
