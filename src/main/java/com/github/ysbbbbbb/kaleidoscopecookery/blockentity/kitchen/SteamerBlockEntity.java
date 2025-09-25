package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ISteamer;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.SteamerBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.SteamerRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModParticles;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;

public class SteamerBlockEntity extends BaseBlockEntity implements ISteamer {
    // 最大火力等级为 4，往上继续叠蒸笼，火力等级逐步降低
    public static final int MAX_LIT_LEVEL = 4;
    public static final String COOKING_TIMES_TAG = "CookingTimes";
    public static final String COOKING_TOTAL_TIMES_TAG = "CookingTotalTimes";

    // 合成表
    private final RecipeManager.CachedCheck<Container, SteamerRecipe> quickCheck = RecipeManager.createCheck(ModRecipes.STEAMER_RECIPE);

    // 0-3 是下层蒸笼，4-7 是上层蒸笼
    private final NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);
    private final int[] cookingProgress = new int[8];
    private final int[] cookingTime = new int[8];

    private int litLevel = 0;

    public SteamerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.STEAMER_BE.get(), pos, state);
    }

    public void tick(Level level) {
        // 蒸笼每火力每 5 tick 更新一次
        if (level.getGameTime() % 5 == 0) {
            updateLitLevel(level);
        }

        if (this.litLevel > 0) {
            cookingTick(level, worldPosition, getBlockState(), this);
        } else {
            cooldownTick(level, worldPosition, getBlockState(), this);
        }
    }

    @Override
    public void updateLitLevel(Level level) {
        BlockPos pos = this.getBlockPos();
        if (this.hasHeatSource(level)) {
            this.litLevel = MAX_LIT_LEVEL;
        } else if (level.getBlockEntity(pos.below()) instanceof SteamerBlockEntity steamer) {
            this.litLevel = Math.max(steamer.litLevel - 1, 0);
        }
    }

    @Override
    public boolean hasHeatSource(Level level) {
        BlockState belowState = level.getBlockState(worldPosition.below());
        if (belowState.hasProperty(BlockStateProperties.LIT) && belowState.getValue(BlockStateProperties.LIT)) {
            return true;
        }
        return belowState.is(TagMod.HEAT_SOURCE_BLOCKS_WITHOUT_LIT);
    }

    private void cookingTick(Level level, BlockPos pos, BlockState state, SteamerBlockEntity steamer) {
        // 先检查上面是否是蒸笼
        BlockState aboveState = level.getBlockState(pos.above());
        boolean aboveIsSteamer = aboveState.is(ModBlocks.STEAMER.get());
        if (!aboveIsSteamer) {
            // 上面不是蒸笼，释放蒸汽粒子
            this.makeParticles(level, pos);
            // 既没有上层蒸笼，也没有盖子，不能蒸
            if (!state.getValue(SteamerBlock.HAS_LID)) {
                return;
            }
        }

        // 开始蒸
        boolean hasCooking = false;
        for (int i = 0; i < steamer.items.size(); i++) {
            ItemStack stack = steamer.items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            hasCooking = true;
            int progress = steamer.cookingProgress[i]++;
            if (progress < steamer.cookingTime[i]) {
                continue;
            }
            Container container = new SimpleContainer(stack);
            ItemStack resultStack = steamer.quickCheck.getRecipeFor(container, level)
                    .map(r -> r.assemble(container, level.registryAccess()))
                    .orElse(stack);
            if (!resultStack.isEmpty()) {
                steamer.items.set(i, resultStack);
                // 设置为 -1 代表已经蒸熟
                steamer.cookingTime[i] = -1;
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            }
        }
        if (hasCooking) {
            setChanged(level, pos, state);
        }
    }

    private void cooldownTick(Level level, BlockPos pos, BlockState state, SteamerBlockEntity steamer) {
        boolean hasCooking = false;

        for (int i = 0; i < steamer.items.size(); i++) {
            if (steamer.cookingProgress[i] > 0) {
                hasCooking = true;
                steamer.cookingProgress[i] = Mth.clamp(steamer.cookingProgress[i] - 2, 0, steamer.cookingTime[i]);
            }
        }

        if (hasCooking) {
            setChanged(level, pos, state);
        }
    }

    public void makeParticles(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel && level.random.nextFloat() < 0.1F) {
            RandomSource random = serverLevel.random;
            boolean half = this.getBlockState().getValue(SteamerBlock.HALF);
            double yOffset = half ? 0.5 : 1;
            serverLevel.sendParticles(ModParticles.COOKING.get(),
                    pos.getX() + 0.5 + random.nextDouble() / 2 * (random.nextBoolean() ? 1 : -1),
                    pos.getY() + yOffset + random.nextDouble() / 2,
                    pos.getZ() + 0.5 + random.nextDouble() / 2 * (random.nextBoolean() ? 1 : -1),
                    1, 0, 0, 0, 0.05);
        }
    }

    public Optional<SteamerRecipe> getSteamerRecipe(Level level, ItemStack stack) {
        if (this.items.stream().noneMatch(ItemStack::isEmpty)) {
            return Optional.empty();
        }
        return this.quickCheck.getRecipeFor(new SimpleContainer(stack), level);
    }

    @Override
    public boolean placeFood(Level level, LivingEntity user, ItemStack food) {
        Optional<SteamerRecipe> steamerRecipe = getSteamerRecipe(level, food);
        if (steamerRecipe.isEmpty()) {
            return false;
        }
        int cookTime = steamerRecipe.get().getCookTick();
        if (cookTime <= 0) {
            return false;
        }
        boolean half = this.getBlockState().getValue(SteamerBlock.HALF);
        int maxCount = half ? 4 : 8;
        for (int i = 0; i < maxCount; ++i) {
            ItemStack itemstack = this.items.get(i);
            if (itemstack.isEmpty()) {
                this.cookingTime[i] = cookTime;
                this.cookingProgress[i] = 0;
                this.items.set(i, food.split(1));
                this.refresh();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean takeFood(Level level, LivingEntity user) {
        for (int i = this.items.size() - 1; i >= 0; i--) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            ItemUtils.getItemToLivingEntity(user, stack);
            this.items.set(i, ItemStack.EMPTY);
            this.cookingTime[i] = 0;
            this.cookingProgress[i] = 0;
            this.refresh();
            return true;
        }
        return false;
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items);
        if (tag.contains(COOKING_TIMES_TAG, 11)) {
            int[] times = tag.getIntArray(COOKING_TIMES_TAG);
            int length = Math.min(this.cookingTime.length, times.length);
            System.arraycopy(times, 0, this.cookingProgress, 0, length);
        }
        if (tag.contains(COOKING_TOTAL_TIMES_TAG, 11)) {
            int[] times = tag.getIntArray(COOKING_TOTAL_TIMES_TAG);
            int length = Math.min(this.cookingTime.length, times.length);
            System.arraycopy(times, 0, this.cookingTime, 0, length);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items, true);
        tag.putIntArray(COOKING_TIMES_TAG, this.cookingProgress);
        tag.putIntArray(COOKING_TOTAL_TIMES_TAG, this.cookingTime);
    }

    public int[] getCookingProgress() {
        return cookingProgress;
    }

    public int[] getCookingTime() {
        return cookingTime;
    }
}
