package com.github.ysbbbbbb.kaleidoscopecookery.util;

import net.minecraft.nbt.CompoundTag;

public record IntRange(int start, int end) {
    public IntRange {
        if (start > end) {
            throw new IllegalArgumentException("Start must be less than or equal to end.");
        }
    }

    public static IntRange second(int start, int end) {
        return new IntRange(start * 20, end * 20);
    }

    public static IntRange tick(int start, int end) {
        return new IntRange(start, end);
    }

    public boolean contains(int value) {
        return start <= value && value <= end;
    }

    public boolean before(int value) {
        return value < start;
    }

    public boolean after(int value) {
        return value > end;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Start", start);
        tag.putInt("End", end);
        return tag;
    }

    public static IntRange deserialize(CompoundTag tag) {
        return new IntRange(tag.getInt("Start"), tag.getInt("End"));
    }
}
