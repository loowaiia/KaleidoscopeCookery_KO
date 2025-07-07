package com.github.ysbbbbbb.kaleidoscopecookery.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChairBlockEntity extends BlockEntity {
    private static final String COLOR_TAG = "CarpetColor";
    private DyeColor color = DyeColor.WHITE;

    public ChairBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.CHAIR_BE.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(COLOR_TAG, this.color.getId());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.color = DyeColor.byId(tag.getInt(COLOR_TAG));
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }
}
