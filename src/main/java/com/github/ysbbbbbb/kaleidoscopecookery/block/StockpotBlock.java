package com.github.ysbbbbbb.kaleidoscopecookery.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StockpotBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty HAS_LID = BooleanProperty.create("has_lid");
    public static final BooleanProperty HAS_BASE = BooleanProperty.create("has_base");
    public static final VoxelShape AABB = Shapes.or(Block.box(2, 0, 2, 14, 5, 14),
            Block.box(1, 5, 1, 15, 7, 15));
    public static final VoxelShape AABB_WITH_LID = Shapes.or(Block.box(2, 0, 2, 14, 9, 14),
            Block.box(1, 5, 1, 15, 7, 15));

    public StockpotBlock() {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .sound(ModSoundType.POT).noOcclusion()
                .strength(1.5F, 6.0F));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(WATERLOGGED, false)
                .setValue(HAS_LID, false)
                .setValue(HAS_BASE, false));
    }

    @Nullable
    @SuppressWarnings("all")
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> pServerType, BlockEntityType<E> pClientType, BlockEntityTicker<? super E> pTicker) {
        return pClientType == pServerType ? (BlockEntityTicker<A>) pTicker : null;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState neighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos neighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        // 如果下方是不完整方块，则添加基座
        if (pDirection == Direction.DOWN) {
            return pState.setValue(HAS_BASE, !neighborState.isFaceSturdy(pLevel, neighborPos, Direction.UP));
        }
        return super.updateShape(pState, pDirection, neighborState, pLevel, pPos, neighborPos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof StockpotBlockEntity stockpot)) {
            return InteractionResult.PASS;
        }
        ItemStack stack = player.getMainHandItem();
        if (state.getValue(HAS_LID) || stack.is(ModItems.STOCKPOT_LID.get())) {
            stockpot.onLitClick(player);
        }
        if (stack.getItem() instanceof BucketItem) {
            stockpot.onBucketClick(player);
            return InteractionResult.SUCCESS;
        }
        int status = stockpot.getStatus();
        if (status == StockpotBlockEntity.PUT_INGREDIENT) {
            stockpot.onIngredientClick(player);
            return InteractionResult.SUCCESS;
        }
        if (status == StockpotBlockEntity.FINISHED && player.getMainHandItem().is(Items.BOWL)) {
            stockpot.onBowlClick(player);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, pHand, pHit);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new StockpotBlockEntity(pPos, pState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide) {
            return createTickerHelper(pBlockEntityType, ModBlocks.STOCKPOT_BE.get(), (level, pos, state, pot) -> pot.clientTick());
        }
        return createTickerHelper(pBlockEntityType, ModBlocks.STOCKPOT_BE.get(), (level, pos, state, pot) -> pot.tick());
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        BlockState blockState = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        // 如果下方是不完整方块，则添加基座
        BlockState state = context.getLevel().getBlockState(context.getClickedPos().below());
        if (!state.isFaceSturdy(context.getLevel(), context.getClickedPos().below(), Direction.UP)) {
            return blockState.setValue(HAS_BASE, true);
        }
        return blockState;
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, HAS_LID, HAS_BASE);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pState.getValue(HAS_LID)) {
            return AABB_WITH_LID;
        }
        return AABB;
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        List<ItemStack> drops = super.getDrops(pState, pParams);
        if (pState.getValue(HAS_LID)) {
            BlockEntity parameter = pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (parameter instanceof StockpotBlockEntity stockpot && !stockpot.getLidItem().isEmpty()) {
                drops.add(stockpot.getLidItem().copy());
            } else {
                drops.add(new ItemStack(ModItems.STOCKPOT_LID.get()));
            }
        }
        BlockEntity parameter = pParams.getParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof StockpotBlockEntity stockpotBlock && stockpotBlock.getStatus() == StockpotBlockEntity.PUT_INGREDIENT) {
            stockpotBlock.getItems().forEach(stack -> {
                if (!stack.isEmpty()) {
                    drops.add(stack);
                }
            });
        }
        return drops;
    }
}
