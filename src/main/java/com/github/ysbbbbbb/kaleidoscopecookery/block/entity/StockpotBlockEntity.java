package com.github.ysbbbbbb.kaleidoscopecookery.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.block.StockpotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.client.particle.StockpotParticleOptions;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.tag.TagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.init.*;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
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

    public static final int MAX_TAKEOUT_COUNT = 9;

    private static final String INPUT_ENTITY_TYPE = "InputEntityType";
    private static final String RESULT = "Result";
    private static final String STATUS = "Status";
    private static final String SOUP_BASE = "SoupBase";
    private static final String CURRENT_TICK = "CurrentTick";
    private static final String TAKEOUT_COUNT = "TakeoutCount";
    private static final String COOKING_TEXTURE = "CookingTexture";
    private static final String FINISHED_TEXTURE = "FinishedTexture";
    private static final String SOUP_BASE_BUBBLE_COLOR = "SoupBaseBubbleColor";
    private static final String COOKING_BUBBLE_COLOR = "CookingBubbleColor";
    private static final String FINISHED_BUBBLE_COLOR = "FinishedBubbleColor";

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
    private int soupBaseBubbleColor = 0xFFFFFF;
    private int cookingBubbleColor = StockpotRecipeSerializer.DEFAULT_COOKING_BUBBLE_COLOR;
    private int finishedBubbleColor = StockpotRecipeSerializer.DEFAULT_FINISHED_BUBBLE_COLOR;

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
        if (level == null) {
            return;
        }
        // 下方没有火源
        BlockState belowState = level.getBlockState(worldPosition.below());
        if (!belowState.hasProperty(BlockStateProperties.LIT) || !belowState.getValue(BlockStateProperties.LIT)) {
            return;
        }
        // 盖子
        Boolean hasLid = level.getBlockState(worldPosition).getValue(StockpotBlock.HAS_LID);

        // 音效播放
        if (status != PUT_SOUP_BASE && level.getGameTime() % 15 == 0) {
            float volume = hasLid ? 0.075f : 0.2f;
            float pitch = hasLid ? 0.1f + level.random.nextFloat() * 0.05f : 1f + level.random.nextFloat() * 0.1f;
            level.playSound(null,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                    ModSounds.BLOCK_STOCKPOT.get(), SoundSource.BLOCKS, volume, pitch);
        }

        // 没有盖子时，不进行任何 tick，只生成粒子
        if (!hasLid) {
            if (status != PUT_SOUP_BASE && level instanceof ServerLevel serverLevel && level.random.nextFloat() < 0.25F) {
                int color = status == COOKING ? this.cookingBubbleColor : status == FINISHED ? this.finishedBubbleColor : this.soupBaseBubbleColor;
                serverLevel.sendParticles(new StockpotParticleOptions(Vec3.fromRGB24(color).toVector3f(), 1f),
                        worldPosition.getX() + 0.25 + (level.random.nextFloat() * 0.5F),
                        worldPosition.getY() + 0.375,
                        worldPosition.getZ() + 0.25 + (level.random.nextFloat() * 0.5F),
                        2,
                        (level.random.nextFloat() - 0.5) * 0.1F,
                        0,
                        (level.random.nextFloat() - 0.5) * 0.1F,
                        0.01f
                );
            }
            return;
        }
        // 有盖子时，生成白色粒子
        if (status != PUT_SOUP_BASE && level instanceof ServerLevel serverLevel && level.random.nextFloat() < 0.05F) {
            RandomSource random = serverLevel.random;
            serverLevel.sendParticles(ModParticles.COOKING.get(),
                    worldPosition.getX() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    worldPosition.getY() + 0.375 + random.nextDouble() / 3,
                    worldPosition.getZ() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    1, 0, 0, 0, 0.05);
        }

        // 如果当前状态是放入素材，且素材不为空
        // 因为 isEmpty() 可能耗时，所以每隔 5 tick 检查一次
        if (status == PUT_INGREDIENT && this.level.getGameTime() % 5 == 0 && !this.isEmpty()) {
            this.setRecipe(level);
            status = COOKING;
            this.refresh();
            return;
        }

        // 如果当前状态是烹饪中，递减当前 tick
        if (status == COOKING) {
            if (currentTick > 0) {
                currentTick--;
                return;
            }
            status = FINISHED;
            inputEntityType = null;
            currentTick = -1;
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
            mainHandItem.shrink(1);
            this.setChanged();
            level.setBlockAndUpdate(worldPosition, blockState.setValue(StockpotBlock.HAS_LID, true));
            player.playSound(SoundEvents.LANTERN_PLACE, 0.5F, 0.5F);
            return;
        }

        // 第二种情况，取下盖子
        if (hasLid && mainHandItem.isEmpty()) {
            ItemStack lid = ModItems.STOCKPOT_LID.get().getDefaultInstance();
            player.setItemInHand(InteractionHand.MAIN_HAND, lid);
            this.setChanged();
            level.setBlockAndUpdate(worldPosition, blockState.setValue(StockpotBlock.HAS_LID, false));
            player.playSound(SoundEvents.LANTERN_BREAK, 0.5F, 0.5F);
        }
    }

    private void setRecipe(Level levelIn) {
        levelIn.getRecipeManager().getRecipeFor(ModRecipes.STOCKPOT_RECIPE, this, levelIn).ifPresentOrElse(recipe -> {
            this.result = recipe.assemble(this, levelIn.registryAccess());
            this.currentTick = recipe.getTime();
            this.cookingTexture = recipe.getCookingTexture();
            this.finishedTexture = recipe.getFinishedTexture();
            this.cookingBubbleColor = recipe.getCookingBubbleColor();
            this.finishedBubbleColor = recipe.getFinishedBubbleColor();
            this.takeoutCount = Math.min(this.result.getCount(), MAX_TAKEOUT_COUNT);
        }, () -> {
            this.result = Items.SUSPICIOUS_STEW.getDefaultInstance();
            this.currentTick = StockpotRecipeSerializer.DEFAULT_TIME;
            this.cookingTexture = StockpotRecipeSerializer.DEFAULT_COOKING_TEXTURE;
            this.finishedTexture = StockpotRecipeSerializer.DEFAULT_FINISHED_TEXTURE;
            this.cookingBubbleColor = StockpotRecipeSerializer.DEFAULT_COOKING_BUBBLE_COLOR;
            this.finishedBubbleColor = StockpotRecipeSerializer.DEFAULT_FINISHED_BUBBLE_COLOR;
            this.takeoutCount = 1;
        });
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
            // 仅判断水和熔岩
            if (this.soupBase.is(FluidTags.WATER)) {
                this.soupBaseBubbleColor = 0x3F76E4;
            } else if (this.soupBase.is(FluidTags.LAVA)) {
                this.soupBaseBubbleColor = 0xff7518;
            } else {
                // FIXME 该如何自定义？
                this.soupBaseBubbleColor = 0xFFFFFF;
            }
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

            if (mainHandItem.isEdible() || mainHandItem.is(TagItem.POT_INGREDIENT)) {
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
                    this.inputEntityType = null;
                    this.items.clear();
                    this.result = ItemStack.EMPTY;
                    this.soupBase = Fluids.EMPTY;
                    this.cookingTexture = null;
                    this.finishedTexture = null;
                    this.soupBaseBubbleColor = 0xffffff;
                    this.cookingBubbleColor = StockpotRecipeSerializer.DEFAULT_COOKING_BUBBLE_COLOR;
                    this.finishedBubbleColor = StockpotRecipeSerializer.DEFAULT_FINISHED_BUBBLE_COLOR;
                    this.currentTick = -1;
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
        tag.putInt(SOUP_BASE_BUBBLE_COLOR, this.soupBaseBubbleColor);
        tag.putInt(COOKING_BUBBLE_COLOR, this.cookingBubbleColor);
        tag.putInt(FINISHED_BUBBLE_COLOR, this.finishedBubbleColor);
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
        this.soupBaseBubbleColor = tag.getInt(SOUP_BASE_BUBBLE_COLOR);
        this.cookingBubbleColor = tag.getInt(COOKING_BUBBLE_COLOR);
        this.finishedBubbleColor = tag.getInt(FINISHED_BUBBLE_COLOR);
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
