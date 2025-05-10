package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.PotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class KitchenShovelItem extends ShovelItem {
    public KitchenShovelItem() {
        super(Tiers.IRON, -1, -2.0F, new Properties());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (level.getBlockEntity(clickedPos) instanceof PotBlockEntity potBlockEntity
            && player != null && player.isSecondaryUseActive()
            && potBlockEntity.getStatus() == PotBlockEntity.FINISHED
            && !potBlockEntity.isNeedBowl()) {
            potBlockEntity.takeOut(player);
            return InteractionResult.SUCCESS;
        }
        return super.onItemUseFirst(stack, context);
    }
}
