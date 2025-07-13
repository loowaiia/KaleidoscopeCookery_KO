package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemStackHandler;

public class TableBlockEntity extends BlockEntity {
    private static final String COLOR_TAG = "CarpetColor";
    private static final String SHOW_ITEMS = "ShowItems";

    private DyeColor color = DyeColor.WHITE;
    private ItemStackHandler items = new ItemStackHandler(4);

    public TableBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.TABLE_BE.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(COLOR_TAG, this.color.getId());
        tag.put(SHOW_ITEMS, this.items.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(COLOR_TAG)) {
            this.color = DyeColor.byId(tag.getInt(COLOR_TAG));
        }
        if (tag.contains(SHOW_ITEMS)) {
            this.items.deserializeNBT(tag.getCompound(SHOW_ITEMS));
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(this.worldPosition);
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

    public ItemStackHandler getItems() {
        return items;
    }
}
