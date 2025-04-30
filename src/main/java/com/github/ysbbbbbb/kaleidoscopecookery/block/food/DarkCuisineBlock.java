package com.github.ysbbbbbb.kaleidoscopecookery.block.food;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DarkCuisineBlock extends FoodBlock {
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;

        level.addParticle(ParticleTypes.SMOKE,
                x + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                y + random.nextDouble() / 3,
                z + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                0, 0.02, 0);
    }
}
