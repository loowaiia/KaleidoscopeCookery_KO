package com.github.ysbbbbbb.kaleidoscopecookery.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

public class ItemUtils {
    public static boolean itemHandlerIsEmpty(IItemHandler itemHandler) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static Pair<Integer, ItemStack> getLastStack(IItemHandler itemHandler) {
        for (int i = itemHandler.getSlots(); i > 0; i--) {
            int index = i - 1;
            ItemStack stack = itemHandler.getStackInSlot(index);
            if (!stack.isEmpty()) {
                return Pair.of(index, stack);
            }
        }
        return Pair.of(0, ItemStack.EMPTY);
    }
}
