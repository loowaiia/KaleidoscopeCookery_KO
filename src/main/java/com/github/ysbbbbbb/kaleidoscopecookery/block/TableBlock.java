package com.github.ysbbbbbb.kaleidoscopecookery.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.TableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.ysbbbbbb.kaleidoscopecookery.util.CarpetColor.getCarpetByColor;
import static com.github.ysbbbbbb.kaleidoscopecookery.util.CarpetColor.getColorByCarpet;

public class TableBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 0, 3);
    public static final BooleanProperty HAS_CARPET = BooleanProperty.create("has_carpet");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final int SINGLE = 0;
    public static final int LEFT = 1;
    public static final int MIDDLE = 2;
    public static final int RIGHT = 3;

    private static final VoxelShape FACE = Block.box(0, 13, 0, 16, 16, 16);

    public TableBlock() {
        super(Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .noOcclusion()
                .ignitedByLava());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AXIS, Direction.Axis.Z)
                .setValue(POSITION, SINGLE)
                .setValue(HAS_CARPET, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND) {
            if (itemInHand.is(ItemTags.WOOL_CARPETS)) {
                return useWithCarpets(state, level, pos, itemInHand);
            } else if (level.getBlockEntity(pos) instanceof TableBlockEntity table) {
                return useWithOther(level, pos, player, hand, table, itemInHand);
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @NotNull
    private InteractionResult useWithOther(Level level, BlockPos pos, Player player, InteractionHand hand, TableBlockEntity table, ItemStack itemInHand) {
        ItemStack tableItem = table.getItemStack();
        boolean handEmpty = itemInHand.isEmpty();
        boolean tableHasItem = !tableItem.isEmpty();

        if (tableHasItem && handEmpty) {
            // 玩家手为空，桌子有物品：取出桌子物品
            level.playSound(player, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, player.getSoundSource(), 1.0F, 1.0F);
            popResource(level, pos.above(), tableItem.copy());
            table.setItemStack(ItemStack.EMPTY);
            table.setChanged();
            return InteractionResult.SUCCESS;
        } else if (!handEmpty && !tableHasItem) {
            // 玩家手有物品，桌子为空：放入物品
            table.setItemStack(itemInHand.copy());
            table.setChanged();
            player.setItemInHand(hand, ItemStack.EMPTY);
            level.playSound(player, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, player.getSoundSource(), 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        } else if (!handEmpty && tableHasItem) {
            // 玩家手有物品，桌子也有物品：先取出桌子物品，再放入新物品
            popResource(level, pos.above(), tableItem.copy());
            table.setItemStack(itemInHand.copy());
            table.setChanged();
            player.setItemInHand(hand, ItemStack.EMPTY);
            level.playSound(player, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, player.getSoundSource(), 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        // 玩家手为空，桌子也为空：无操作
        return InteractionResult.PASS;
    }

    @NotNull
    private InteractionResult useWithCarpets(BlockState state, Level level, BlockPos pos, ItemStack itemInHand) {
        @Nullable DyeColor dyeColor = getColorByCarpet(itemInHand.getItem());
        boolean hasCarpet = state.getValue(HAS_CARPET);
        if (dyeColor == null) {
            return InteractionResult.PASS;
        }

        // 第一种情况，桌子上没有地毯
        if (!hasCarpet) {
            level.setBlockAndUpdate(pos, state.setValue(HAS_CARPET, true));
            if (level.getBlockEntity(pos) instanceof TableBlockEntity tableBlockEntity) {
                tableBlockEntity.setColor(dyeColor);
                tableBlockEntity.setChanged();
                itemInHand.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }

        // 第二种情况：有地毯，但是颜色不一致
        if (hasCarpet && level.getBlockEntity(pos) instanceof TableBlockEntity tableBlockEntity && tableBlockEntity.getColor() != dyeColor) {
            // 掉落原地毯
            DyeColor originalColor = tableBlockEntity.getColor();
            ItemStack carpetItem = getCarpetByColor(originalColor).getDefaultInstance();
            popResource(level, pos.above(), carpetItem);

            tableBlockEntity.setColor(dyeColor);
            tableBlockEntity.setChanged();
            level.setBlockAndUpdate(pos, state.setValue(HAS_CARPET, true));
            itemInHand.shrink(1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        List<ItemStack> drops = super.getDrops(pState, pParams);
        BlockEntity parameter = pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof TableBlockEntity table) {
            if (pState.getValue(HAS_CARPET)) {
                Item carpet = getCarpetByColor(table.getColor());
                drops.add(new ItemStack(carpet));
            }
            if (!table.getItemStack().isEmpty()) {
                drops.add(table.getItemStack().copy());
            }
        }
        return drops;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, POSITION, HAS_CARPET, WATERLOGGED);
    }

    /**
     * 根据指定方向和位置，计算桌子的连接状态和朝向
     */
    private BlockState getConnectedState(LevelAccessor level, BlockPos pos, BlockState baseState) {
        // 先判断南北方向
        BlockState northState = level.getBlockState(pos.north());
        BlockState southState = level.getBlockState(pos.south());
        if (northState.is(this) && southState.is(this)) {
            return baseState.setValue(POSITION, MIDDLE).setValue(AXIS, Direction.Axis.Z);
        }
        if (!northState.is(this) && southState.is(this)) {
            return baseState.setValue(POSITION, LEFT).setValue(AXIS, Direction.Axis.Z);
        }
        if (northState.is(this) && !southState.is(this)) {
            return baseState.setValue(POSITION, RIGHT).setValue(AXIS, Direction.Axis.Z);
        }

        // 再判断东西方向
        BlockState westState = level.getBlockState(pos.west());
        BlockState eastState = level.getBlockState(pos.east());
        if (eastState.is(this) && westState.is(this)) {
            return baseState.setValue(POSITION, MIDDLE).setValue(AXIS, Direction.Axis.X);
        }
        if (eastState.is(this) && !westState.is(this)) {
            return baseState.setValue(POSITION, LEFT).setValue(AXIS, Direction.Axis.X);
        }
        if (!eastState.is(this) && westState.is(this)) {
            return baseState.setValue(POSITION, RIGHT).setValue(AXIS, Direction.Axis.X);
        }

        // 都没有则为单独
        return baseState.setValue(POSITION, SINGLE);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        // 只要有邻居变化，重新计算连接状态
        return getConnectedState(level, pos, state);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        boolean hasWater = level.getFluidState(clickedPos).getType() == Fluids.WATER;
        BlockState base = this.defaultBlockState().setValue(WATERLOGGED, hasWater);
        return getConnectedState(level, clickedPos, base);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return FACE;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TableBlockEntity(pPos, pState);
    }
}
