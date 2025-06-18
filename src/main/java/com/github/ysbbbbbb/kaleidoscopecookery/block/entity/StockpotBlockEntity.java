package com.github.ysbbbbbb.kaleidoscopecookery.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.block.StockpotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.tag.TagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.mixin.MobBucketItemAccessor;
import com.github.ysbbbbbb.kaleidoscopecookery.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.recipe.serializer.StockpotRecipeSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;

public class StockpotBlockEntity extends BlockEntity implements Container {
    public static final int PUT_SOUP_BASE = 0;
    public static final int PUT_INGREDIENT = 1;
    public static final int COOKING = 2;
    public static final int FINISHED = 3;

    public static final int MAX_TAKEOUT_COUNT = 3;

    private static final String INPUT_ENTITY_TYPE = "InputEntityType";
    private static final String RESULT = "Result";
    private static final String STATUS = "Status";
    private static final String SOUP_BASE = "SoupBase";
    private static final String CURRENT_TICK = "CurrentTick";
    private static final String TAKEOUT_COUNT = "TakeoutCount";
    private static final String COOKING_TEXTURE = "CookingTexture";
    private static final String FINISHED_TEXTURE = "FinishedTexture";

    private NonNullList<ItemStack> items = NonNullList.withSize(StockpotRecipe.RECIPES_SIZE, ItemStack.EMPTY);
    // 由于原版没有很好的记录桶内生物类型的方式，这里使用一个 Item 来记录
    private Item inputEntityType = null;
    private ItemStack result = ItemStack.EMPTY;
    private int status = PUT_SOUP_BASE;
    private Fluid soupBase = Fluids.EMPTY;
    private int currentTick = -1;
    private int takeoutCount = 0;
    private @Nullable ResourceLocation cookingTexture;
    private @Nullable ResourceLocation finishedTexture;

    // 仅用于客户端渲染缓存对象
    public Entity renderEntity = null;

    public StockpotBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.STOCKPOT_BE.get(), pPos, pBlockState);
    }

    public void clientTick() {
        if (this.renderEntity != null) {
            this.renderEntity.tickCount++;
        }
    }

    public void tick() {
        // 没有盖子时，不进行任何 tick
        if (level == null || !level.getBlockState(worldPosition).getValue(StockpotBlock.HAS_LID)) {
            return;
        }
        // 下方没有火源
        if (!level.getBlockState(worldPosition.below()).hasProperty(BlockStateProperties.LIT) ||
            !level.getBlockState(worldPosition.below()).getValue(BlockStateProperties.LIT)) {
            return;
        }

        // 如果当前状态是烹饪中，递减当前 tick
        if (status == COOKING) {
            if (currentTick > 0) {
                currentTick--;
                return;
            }
            status = FINISHED;
            currentTick = -1;
            takeoutCount = MAX_TAKEOUT_COUNT;
            soupBase = Fluids.EMPTY;
            this.items.clear();
            this.refresh();
        }
    }

    public void onLitClick(Player player) {
        if (level == null) {
            return;
        }

        BlockState blockState = level.getBlockState(worldPosition);
        Boolean hasLid = blockState.getValue(StockpotBlock.HAS_LID);
        ItemStack mainHandItem = player.getMainHandItem();

        // 第一种情况，放上盖子
        if (!hasLid && mainHandItem.is(ModItems.STOCKPOT_LID.get())) {
            // 开始检查是否处于可以煮的状态
            if (status == PUT_INGREDIENT && !this.isEmpty()) {
                level.getRecipeManager().getRecipeFor(ModRecipes.STOCKPOT_RECIPE, this, level).ifPresentOrElse(recipe -> {
                    this.result = recipe.assemble(this, level.registryAccess());
                    this.currentTick = recipe.getTime();
                    this.cookingTexture = recipe.getCookingTexture();
                    this.finishedTexture = recipe.getFinishedTexture();
                }, () -> {
                    this.result = FoodBiteRegistry.getItem(FoodBiteRegistry.SUSPICIOUS_STIR_FRY).getDefaultInstance();
                    this.currentTick = StockpotRecipeSerializer.DEFAULT_TIME;
                    this.cookingTexture = StockpotRecipeSerializer.DEFAULT_COOKING_TEXTURE;
                    this.finishedTexture = StockpotRecipeSerializer.DEFAULT_FINISHED_TEXTURE;
                });
                status = COOKING;
                this.refresh();
            }
            // 扣上盖子
            mainHandItem.shrink(1);
            this.setChanged();
            level.setBlockAndUpdate(worldPosition, blockState.setValue(StockpotBlock.HAS_LID, true));
            return;
        }

        // 第二种情况，取下盖子
        if (hasLid && mainHandItem.isEmpty()) {
            ItemStack lid = ModItems.STOCKPOT_LID.get().getDefaultInstance();
            player.setItemInHand(InteractionHand.MAIN_HAND, lid);
            this.setChanged();
            level.setBlockAndUpdate(worldPosition, blockState.setValue(StockpotBlock.HAS_LID, false));
        }
    }

    public void onBucketClick(Player player) {
        // 必须打开盖子才能放入汤底
        if (level == null || level.getBlockState(worldPosition).getValue(StockpotBlock.HAS_LID)) {
            return;
        }
        ItemStack bucket = player.getMainHandItem();
        if (!(bucket.getItem() instanceof BucketItem bucketItem)) {
            return;
        }
        // 当前状态是放入汤底
        if (status == PUT_SOUP_BASE) {
            Fluid fluid = bucketItem.getFluid();
            if (fluid == Fluids.EMPTY) {
                return;
            }
            // 如果是生物桶呢？
            if (bucket.getItem() instanceof MobBucketItemAccessor accessor && accessor.kaleidoscope$GetFishType() != null) {
                this.inputEntityType = bucketItem;
            }
            SoundEvent sound = fluid.getFluidType().getSound(player, SoundActions.BUCKET_EMPTY);
            if (sound != null) {
                level.playSound(null, worldPosition.getX(), worldPosition.getY() + 0.5, worldPosition.getZ(),
                        sound, SoundSource.BLOCKS, 1, 1);
            }
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemUtils.createFilledResult(bucket, player, new ItemStack(Items.BUCKET)));
            this.soupBase = fluid;
            this.status = PUT_INGREDIENT;
            this.refresh();
            return;
        }
        // 当前是放入食材，但是还没放
        if (status == PUT_INGREDIENT && this.isEmpty() && this.soupBase != Fluids.EMPTY && bucketItem.getFluid() == Fluids.EMPTY) {
            SoundEvent sound = this.soupBase.getFluidType().getSound(player, SoundActions.BUCKET_FILL);
            if (sound != null) {
                level.playSound(null, worldPosition.getX(), worldPosition.getY() + 0.5, worldPosition.getZ(),
                        sound, SoundSource.BLOCKS, 1, 1);
            }
            ItemStack resultBucket = new ItemStack(Objects.requireNonNullElseGet(this.inputEntityType, () -> this.soupBase.getBucket()));
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemUtils.createFilledResult(bucket, player, resultBucket));
            this.inputEntityType = null;
            this.renderEntity = null;
            this.soupBase = Fluids.EMPTY;
            this.status = PUT_SOUP_BASE;
            this.refresh();
        }
    }

    public void onIngredientClick(Player player) {
        if (level == null || level.getBlockState(worldPosition).getValue(StockpotBlock.HAS_LID)) {
            return;
        }
        if (status == PUT_INGREDIENT) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (mainHandItem.isEmpty()) {
                for (int i = this.getContainerSize() - 1; i >= 0; i--) {
                    if (!this.getItem(i).isEmpty()) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, this.getItem(i).copy());
                        level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                                ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        this.removeItem(i, 1);
                        this.refresh();
                        return;
                    }
                }
                return;
            }

            if (mainHandItem.is(TagItem.POT_INGREDIENT)) {
                // 检查是否有足够的空间放入食材
                for (int i = 0; i < this.getContainerSize(); i++) {
                    if (this.getItem(i).isEmpty()) {
                        this.setItem(i, mainHandItem.split(1));
                        this.refresh();
                        return;
                    }
                }
            }
        }
    }

    public void onBowlClick(Player player) {
        if (level == null || level.getBlockState(worldPosition).getValue(StockpotBlock.HAS_LID)) {
            return;
        }

        // 如果当前状态是烹饪完成
        if (status == FINISHED && takeoutCount > 0) {
            ItemStack bowl = player.getMainHandItem();
            if (bowl.is(Items.BOWL)) {
                // 放入碗中
                ItemStack resultCopy = this.result.copy();
                resultCopy.setCount(1);
                bowl.shrink(1);
                if (bowl.isEmpty()) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, resultCopy);
                    level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                            SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                            ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                } else {
                    ItemHandlerHelper.giveItemToPlayer(player, resultCopy);
                }
                takeoutCount--;
                if (takeoutCount <= 0) {
                    status = PUT_SOUP_BASE;
                    this.items.clear();
                    this.result = ItemStack.EMPTY;
                    this.soupBase = Fluids.EMPTY;
                    this.cookingTexture = null;
                    this.finishedTexture = null;
                }
                this.refresh();
            }
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
        if (this.inputEntityType != null) {
            tag.putString(INPUT_ENTITY_TYPE, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(this.inputEntityType)).toString());
        } else {
            tag.remove(INPUT_ENTITY_TYPE);
        }
        tag.put(RESULT, this.result.save(new CompoundTag()));
        tag.putInt(STATUS, this.status);
        tag.putString(SOUP_BASE, Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(this.soupBase)).toString());
        tag.putInt(CURRENT_TICK, this.currentTick);
        tag.putInt(TAKEOUT_COUNT, this.takeoutCount);
        if (this.cookingTexture != null) {
            tag.putString(COOKING_TEXTURE, this.cookingTexture.toString());
        }
        if (this.finishedTexture != null) {
            tag.putString(FINISHED_TEXTURE, this.finishedTexture.toString());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        if (tag.contains(INPUT_ENTITY_TYPE)) {
            this.inputEntityType = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(tag.getString(INPUT_ENTITY_TYPE)));
        } else {
            this.inputEntityType = null;
        }
        this.result = ItemStack.of(tag.getCompound(RESULT));
        this.status = tag.getInt(STATUS);
        this.soupBase = ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryParse(tag.getString(SOUP_BASE)));
        this.currentTick = tag.getInt(CURRENT_TICK);
        this.takeoutCount = tag.getInt(TAKEOUT_COUNT);
        if (tag.contains(COOKING_TEXTURE)) {
            this.cookingTexture = ResourceLocation.tryParse(tag.getString(COOKING_TEXTURE));
        } else {
            this.cookingTexture = null;
        }
        if (tag.contains(FINISHED_TEXTURE)) {
            this.finishedTexture = ResourceLocation.tryParse(tag.getString(FINISHED_TEXTURE));
        } else {
            this.finishedTexture = null;
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

    public Fluid getSoupBase() {
        return soupBase;
    }

    public ItemStack getResult() {
        return result;
    }

    public int getStatus() {
        return status;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public int getTakeoutCount() {
        return takeoutCount;
    }

    @Nullable
    public ResourceLocation getCookingTexture() {
        return cookingTexture;
    }

    @Nullable
    public ResourceLocation getFinishedTexture() {
        return finishedTexture;
    }

    @Nullable
    public EntityType<?> getInputEntityType() {
        if (this.inputEntityType instanceof MobBucketItemAccessor accessor) {
            return accessor.kaleidoscope$GetFishType();
        }
        return null;
    }
}
