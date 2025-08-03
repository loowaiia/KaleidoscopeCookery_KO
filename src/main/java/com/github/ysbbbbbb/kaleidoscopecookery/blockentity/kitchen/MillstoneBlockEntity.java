package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MillstoneBlockEntity extends BaseBlockEntity {
    private static final String ENTITY_ID_KEY = "EntityId";
    private static final String CACHE_ROT_KEY = "CacheRot";
    private static final String LIFT_ANGLE_KEY = "LiftAngle";

    private UUID entityId = Util.NIL_UUID;
    private @Nullable Mob bindEntity;
    // 缓存的角度，避免动画突兀的跳动变化
    private float cacheRot = 0f;
    private float liftAngle = 0f;

    public MillstoneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.MILLSTONE_BE.get(), pos, state);
    }

    public float getRotation(Level level, float partialTick) {
        // 旋转一圈的时间 (ticks)
        float speed = 9 * 20;
        float degPerTick = 360f / speed;
        float gameTime = level.getGameTime() + partialTick;
        return (this.cacheRot + gameTime * degPerTick) % 360;
    }

    public float getCacheRot() {
        return this.cacheRot;
    }

    public float getLiftAngle() {
        return this.liftAngle;
    }

    public void tick(Level level) {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (Util.NIL_UUID.equals(this.entityId)) {
            return;
        }

        // 旋转一圈的时间 (ticks)
        float rot = this.getRotation(level, 0);
        Vec3 center = Vec3.atBottomCenterOf(this.getBlockPos());
        double maxDistanceSqr = 5 * 5;
        // 服务器端检查实体是否还存在
        if (bindEntity == null) {
            // 必须距离磨盘足够近才可以（5 格）
            if (serverLevel.getEntity(entityId) instanceof Mob mob
                && mob.isAlive() && mob.distanceToSqr(center) < maxDistanceSqr
                && this.canBindEntity(mob)) {
                this.bindEntity(mob);
            } else {
                this.entityId = Util.NIL_UUID;
                this.cacheRot = 0f;
                this.liftAngle = 0f;
                this.refresh();
                return;
            }
        } else if (!bindEntity.isAlive() || bindEntity.distanceToSqr(center) >= maxDistanceSqr) {
            this.entityId = Util.NIL_UUID;
            this.bindEntity = null;
            this.cacheRot = rot;
            this.liftAngle = 0f;
            this.refresh();
            return;
        }

        // 如果实体存在，检查是否需要更新位置
        Vec3 pos = new Vec3(0, 0, 2).yRot(rot * Mth.DEG_TO_RAD).add(center);
        this.bindEntity.moveTo(pos.x, pos.y, pos.z, -rot - 90, 0);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putUUID(ENTITY_ID_KEY, entityId);
        tag.putFloat(CACHE_ROT_KEY, cacheRot);
        tag.putFloat(LIFT_ANGLE_KEY, liftAngle);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.entityId = tag.getUUID(ENTITY_ID_KEY);
        this.cacheRot = tag.getFloat(CACHE_ROT_KEY);
        this.liftAngle = tag.getFloat(LIFT_ANGLE_KEY);
    }

    public boolean canBindEntity(Mob mob) {
        if (!mob.getType().is(TagMod.MILLSTONE_BINDABLE)) {
            return false;
        }
        if (mob instanceof OwnableEntity ownable) {
            return ownable.getOwnerUUID() != null;
        }
        return true;
    }

    public void bindEntity(Mob mob) {
        if (this.level == null || this.level.isClientSide) {
            // 仅在服务器端绑定实体
            return;
        }
        if (!mob.isAlive()) {
            return;
        }
        this.entityId = mob.getUUID();
        this.bindEntity = mob;
        // 缓存角度纠正
        float rot = this.getRotation(this.level, 0);
        this.cacheRot = this.cacheRot - (rot - this.cacheRot);
        // 依据实体的碰撞箱，决定抬起角度
        float height = mob.getEyeHeight() - 0.125f;
        this.liftAngle = (float) Math.atan2(height - 0.875f, 2);
        this.refresh();
    }

    public boolean hasEntity() {
        return !Util.NIL_UUID.equals(this.entityId);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-2, 0, -2), worldPosition.offset(2, 1, 2));
    }
}
