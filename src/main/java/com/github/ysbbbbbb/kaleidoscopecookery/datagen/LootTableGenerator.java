package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public class LootTableGenerator {
    public static class BlockLootTables extends BlockLootSubProvider {
        public final Set<Block> knownBlocks = new HashSet<>();

        public BlockLootTables() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        public void generate() {
            dropSelf(ModBlocks.STOVE.get());
            dropSelf(ModBlocks.POT.get());
            dropSelf(ModBlocks.SUSPICIOUS_STIR_FRY.get());
            dropSelf(ModBlocks.DARK_CUISINE.get());
            dropSelf(ModBlocks.COOK_STOOL_OAK.get());
            dropSelf(ModBlocks.COOK_STOOL_SPRUCE.get());
            dropSelf(ModBlocks.COOK_STOOL_ACACIA.get());
            dropSelf(ModBlocks.COOK_STOOL_BAMBOO.get());
            dropSelf(ModBlocks.COOK_STOOL_BIRCH.get());
            dropSelf(ModBlocks.COOK_STOOL_CHERRY.get());
            dropSelf(ModBlocks.COOK_STOOL_CRIMSON.get());
            dropSelf(ModBlocks.COOK_STOOL_DARK_OAK.get());
            dropSelf(ModBlocks.COOK_STOOL_JUNGLE.get());
            dropSelf(ModBlocks.COOK_STOOL_MANGROVE.get());
            dropSelf(ModBlocks.COOK_STOOL_WARPED.get());
        }

        @Override
        public void add(Block block, LootTable.Builder builder) {
            this.knownBlocks.add(block);
            super.add(block, builder);
        }

        @Override
        public Iterable<Block> getKnownBlocks() {
            return this.knownBlocks;
        }
    }
}
