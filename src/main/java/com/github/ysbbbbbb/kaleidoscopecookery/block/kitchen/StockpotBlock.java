package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
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
            BlockEntityType<A> serverType, BlockEntityType<E> clientType, BlockEntityTicker<? super E> ticker) {
        return clientType == serverType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        // 如果下方是不完整方块，则添加基座
        if (direction == Direction.DOWN) {
            return state.setValue(HAS_BASE, !neighborState.isFaceSturdy(levelAccessor, neighborPos, Direction.UP));
        }
        return super.updateShape(state, direction, neighborState, levelAccessor, pos, neighborPos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof StockpotBlockEntity stockpot)) {
            return InteractionResult.PASS;
        }
        ItemStack stack = player.getMainHandItem();
        if (state.getValue(HAS_LID) || stack.is(ModItems.STOCKPOT_LID.get())) {
            stockpot.onLitClick(player);
            return InteractionResult.SUCCESS;
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
        return InteractionResult.PASS;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StockpotBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return createTickerHelper(blockEntityType, ModBlocks.STOCKPOT_BE.get(),
                    (lvl, blockPos, blockState, pot) -> pot.clientTick());
        }
        return createTickerHelper(blockEntityType, ModBlocks.STOCKPOT_BE.get(),
                (lvl, blockPos, blockState, pot) -> pot.tick());
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        BlockState blockState = this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        // 如果下方是不完整方块，则添加基座
        BlockState belowState = context.getLevel().getBlockState(context.getClickedPos().below());
        if (!belowState.isFaceSturdy(context.getLevel(), context.getClickedPos().below(), Direction.UP)) {
            return blockState.setValue(HAS_BASE, true);
        }
        return blockState;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, HAS_LID, HAS_BASE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        if (state.getValue(HAS_LID)) {
            return AABB_WITH_LID;
        }
        return AABB;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder lootParamsBuilder) {
        List<ItemStack> drops = super.getDrops(state, lootParamsBuilder);
        if (state.getValue(HAS_LID)) {
            BlockEntity parameter = lootParamsBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (parameter instanceof StockpotBlockEntity stockpot && !stockpot.getLidItem().isEmpty()) {
                drops.add(stockpot.getLidItem().copy());
            } else {
                drops.add(new ItemStack(ModItems.STOCKPOT_LID.get()));
            }
        }
        BlockEntity parameter = lootParamsBuilder.getParameter(LootContextParams.BLOCK_ENTITY);
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
