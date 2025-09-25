package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SteamerBlockEntity extends BaseBlockEntity {
    public SteamerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.STEAMER_BE.get(), pos, state);
    }
}
