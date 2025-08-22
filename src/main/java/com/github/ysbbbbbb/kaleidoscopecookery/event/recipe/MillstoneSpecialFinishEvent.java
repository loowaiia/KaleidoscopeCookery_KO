package com.github.ysbbbbbb.kaleidoscopecookery.event.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.MillstoneFinishEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.OilPotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class MillstoneSpecialFinishEvent {
    @SubscribeEvent
    public static void onMillstoneTakeItem(MillstoneFinishEvent event) {
        MillstoneBlockEntity millstone = event.getMillstone();
        ItemStack output = millstone.getOutput();
        Level level = millstone.getLevel();
        if (level == null) {
            return;
        }
        // 如果是种子油且下方是油壶，那么自动化输出
        if (!output.is(ModItems.OIL_POT.get())) {
            return;
        }
        // 如果下方是油壶，那么自动化输出
        BlockPos below = millstone.getBlockPos().below();
        BlockState blockState = level.getBlockState(below);
        if (!blockState.is(ModBlocks.OIL_POT.get())) {
            return;
        }
        // 如果油壶满了，那么不输出
        int oilCount = blockState.getValue(OilPotBlock.OIL_COUNT);
        if (oilCount >= OilPotBlock.MAX_OIL_COUNT) {
            return;
        }
        // 不足 8 个时，概率产出
        RandomSource random = level.getRandom();
        if (random.nextInt(8) < output.getCount()) {
            level.setBlockAndUpdate(below, blockState.setValue(OilPotBlock.OIL_COUNT, oilCount + 1));
        }
        millstone.resetWhenTakeout();
    }
}
