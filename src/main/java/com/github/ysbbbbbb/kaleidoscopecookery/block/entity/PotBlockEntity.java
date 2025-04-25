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
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;
import java.util.Optional;

import static com.github.ysbbbbbb.kaleidoscopecookery.block.PotBlock.HAS_OIL;

public class PotBlockEntity extends BlockEntity implements Container {
    public static final IntRange INGREDIENT_TICK = IntRange.second(1, 6);

    public static final int HEAT_THE_OIL = 0;
    public static final int COOKING = 1;
    public static final int FAIL = 2;

    private static final String SEED = "Seed";
    private static final String STATUS = "Status";
    private static final String CURRENT_TICK = "CurrentTick";
    private static final String STIR_FRY_COUNT = "StirFryCount";
    private static final String RECIPE_ID = "RecipeId";
    private static final String COOKING_TICK = "CookingTick";
    private static final String BURNT_LEVEL = "BurntLevel";

    private NonNullList<ItemStack> items = NonNullList.withSize(PotRecipe.RECIPES_SIZE, ItemStack.EMPTY);
    private int status = HEAT_THE_OIL;
    private int currentTick = 0;
    private @Nullable ResourceLocation recipeId;
    private @Nullable IntRange cookingTick;
    private int stirFryCount = 0;
    private int burntLevel = 0;

    private long seed;

    public PotBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.POT_BE.get(), pPos, pBlockState);
        this.seed = System.currentTimeMillis();
    }

    public void tick() {
        if (this.level == null) {
            return;
        }

        // 下方没有点燃属性？不更新
        Optional<Boolean> value = level.getBlockState(worldPosition.below()).getOptionalValue(BlockStateProperties.LIT);
        if (value.isEmpty() || !value.get()) {
            return;
        }

        // 失败阶段不刷新
        if (this.status != FAIL) {
            this.currentTick++;
            // 每 2tick 刷新一次
            if (this.currentTick % 2 == 1) {
                this.refresh();
            }
        }

        // 放油阶段
        if (this.status == HEAT_THE_OIL) {
            // 有菜，且炒菜时间超过了
            if (INGREDIENT_TICK.after(currentTick)) {
                onPreparationTimeout(this.level);
            }
            return;
        }

        // 炒菜阶段
        if (this.status == COOKING) {
            if (this.cookingTick == null) {
                return;
            }

            // 如果开始超时，那么菜慢慢变黑
            if (this.currentTick > this.cookingTick.start() + 20) {
                this.burntLevel = (this.currentTick - this.cookingTick.start() - 20) / 5;
                this.burntLevel = Mth.clamp(this.burntLevel, 0, 16);
            }

            // 如果超时了，那么变失败状态
            if (this.cookingTick.after(this.currentTick)) {
                this.status = FAIL;
            }
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
        for (int i = 0; i < 10; i++) {
            level.addParticle(ParticleTypes.SMOKE,
                    worldPosition.getX() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    worldPosition.getY() + 0.25 + random.nextDouble() / 3,
                    worldPosition.getZ() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    0, 0.05, 0);
        }
    }

    public void onShovelHit(Level level, Player player, ItemStack shovel) {
        this.seed = System.currentTimeMillis();

        // 起锅烧油，放入食材阶段
        if (this.status == HEAT_THE_OIL) {
            // 有菜，且炒菜时间符合
            if (INGREDIENT_TICK.contains(currentTick) && !this.isEmpty()) {
                this.startCooking(level);
            }
        }

        // 炒菜阶段
        if (this.status == COOKING) {
            this.stirFryCount++;
            this.getResult(level, player);
        }

        if (this.status == FAIL) {
            this.getResult(level, player);
        }
    }

    private void startCooking(Level level) {
        level.getRecipeManager().getRecipeFor(ModRecipes.POT_RECIPE, this, level).ifPresentOrElse(recipe -> {
            // 如果合成表符合，那么进入炒菜阶段
            this.recipeId = recipe.getId();
            int time = recipe.getTime() / 20;
            // -1 到 +2 秒内是炒菜时间
            this.cookingTick = IntRange.second(time - 2, time + 3);
        }, () -> {
            // 不符合，进入迷之炒菜阶段
            this.recipeId = null;
            this.cookingTick = IntRange.second(10, 15);
        });
        this.status = COOKING;
        this.currentTick = 0;
        this.refresh();
    }

    public void getResult(Level level, Player player) {
        if (this.status == FAIL) {
            player.addItem(ModItems.DARK_CUISINE.get().getDefaultInstance());
            this.reset();
            return;
        }

        if (this.status == COOKING && this.cookingTick != null && this.cookingTick.contains(this.currentTick)) {
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
            player.addItem(result);
            this.reset();
        }
    }

    public void addIngredient(ItemStack itemStack) {
        if (this.status != HEAT_THE_OIL) {
            return;
        }
        // 不在放入食材时间内，不允许放入
        if (!INGREDIENT_TICK.contains(currentTick)) {
            return;
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
                return;
            }
        }
    }

    public void reset() {
        this.currentTick = 0;
        this.recipeId = null;
        this.cookingTick = null;
        this.stirFryCount = 0;
        this.burntLevel = 0;
        this.items.clear();
        this.status = HEAT_THE_OIL;
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.setBlockAndUpdate(worldPosition, state.setValue(HAS_OIL, false));
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
        tag.putInt(BURNT_LEVEL, this.burntLevel);
        if (this.recipeId != null) {
            tag.putString(RECIPE_ID, this.recipeId.toString());
        }
        if (this.cookingTick != null) {
            tag.put(COOKING_TICK, this.cookingTick.serialize());
        }
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
        this.burntLevel = tag.getInt(BURNT_LEVEL);
        if (tag.contains(RECIPE_ID)) {
            this.recipeId = new ResourceLocation(tag.getString(RECIPE_ID));
        }
        if (tag.contains(COOKING_TICK)) {
            this.cookingTick = IntRange.deserialize(tag.getCompound(COOKING_TICK));
        }
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
    public IntRange getCookingTick() {
        return cookingTick;
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
}
