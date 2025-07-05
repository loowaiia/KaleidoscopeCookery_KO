package com.github.ysbbbbbb.kaleidoscopecookery.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class KitchenwareRacksBlockEntity extends BlockEntity {
    private static final String LEFT_ITEM = "LeftItem";
    private static final String RIGHT_ITEM = "RightItem";

    private ItemStack itemLeft = ItemStack.EMPTY;
    private ItemStack itemRight = ItemStack.EMPTY;

    public KitchenwareRacksBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.KITCHENWARE_RACKS_BE.get(), pPos, pBlockState);
    }

    public boolean onClick(Player player, ItemStack stack, boolean isLeft) {
        ItemStack stackInRacks = isLeft ? itemLeft : itemRight;

        if (stack.isEmpty() && !stackInRacks.isEmpty()) {
            if (player.getMainHandItem().isEmpty()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, stackInRacks);
            } else {
                ItemHandlerHelper.giveItemToPlayer(player, stackInRacks);
            }
            if (isLeft) {
                itemLeft = ItemStack.EMPTY;
            } else {
                itemRight = ItemStack.EMPTY;
            }
            player.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            this.refresh();
            return true;
        }

        if (stack.is(Tags.Items.TOOLS) && stackInRacks.isEmpty()) {
            if (isLeft) {
                itemLeft = stack.split(1);
            } else {
                itemRight = stack.split(1);
            }
            player.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
            this.refresh();
            return true;
        }

        return false;
    }

    public void refresh() {
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(LEFT_ITEM, itemLeft.save(new CompoundTag()));
        tag.put(RIGHT_ITEM, itemRight.save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.itemLeft = ItemStack.of(tag.getCompound(LEFT_ITEM));
        this.itemRight = ItemStack.of(tag.getCompound(RIGHT_ITEM));
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ItemStack getItemLeft() {
        return itemLeft;
    }

    public ItemStack getItemRight() {
        return itemRight;
    }
}
