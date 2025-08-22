package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

public class MillstoneFinishEvent extends Event {
    private final MillstoneBlockEntity millstone;
    private final @Nullable Mob bindEntity;

    public MillstoneFinishEvent(MillstoneBlockEntity millstone, @Nullable Mob bindEntity) {
        this.millstone = millstone;
        this.bindEntity = bindEntity;
    }

    public MillstoneBlockEntity getMillstone() {
        return millstone;
    }

    public @Nullable Mob getBindEntity() {
        return bindEntity;
    }
}
