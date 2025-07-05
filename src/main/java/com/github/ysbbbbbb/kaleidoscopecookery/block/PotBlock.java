package com.github.ysbbbbbb.kaleidoscopecookery.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoundType;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.item.KitchenShovelItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PotBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty HAS_OIL = BooleanProperty.create("has_oil");
    public static final BooleanProperty SHOW_OIL = BooleanProperty.create("show_oil");
    public static final VoxelShape AABB = Block.box(2, 0, 2, 14, 4, 14);

    public PotBlock() {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .sound(ModSoundType.POT).noOcclusion()
                .strength(1.5F, 6.0F));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(HAS_OIL, false)
                .setValue(SHOW_OIL, false).setValue(WATERLOGGED, false));
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Nullable
    @SuppressWarnings("all")
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> pServerType, BlockEntityType<E> pClientType, BlockEntityTicker<? super E> pTicker) {
        return pClientType == pServerType ? (BlockEntityTicker<A>) pTicker : null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand == InteractionHand.OFF_HAND) {
            return super.use(state, level, pos, player, pHand, pHit);
        }

        // 判断热源
        BlockState belowState = level.getBlockState(pos.below());
        if (!belowState.hasProperty(BlockStateProperties.LIT) || !belowState.getValue(BlockStateProperties.LIT)) {
            sendBarMessage(player, Component.translatable("tip.kaleidoscope_cookery.pot.need_lit_stove"));
            return InteractionResult.FAIL;
        }

        ItemStack itemInHand = player.getItemInHand(pHand);
        RandomSource random = level.random;

        // 放油
        if (!state.getValue(HAS_OIL)) {
            if (itemInHand.is(TagMod.OIL)) {
                placeOil(state, level, pos, player, random);
                itemInHand.shrink(1);
                return InteractionResult.SUCCESS;
            }
            if (itemInHand.is(ModItems.KITCHEN_SHOVEL.get()) && KitchenShovelItem.hasOil(itemInHand)) {
                placeOil(state, level, pos, player, random);
                KitchenShovelItem.setHasOil(itemInHand, false);
                return InteractionResult.SUCCESS;
            }
            sendBarMessage(player, Component.translatable("tip.kaleidoscope_cookery.pot.need_oil"));
            return InteractionResult.FAIL;
        }

        // 炒菜等内容
        if (level.getBlockEntity(pos) instanceof PotBlockEntity pot) {
            cooking(level, pos, player, pot, itemInHand, random);
            return InteractionResult.SUCCESS;
        }

        return super.use(state, level, pos, player, pHand, pHit);
    }

    private void sendBarMessage(Player player, MutableComponent message) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(message));
        }
    }

    private void cooking(Level level, BlockPos pos, Player player, PotBlockEntity pot, ItemStack itemInHand, RandomSource random) {
        if (itemInHand.is(ModItems.KITCHEN_SHOVEL.get())) {
            pot.onShovelHit(level, player, itemInHand);
            level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F,
                    1F + (random.nextFloat() - random.nextFloat()) * 0.8F);
            return;
        }

        if (pot.getStatus() == PotBlockEntity.FINISHED) {
            pot.takeOut(player);
            return;
        }

        pot.addIngredient(itemInHand, player);
    }

    private void placeOil(BlockState pState, Level level, BlockPos pos, Player player, RandomSource random) {
        level.setBlockAndUpdate(pos, pState.setValue(HAS_OIL, true).setValue(SHOW_OIL, true));
        level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1F,
                (random.nextFloat() - random.nextFloat()) * 0.8F);
        for (int i = 0; i < 10; i++) {
            level.addParticle(ParticleTypes.SMOKE,
                    pos.getX() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    pos.getY() + 0.25 + random.nextDouble() / 3,
                    pos.getZ() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    0, 0.05, 0);
        }
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PotBlockEntity(pPos, pState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide) {
            return null;
        }
        if (!pState.getValue(HAS_OIL)) {
            return null;
        }
        return createTickerHelper(pBlockEntityType, ModBlocks.POT_BE.get(),
                (level, pos, state, pot) -> pot.tick());
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_OIL, SHOW_OIL, FACING, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AABB;
    }
}
