package com.github.ysbbbbbb.kaleidoscopecookery.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.tag.TagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.util.IntRange;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Optional;

import static com.github.ysbbbbbb.kaleidoscopecookery.block.PotBlock.HAS_OIL;
import static com.github.ysbbbbbb.kaleidoscopecookery.block.PotBlock.SHOW_OIL;

public class PotBlockEntity extends BlockEntity implements Container {
    public static final IntRange INGREDIENT_TICK = IntRange.second(0, 30);

    public static final int PUT_INGREDIENT = 0;
    public static final int COOKING = 1;
    public static final int FINISHED = 2;

    private static final String SEED = "Seed";
    private static final String STATUS = "Status";
    private static final String CURRENT_TICK = "CurrentTick";
    private static final String STIR_FRY_COUNT = "StirFryCount";
    private static final String NEED_BOWL = "NeedBowl";
    private static final String RECIPE_ID = "RecipeId";
    private static final String RESULT_TICK = "ResultTick";
    private static final String RESULT_STACK = "ResultStack";
    private static final String BURNT_LEVEL = "BurntLevel";

    private NonNullList<ItemStack> items = NonNullList.withSize(PotRecipe.RECIPES_SIZE, ItemStack.EMPTY);
    private ItemStack result = ItemStack.EMPTY;
    private int status = PUT_INGREDIENT;
    private int currentTick = 0;
    private @Nullable ResourceLocation recipeId;
    private @Nullable IntRange getResultTick;
    private int stirFryCount = 0;
    private boolean needBowl = false;
    private int burntLevel = 0;

    private long seed;
    public StirFryAnimationData animationData = new StirFryAnimationData();

    public PotBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.POT_BE.get(), pPos, pBlockState);
        this.seed = System.currentTimeMillis();
    }

    public void tick() {
        if (this.level == null) {
            return;
        }
        RandomSource random = level.random;

        // 下方没有点燃属性？不更新
        Optional<Boolean> value = level.getBlockState(worldPosition.below()).getOptionalValue(BlockStateProperties.LIT);
        if (value.isEmpty() || !value.get()) {
            return;
        }

        this.currentTick++;
        // 每 5tick 刷新一次
        if (this.currentTick % 5 == 0) {
            this.refresh();
        }

        if (this.currentTick % 10 == 0) {
            // 模拟油炸声音
            level.playSound(null, this.worldPosition, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1f, 1f);
        }

        // 放素材阶段
        if (this.status == PUT_INGREDIENT) {
            // 有菜，且炒菜时间超过了
            if (INGREDIENT_TICK.after(currentTick)) {
                onPreparationTimeout(this.level);
            }
            if (this.currentTick % 10 == 0 && this.level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.POOF,
                        worldPosition.getX() + 0.5 + random.nextDouble() / 5 * (random.nextBoolean() ? 1 : -1),
                        worldPosition.getY() + 0.25 + random.nextDouble() / 3,
                        worldPosition.getZ() + 0.5 + random.nextDouble() / 5 * (random.nextBoolean() ? 1 : -1),
                        1, 0, 0, 0, 0.05);
            }
            return;
        }

        if (this.getResultTick == null) {
            return;
        }

        // 炒菜阶段
        if (this.status == COOKING) {
            if (this.currentTick > this.getResultTick.start()) {
                level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1F,
                        (random.nextFloat() - random.nextFloat()) * 0.8F);
                this.result = getResult(this.level);
                this.status = FINISHED;
                this.setChanged();
                if (level != null) {
                    BlockState state = level.getBlockState(worldPosition);
                    level.setBlockAndUpdate(worldPosition, state.setValue(SHOW_OIL, false));
                }
            }
        }

        // 出结果阶段
        if (this.status == FINISHED) {
            int time = this.currentTick - this.getResultTick.start();
            // 前 20 秒白烟
            if (time < 20 * 20) {
                if (this.currentTick % 10 == 0 && this.level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.POOF,
                            worldPosition.getX() + 0.5,
                            worldPosition.getY() + 0.25 + random.nextDouble() / 2,
                            worldPosition.getZ() + 0.5,
                            1, 0, 0, 0, 0);
                }
            } else {
                // 如果开始超时，那么菜慢慢变黑
                this.burntLevel = (time - 20 * 20) / 25;
                this.burntLevel = Mth.clamp(this.burntLevel, 0, 16);

                // 依据过头情况，粒子数量逐渐增加
                if (this.currentTick % 2 == 0 && this.level instanceof ServerLevel serverLevel && this.burntLevel >= 2) {
                    serverLevel.sendParticles(ParticleTypes.SMOKE,
                            worldPosition.getX() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                            worldPosition.getY() + 0.25 + random.nextDouble() / 3,
                            worldPosition.getZ() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                            this.burntLevel / 2, 0, 0, 0, 0.05);
                }
            }

            // 如果超时了，那么随机产生木炭
            if (this.getResultTick.after(this.currentTick) && this.level != null) {
                onFinishedTimeout(level);
            }
        }
    }

    private void onFinishedTimeout(Level level) {
        this.reset();
        RandomSource random = level.random;
        level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1F,
                (random.nextFloat() - random.nextFloat()) * 0.8F);
        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    worldPosition.getX() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    worldPosition.getY() + 0.25 + random.nextDouble() / 3,
                    worldPosition.getZ() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    8, 0, 0, 0, 0.05);

            int count = 1 + random.nextInt(3);
            ItemEntity itemEntity = new ItemEntity(serverLevel,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.25,
                    worldPosition.getZ() + 0.5,
                    new ItemStack(Items.CHARCOAL, count));
            itemEntity.setDeltaMovement(0, 0.2, 0);
            itemEntity.setPickUpDelay(10);
            level.addFreshEntity(itemEntity);
        }
    }

    private void onPreparationTimeout(Level level) {
        if (!this.isEmpty()) {
            // 自动切炒菜阶段
            this.startCooking(level);
            return;
        }
        // 清空
        this.reset();
        RandomSource random = level.random;
        level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1F,
                (random.nextFloat() - random.nextFloat()) * 0.8F);
        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.POOF,
                    worldPosition.getX() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    worldPosition.getY() + 0.25 + random.nextDouble() / 3,
                    worldPosition.getZ() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    8, 0, 0, 0, 0.05);
        }
    }

    public void onShovelHit(Level level, Player player, ItemStack shovel) {
        if (!player.level().isClientSide) {
            this.seed = System.currentTimeMillis();
            this.refresh();
        }

        // 每次翻炒给点粒子效果
        if (this.level instanceof ServerLevel serverLevel) {
            RandomSource random = serverLevel.random;
            serverLevel.sendParticles(ParticleTypes.POOF,
                    worldPosition.getX() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    worldPosition.getY() + 0.25 + random.nextDouble() / 3,
                    worldPosition.getZ() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    1, 0, 0, 0, 0.05);
        }

        // 起锅烧油，放入食材阶段
        if (this.status == PUT_INGREDIENT) {
            if (!this.isEmpty()) {
                this.startCooking(level);
            }
        }

        // 炒菜阶段
        if (this.status == COOKING) {
            this.stirFryCount++;
        }
    }

    private void startCooking(Level level) {
        level.getRecipeManager().getRecipeFor(ModRecipes.POT_RECIPE, this, level).ifPresentOrElse(recipe -> {
            // 如果合成表符合，那么进入炒菜阶段
            this.recipeId = recipe.getId();
            this.needBowl = recipe.isNeedBowl();
            int time = recipe.getTime() / 20;
            // 最后给 20 秒时间来取出成品，20 秒取出黑暗料理
            this.getResultTick = IntRange.second(time, time + 40);
        }, () -> {
            // 不符合，进入迷之炒菜阶段
            this.recipeId = null;
            this.getResultTick = IntRange.second(10, 50);
        });
        this.status = COOKING;
        this.currentTick = 0;
        this.refresh();
    }

    private ItemStack getResult(Level level) {
        ItemStack result = ModItems.SUSPICIOUS_STIR_FRY.get().getDefaultInstance();
        if (this.recipeId != null) {
            var recipe = level.getRecipeManager().getRecipeFor(ModRecipes.POT_RECIPE, this, level, this.recipeId);
            if (recipe.isPresent()) {
                PotRecipe potRecipe = recipe.get().getSecond();
                if (this.stirFryCount >= potRecipe.getStirFryCount()) {
                    result = potRecipe.getResult().copy();
                }
            }
        }
        if (result.is(ModItems.SUSPICIOUS_STIR_FRY.get())) {
            this.needBowl = true;
        }
        return result;
    }

    public void takeOut(Player player) {
        if (this.status != FINISHED) {
            return;
        }
        if (this.getResultTick == null) {
            return;
        }

        // 超时取出的是黑暗料理
        ItemStack finallyResult = this.getResult(level);
        int time = this.currentTick - this.getResultTick.start();
        if (time > 20 * 20) {
            finallyResult = new ItemStack(ModItems.DARK_CUISINE.get());
        }

        if (this.needBowl) {
            if (player.getMainHandItem().is(Items.BOWL)) {
                ItemHandlerHelper.giveItemToPlayer(player, finallyResult);
                player.getMainHandItem().shrink(1);
                this.reset();
            } else {
                player.hurt(player.level().damageSources().inFire(), 1);
                if (player instanceof ServerPlayer serverPlayer) {
                    MutableComponent component = Component.translatable("tip.kaleidoscope_cookery.pot.need_bowl");
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(component));
                }
            }
        } else {
            if (player.getMainHandItem().is(ModItems.KITCHEN_SHOVEL.get())) {
                if (player.isSecondaryUseActive()) {
                    ItemHandlerHelper.giveItemToPlayer(player, finallyResult);
                    this.reset();
                }
            } else {
                player.hurt(player.level().damageSources().inFire(), 1);
                if (player instanceof ServerPlayer serverPlayer) {
                    MutableComponent component = Component.translatable("tip.kaleidoscope_cookery.pot.need_kitchen_shovel");
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(component));
                }
            }
        }
    }

    public void addIngredient(ItemStack itemStack, Player player) {
        if (this.status != PUT_INGREDIENT) {
            return;
        }
        if (itemStack.isEmpty()) {
            for (int i = 0; i < this.items.size(); i++) {
                ItemStack stack = this.items.get(i);
                if (!stack.isEmpty()) {
                    this.items.set(i, ItemStack.EMPTY);
                    ItemHandlerHelper.giveItemToPlayer(player, stack);
                    player.hurt(player.level().damageSources().inFire(), 1);
                    return;
                }
            }
        }
        // 只允许食物和特定 tag 的东西放入
        if (!itemStack.isEdible() && !itemStack.is(TagItem.POT_INGREDIENT)) {
            return;
        }
        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack item = this.getItem(i);
            if (item.isEmpty()) {
                this.setItem(i, itemStack.copyWithCount(1));
                itemStack.shrink(1);
                player.level().playSound(null, this.worldPosition, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 1.0F, 0.5F);
                return;
            }
        }
    }

    public void reset() {
        this.currentTick = 0;
        this.recipeId = null;
        this.getResultTick = null;
        this.result = ItemStack.EMPTY;
        this.stirFryCount = 0;
        this.needBowl = false;
        this.burntLevel = 0;
        this.items.clear();
        this.status = PUT_INGREDIENT;
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.setBlockAndUpdate(worldPosition, state.setValue(HAS_OIL, false).setValue(SHOW_OIL, false));
        }
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
        ContainerHelper.saveAllItems(tag, this.items);
        tag.putLong(SEED, this.seed);
        tag.putInt(STATUS, this.status);
        tag.putInt(CURRENT_TICK, this.currentTick);
        tag.putInt(STIR_FRY_COUNT, this.stirFryCount);
        tag.putBoolean(NEED_BOWL, this.needBowl);
        tag.putInt(BURNT_LEVEL, this.burntLevel);
        if (this.recipeId != null) {
            tag.putString(RECIPE_ID, this.recipeId.toString());
        }
        if (this.getResultTick != null) {
            tag.put(RESULT_TICK, this.getResultTick.serialize());
        }
        tag.put(RESULT_STACK, this.result.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.seed = tag.getLong(SEED);
        this.status = tag.getInt(STATUS);
        this.currentTick = tag.getInt(CURRENT_TICK);
        this.stirFryCount = tag.getInt(STIR_FRY_COUNT);
        this.needBowl = tag.getBoolean(NEED_BOWL);
        this.burntLevel = tag.getInt(BURNT_LEVEL);
        if (tag.contains(RECIPE_ID)) {
            this.recipeId = new ResourceLocation(tag.getString(RECIPE_ID));
        }
        if (tag.contains(RESULT_TICK)) {
            this.getResultTick = IntRange.deserialize(tag.getCompound(RESULT_TICK));
        }
        this.result = ItemStack.of(tag.getCompound(RESULT_STACK));
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

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return (0 <= index && index < this.items.size()) ? this.items.get(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(this.items, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.items, pSlot);
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        if (0 <= pIndex && pIndex < this.items.size()) {
            this.items.set(pIndex, pStack);
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public long getSeed() {
        return seed;
    }

    public int getStatus() {
        return status;
    }

    @Nullable
    public IntRange getGetResultTick() {
        return getResultTick;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public int getStirFryCount() {
        return stirFryCount;
    }

    public int getBurntLevel() {
        return burntLevel;
    }

    public boolean isNeedBowl() {
        return needBowl;
    }

    public ItemStack getResult() {
        return result;
    }

    public static class StirFryAnimationData {
        public long preSeed = -1L;
        public long timestamp = -1L;
        public float[] randomHeights = new float[]{};
    }
}
