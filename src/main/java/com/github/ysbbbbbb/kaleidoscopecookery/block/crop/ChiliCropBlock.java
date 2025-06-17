package com.github.ysbbbbbb.kaleidoscopecookery.block.crop;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ChiliCropBlock extends BaseCropBlock {
    public ChiliCropBlock() {
        super(ModItems.RED_CHILI, ModItems.CHILI_SEED);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pState.getValue(AGE) >= this.getMaxAge()) {
            if (!pLevel.isClientSide) {
                ItemEntity entity = new ItemEntity(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), this.result.get().getDefaultInstance());
                entity.setPickUpDelay(10);
                pLevel.addFreshEntity(entity);
                // 20% 几率掉落青椒
                if (pLevel.getRandom().nextInt(5) == 0) {
                    ItemEntity extra = new ItemEntity(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), ModItems.GREEN_CHILI.get().getDefaultInstance());
                    extra.setPickUpDelay(10);
                    pLevel.addFreshEntity(extra);
                }
                pLevel.playSound(null, pPos.getX(), pPos.getY(), pPos.getZ(), SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                pLevel.setBlock(pPos, this.getStateForAge(5), Block.UPDATE_CLIENTS);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
