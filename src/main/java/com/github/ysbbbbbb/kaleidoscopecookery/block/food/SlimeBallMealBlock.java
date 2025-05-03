package com.github.ysbbbbbb.kaleidoscopecookery.block.food;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods.SLIME_BALL_MEAL_BLOCK;

public class SlimeBallMealBlock extends FoodBlock {
    public static final int MAX_BITES = 3;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 3);

    public SlimeBallMealBlock() {
        super();
        this.registerDefaultState(this.stateDefinition.any().setValue(BITES, 0).setValue(FACING, Direction.SOUTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemInHand = player.getItemInHand(hand);
        int bites = state.getValue(BITES);
        if (bites >= MAX_BITES) {
            level.removeBlock(pos, false);
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            level.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.PLAYERS);
            ItemHandlerHelper.giveItemToPlayer(player, Items.BOWL.getDefaultInstance());
            return InteractionResult.SUCCESS;
        }
        if (level.isClientSide) {
            if (eat(level, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            if (itemInHand.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return eat(level, pos, state, player);
    }

    private InteractionResult eat(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        }
        player.getFoodData().eat(SLIME_BALL_MEAL_BLOCK.getNutrition(), SLIME_BALL_MEAL_BLOCK.getSaturationModifier());
        level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        int bites = state.getValue(BITES);
        level.gameEvent(player, GameEvent.EAT, pos);
        if (bites < MAX_BITES) {
            level.setBlock(pos, state.setValue(BITES, bites + 1), Block.UPDATE_ALL);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(BITES, FACING);
    }

    @Override
    public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        int value = pBlockState.getValue(BITES);
        return (3 - value) * 5;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
}
