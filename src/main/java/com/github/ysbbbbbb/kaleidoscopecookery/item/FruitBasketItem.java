package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.ItemContainerTooltip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Optional;

public class FruitBasketItem extends BlockItem {
    private static final String TAG = "BlockEntityTag";

    public FruitBasketItem() {
        super(ModBlocks.FRUIT_BASKET.get(), new Properties().stacksTo(1));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG)) {
            CompoundTag compound = tag.getCompound(TAG);
            if (compound.contains(FruitBasketBlockEntity.ITEMS)) {
                ItemStackHandler handler = new ItemStackHandler(8);
                handler.deserializeNBT(compound.getCompound(FruitBasketBlockEntity.ITEMS));
                return Optional.of(new ItemContainerTooltip(handler));
            }
        }
        return Optional.empty();
    }
}
