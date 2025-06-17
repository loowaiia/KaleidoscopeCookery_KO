package com.github.ysbbbbbb.kaleidoscopecookery.block.crop;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class RiceCropBlock extends BaseCropBlock implements LiquidBlockContainer {
    public static final int UP = 2;
    public static final int MIDDLE = 1;
    public static final int DOWN = 0;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty LOCATION = IntegerProperty.create("location", DOWN, UP);

    private static final VoxelShape BASE_SHAPE = Block.box(2, 0, 2, 14, 16, 14);
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            // 此时只有两格高
            Block.box(2, 0, 2, 14, 6, 14),
            Block.box(2, 0, 2, 14, 8, 14),
            Block.box(2, 0, 2, 14, 10, 14),
            Block.box(2, 0, 2, 14, 12, 14),
            // 此时有三格高
            Block.box(2, 0, 2, 14, 0, 14),
            Block.box(2, 0, 2, 14, 2, 14),
            Block.box(2, 0, 2, 14, 4, 14),
            Block.box(2, 0, 2, 14, 6, 14)
    };

    public RiceCropBlock() {
        super(ModItems.RICE_PANICLE, ModItems.RICE_SEED);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(this.getAgeProperty(), 0)
                .setValue(WATERLOGGED, false)
                .setValue(LOCATION, DOWN));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor levelAccessor, BlockPos currentPos, BlockPos facingPos) {
        return !state.canSurvive(levelAccessor, currentPos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        Level level = context.getLevel();
        boolean isWaterlogged = level.getFluidState(blockPos).is(FluidTags.WATER);
        boolean aboveMatches = level.getBlockState(blockPos.above(MIDDLE)).isAir();
        boolean above2Matches = level.getBlockState(blockPos.above(UP)).isAir();
        return blockPos.getY() < level.getMaxBuildHeight() - 2 && isWaterlogged && aboveMatches && above2Matches
                ? this.defaultBlockState().setValue(WATERLOGGED, true) : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), this.getStateForAge(0).setValue(LOCATION, MIDDLE), Block.UPDATE_ALL);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
        if (levelReader.getRawBrightness(pos, 0) < 8 && !levelReader.canSeeSky(pos)) {
            return false;
        }
        int location = state.getValue(LOCATION);
        BlockPos startPos = pos.below(location);
        BlockPos basePos = startPos.below();

        BlockState downState = levelReader.getBlockState(startPos);
        BlockState middleState = levelReader.getBlockState(startPos.above(MIDDLE));
        BlockState upState = levelReader.getBlockState(startPos.above(UP));

        boolean baseMatches = this.mayPlaceOn(levelReader.getBlockState(basePos), levelReader, basePos);

        // 如果是首次放置的判断
        if (!downState.is(this)) {
            boolean downMatches = downState.getFluidState().is(FluidTags.WATER);
            boolean middleMatches = middleState.isAir();
            boolean upMatches = upState.isAir();
            return baseMatches && downMatches && middleMatches && upMatches;
        }

        boolean downMatches = downState.is(this) && downState.getValue(LOCATION) == DOWN;
        boolean middleMatches = middleState.is(this) && middleState.getValue(LOCATION) == MIDDLE;
        boolean upMatches;
        if (isThreeBlock(downState)) {
            // 当 age 大于 3 时，此时水稻是三格高
            upMatches = upState.is(this) && upState.getValue(LOCATION) == UP;
        } else {
            upMatches = upState.isAir();
        }

        return baseMatches && downMatches && middleMatches && upMatches;
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pTe, ItemStack pStack) {
        super.playerDestroy(pLevel, pPlayer, pPos, Blocks.AIR.defaultBlockState(), pTe, pStack);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            if (player.isCreative()) {
                int location = state.getValue(LOCATION);
                if (location != DOWN) {
                    BlockPos startPos = pos.below(location);
                    BlockState startState = level.getBlockState(startPos);
                    if (startState.is(state.getBlock()) && startState.getValue(LOCATION) == DOWN) {
                        BlockState blockState = startState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                        level.setBlock(startPos, blockState, Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
                        level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, startPos, Block.getId(startState));
                    }
                }
            } else {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (!isThreeBlock(state) && state.getValue(LOCATION) == MIDDLE) {
            return SHAPE_BY_AGE[state.getValue(AGE)];
        }
        if (isThreeBlock(state) && state.getValue(LOCATION) == UP) {
            return SHAPE_BY_AGE[state.getValue(AGE)];
        }
        return BASE_SHAPE;
    }

    private boolean isThreeBlock(BlockState state) {
        return state.getValue(AGE) > 3;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return super.isRandomlyTicking(state) && state.getValue(LOCATION) == DOWN;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        if (!serverLevel.isAreaLoaded(pos, 1)) {
            return;
        }
        if (state.getValue(LOCATION) != DOWN) {
            return;
        }
        if (serverLevel.getRawBrightness(pos, 0) >= 9) {
            int age = this.getAge(state);
            if (age < this.getMaxAge()) {
                // 生长速度慢 2 倍
                float speed = getGrowthSpeed(this, serverLevel, pos) / 2;
                if (ForgeHooks.onCropsGrowPre(serverLevel, pos, state, random.nextInt((int) (25.0F / speed) + 1) == 0)) {
                    setCropState(serverLevel, pos, age + 1);
                    ForgeHooks.onCropsGrowPost(serverLevel, pos, state);
                }
            }
        }
    }

    private void setCropState(Level level, BlockPos startPos, int age) {
        level.setBlock(startPos, this.getStateForAge(age).setValue(WATERLOGGED, true), Block.UPDATE_CLIENTS);
        level.setBlock(startPos.above(MIDDLE), this.getStateForAge(age).setValue(LOCATION, MIDDLE), Block.UPDATE_CLIENTS);
        if (isThreeBlock(level.getBlockState(startPos))) {
            level.setBlock(startPos.above(UP), this.getStateForAge(age).setValue(LOCATION, UP), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        int boneAge = this.getAge(state) + this.getBonemealAgeIncrease(level);
        int maxAge = this.getMaxAge();
        if (boneAge > maxAge) {
            boneAge = maxAge;
        }
        setCropState(level, pos.below(state.getValue(LOCATION)), boneAge);
    }

    @Override
    protected int getBonemealAgeIncrease(Level level) {
        return Mth.nextInt(level.random, 1, 2);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(AGE) >= this.getMaxAge()) {
            if (!level.isClientSide) {
                ItemStack stack = this.result.get().getDefaultInstance();
                stack.setCount(Mth.nextInt(level.random, 1, 3));
                ItemEntity entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                entity.setPickUpDelay(10);
                level.addFreshEntity(entity);
                setCropState(level, pos.below(state.getValue(LOCATION)), 5);
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, LOCATION, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder pParams) {
        if (state.getValue(LOCATION) != DOWN) {
            return Collections.emptyList();
        }
        return super.getDrops(state, pParams);
    }
}
