package com.github.ysbbbbbb.kaleidoscopecookery.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.ChoppingBoardBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.tag.TagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ChoppingBoardBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final VoxelShape NORTH_SOUTH = Block.box(1, 0, 2, 15, 2, 14);
    public static final VoxelShape EAST_WEST = Block.box(2, 0, 1, 14, 2, 15);

    public ChoppingBoardBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH));
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof ChoppingBoardBlockEntity choppingBoard) {
            if (choppingBoard.onPutOn(player.getItemInHand(hand))) {
                return InteractionResult.SUCCESS;
            }
            ItemStack mainHandItem = player.getMainHandItem();
            if (hand == InteractionHand.OFF_HAND) {
                return InteractionResult.PASS;
            }
            if (choppingBoard.onCut(player)) {
                return InteractionResult.SUCCESS;
            }
            if (choppingBoard.onTakeOut(player)) {
                return InteractionResult.SUCCESS;
            }
            if (mainHandItem.is(TagItem.KITCHEN_KNIFE) && player.getOffhandItem().isEmpty()) {
                choppingBoard.playParticlesSound();
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(blockState, level, pos, player, hand, hit);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChoppingBoardBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (state.getValue(FACING).getAxis() == Direction.Axis.Z) {
            return NORTH_SOUTH;
        }
        return EAST_WEST;
    }
}
