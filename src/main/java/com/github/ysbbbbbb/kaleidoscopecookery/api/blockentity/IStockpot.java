package com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IStockpot {
    int PUT_SOUP_BASE = 0;
    int PUT_INGREDIENT = 1;
    int COOKING = 2;
    int FINISHED = 3;

    int getStatus();

    /**
     * 检查锅下方是否有热源
     *
     * @param level 使用者所处的 level
     * @return 如果有热源则返回 true，否则返回 false
     */
    boolean hasHeatSource(Level level);

    boolean hasLid();

    boolean onLitClick(Level level, LivingEntity user, ItemStack stack);

    boolean addSoupBase(Level level, LivingEntity user, ItemStack bucket);

    boolean removeSoupBase(Level level, LivingEntity user, ItemStack bucket);

    /**
     * 添加原料到锅中
     *
     * @param level     使用者所处的 level
     * @param user      使用者
     * @param itemStack 要添加的原料
     * @return 如果添加成功则返回 true，否则返回 false
     */
    boolean addIngredient(Level level, LivingEntity user, ItemStack itemStack);

    /**
     * 移除锅中的原料
     *
     * @param level 使用者所处的 level
     * @param user  使用者
     * @return 如果移除成功则返回 true，否则返回 false
     */
    boolean removeIngredient(Level level, LivingEntity user);

    /**
     * 从锅中取出产品
     *
     * @param level 使用者所处的 level
     * @param user  使用者
     * @param stack 取出产物时所持有的物品
     * @return 如果取出成功则返回 true，否则返回 false
     */
    boolean takeOutProduct(Level level, LivingEntity user, ItemStack stack);
}
