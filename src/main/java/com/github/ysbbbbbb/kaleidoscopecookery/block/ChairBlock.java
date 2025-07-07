package com.github.ysbbbbbb.kaleidoscopecookery.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.ChairBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.ysbbbbbb.kaleidoscopecookery.util.CarpetColor.getCarpetByColor;
import static com.github.ysbbbbbb.kaleidoscopecookery.util.CarpetColor.getColorByCarpet;

public class ChairBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty HAS_CARPET = BooleanProperty.create("has_carpet");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape BASE = Block.box(2, 0, 2, 14, 9, 14);
    private static final VoxelShape NORTH = Shapes.or(BASE, Block.box(2, 0, 12, 14, 19, 14));
    private static final VoxelShape SOUTH = Shapes.or(BASE, Block.box(2, 0, 2, 14, 19, 4));
    private static final VoxelShape WEST = Shapes.or(BASE, Block.box(12, 0, 2, 14, 19, 14));
    private static final VoxelShape EAST = Shapes.or(BASE, Block.box(2, 0, 2, 4, 19, 14));

    public ChairBlock() {
        super(Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .noOcclusion()
                .ignitedByLava());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(HAS_CARPET, false)
                .setValue(WATERLOGGED, false));
    }


    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND && itemInHand.is(ItemTags.WOOL_CARPETS)) {
            @Nullable DyeColor dyeColor = getColorByCarpet(itemInHand.getItem());
            boolean hasCarpet = state.getValue(HAS_CARPET);
            if (dyeColor == null) {
                return InteractionResult.PASS;
            }

            // 第一种情况，椅子上没有地毯
            if (!hasCarpet) {
                level.setBlockAndUpdate(pos, state.setValue(HAS_CARPET, true));
                if (level.getBlockEntity(pos) instanceof ChairBlockEntity chairBlockEntity) {
                    chairBlockEntity.setColor(dyeColor);
                    chairBlockEntity.setChanged();
                    itemInHand.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }

            // 第二种情况：有地毯，但是颜色不一致
            if (hasCarpet && level.getBlockEntity(pos) instanceof ChairBlockEntity chairBlockEntity && chairBlockEntity.getColor() != dyeColor) {
                // 掉落原地毯
                DyeColor originalColor = chairBlockEntity.getColor();
                ItemStack carpetItem = getCarpetByColor(originalColor).getDefaultInstance();
                popResource(level, pos, carpetItem);

                chairBlockEntity.setColor(dyeColor);
                chairBlockEntity.setChanged();
                level.setBlockAndUpdate(pos, state.setValue(HAS_CARPET, true));
                itemInHand.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }

        List<SitEntity> entities = level.getEntitiesOfClass(SitEntity.class, new AABB(pos));
        if (entities.isEmpty()) {
            SitEntity entitySit = new SitEntity(level, pos, 0.5125);
            entitySit.setYRot(state.getValue(FACING).toYRot());
            level.addFreshEntity(entitySit);
            player.startRiding(entitySit, true);
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        level.getEntitiesOfClass(SitEntity.class, new AABB(pos)).forEach(Entity::discard);
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        List<ItemStack> drops = super.getDrops(pState, pParams);
        BlockEntity parameter = pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (pState.getValue(HAS_CARPET) && parameter instanceof ChairBlockEntity chair) {
            Item carpet = getCarpetByColor(chair.getColor());
            drops.add(new ItemStack(carpet));
        }
        return drops;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_CARPET, WATERLOGGED);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            default -> NORTH;
        };
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pState.getValue(HAS_CARPET)) {
            return new ChairBlockEntity(pPos, pState);
        }
        return null;
    }
}
