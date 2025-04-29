package com.github.ysbbbbbb.kaleidoscopecookery.block;

import com.github.ysbbbbbb.kaleidoscopecookery.entity.EntitySit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CookStoolBlock extends HorizontalDirectionalBlock {
    public static final VoxelShape NORTH_SOUTH = Block.box(2, 0, 3, 14, 7, 13);
    public static final VoxelShape EAST_WEST = Block.box(3, 0, 2, 13, 7, 14);

    public CookStoolBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        List<EntitySit> entities = level.getEntitiesOfClass(EntitySit.class, new AABB(pos));
        if (entities.isEmpty()) {
            EntitySit entitySit = new EntitySit(level, pos);
            entitySit.setYRot(state.getValue(FACING).toYRot());
            level.addFreshEntity(entitySit);
            player.startRiding(entitySit, true);
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        level.getEntitiesOfClass(EntitySit.class, new AABB(pos)).forEach(Entity::discard);
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
