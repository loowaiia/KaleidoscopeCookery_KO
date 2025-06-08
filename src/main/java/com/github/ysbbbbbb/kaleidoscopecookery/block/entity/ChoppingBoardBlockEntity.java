package com.github.ysbbbbbb.kaleidoscopecookery.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.tag.TagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.recipe.ChoppingBoardRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Optional;

public class ChoppingBoardBlockEntity extends BlockEntity {
    private static final String MODEL_ID = "ModelId";
    private static final String CURRENT_CUT_STACK = "CurrentCutStack";
    private static final String RESULT_ITEM = "ResultItem";
    private static final String MAX_CUT_COUNT = "MaxCutCount";
    private static final String CURRENT_CUT_COUNT = "CurrentCutCount";

    /**
     * 仅用于客户端渲染
     */
    public @Nullable ResourceLocation[] cacheModels = null;
    public @Nullable ResourceLocation previousModel = null;

    private @Nullable ResourceLocation modelId = null;
    private int maxCutCount = 0;
    private int currentCutCount = 0;
    private ItemStack currentCutStack = ItemStack.EMPTY;
    private ItemStack result = ItemStack.EMPTY;

    public ChoppingBoardBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.CHOPPING_BOARD_BE.get(), pos, blockState);
    }

    public boolean onPutOn(ItemStack stack) {
        if (this.level == null) {
            return false;
        }
        if (!this.result.isEmpty()) {
            return false;
        }
        SimpleContainer container = new SimpleContainer(stack);
        Optional<ChoppingBoardRecipe> recipeOptional = this.level.getRecipeManager().getRecipeFor(ModRecipes.CHOPPING_BOARD_RECIPE, container, this.level);
        if (recipeOptional.isPresent()) {
            ChoppingBoardRecipe recipe = recipeOptional.get();
            this.modelId = recipe.getModelId();
            this.maxCutCount = recipe.getCutCount();
            this.currentCutCount = 0;
            this.currentCutStack = stack.split(1);
            this.result = recipe.assemble(container, this.level.registryAccess());
            this.refresh();
            level.playSound(null, this.worldPosition,
                    SoundEvents.WOOD_PLACE,
                    SoundSource.BLOCKS,
                    1, 1.2F);
            return true;
        }
        return false;
    }

    public boolean onCut(Player player) {
        if (this.level == null) {
            return false;
        }
        if (this.result.isEmpty()) {
            return false;
        }
        if (this.currentCutCount >= this.maxCutCount) {
            return false;
        }
        if (player.getMainHandItem().is(TagItem.KITCHEN_KNIFE)) {
            this.currentCutCount++;
            this.playParticlesSound();
            this.refresh();
            return true;
        }
        return false;
    }

    public void playParticlesSound() {
        if (this.level instanceof ServerLevel serverLevel) {
            RandomSource random = serverLevel.getRandom();
            serverLevel.sendParticles(ParticleTypes.CRIT,
                    this.worldPosition.getX() + 0.25 + random.nextDouble() / 2,
                    this.worldPosition.getY() + 0.25,
                    this.worldPosition.getZ() + 0.25 + random.nextDouble() / 2,
                    2, 0, 0, 0, 0.1);
            serverLevel.playSound(null, this.worldPosition,
                    SoundEvents.WOOD_PLACE,
                    SoundSource.BLOCKS,
                    1, 1.5f + level.random.nextFloat() * 0.4f);
        }
    }

    public boolean onTakeOut(Player player) {
        if (this.level == null) {
            return false;
        }
        if (player.isSecondaryUseActive() && this.currentCutCount == 0 && !this.currentCutStack.isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(player, this.currentCutStack);
            this.modelId = null;
            this.result = ItemStack.EMPTY;
            this.currentCutStack = ItemStack.EMPTY;
            this.currentCutCount = 0;
            this.maxCutCount = 0;
            this.refresh();
            level.playSound(null, this.worldPosition,
                    SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                    SoundSource.BLOCKS,
                    1, 1.2f + level.random.nextFloat() * 0.2f);
            return true;
        }
        if (this.result.isEmpty()) {
            return false;
        }
        if (this.currentCutCount < this.maxCutCount) {
            return false;
        }
        ItemEntity entityItem = new ItemEntity(this.level,
                this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 0.25,
                this.worldPosition.getZ() + 0.5,
                this.result.copy());
        entityItem.setPickUpDelay(10);
        entityItem.setDeltaMovement(0, 0.1, 0);
        this.level.addFreshEntity(entityItem);
        this.modelId = null;
        this.result = ItemStack.EMPTY;
        this.currentCutStack = ItemStack.EMPTY;
        this.currentCutCount = 0;
        this.maxCutCount = 0;
        this.refresh();
        level.playSound(null, this.worldPosition,
                SoundEvents.WOOD_PLACE,
                SoundSource.BLOCKS,
                1, 2 + level.random.nextFloat() * 0.2f);
        return true;
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
        if (this.modelId != null) {
            tag.putString(MODEL_ID, this.modelId.toString());
        }
        tag.putInt(MAX_CUT_COUNT, this.maxCutCount);
        tag.putInt(CURRENT_CUT_COUNT, this.currentCutCount);
        tag.put(CURRENT_CUT_STACK, this.currentCutStack.serializeNBT());
        tag.put(RESULT_ITEM, this.result.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(MODEL_ID)) {
            this.modelId = new ResourceLocation(tag.getString(MODEL_ID));
        }
        this.maxCutCount = tag.getInt(MAX_CUT_COUNT);
        this.currentCutCount = tag.getInt(CURRENT_CUT_COUNT);
        if (tag.contains(CURRENT_CUT_STACK)) {
            this.currentCutStack = ItemStack.of(tag.getCompound(CURRENT_CUT_STACK));
        }
        if (tag.contains(RESULT_ITEM)) {
            this.result = ItemStack.of(tag.getCompound(RESULT_ITEM));
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

    @Nullable
    public ResourceLocation getModelId() {
        return modelId;
    }

    public int getMaxCutCount() {
        return maxCutCount;
    }

    public int getCurrentCutCount() {
        return currentCutCount;
    }

    public ItemStack getCurrentCutStack() {
        return currentCutStack;
    }
}
