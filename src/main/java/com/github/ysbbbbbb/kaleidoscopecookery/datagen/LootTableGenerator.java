package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceMatchTool;
import com.google.common.collect.Sets;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

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
            dropSelf(ModBlocks.CHOPPING_BOARD.get());
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

    public static class EntityLootTables extends EntityLootSubProvider {
        public final Set<EntityType<?>> knownEntities = Sets.newHashSet();

        protected EntityLootTables() {
            super(FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
            super.generate(output);

            ItemPredicate hasKnife = ItemPredicate.Builder.item().of(ModItems.KITCHEN_KNIFE.get()).build();
            LootItemCondition.Builder toolMatches = AdvanceMatchTool.toolMatches(EquipmentSlot.MAINHAND, hasKnife);
            var count = SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F));
            var looting = LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F));
            var oil = LootItem.lootTableItem(ModItems.OIL.get()).apply(count).apply(looting);

            LootTable.Builder lessOil = LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(oil).when(toolMatches));
            LootTable.Builder moreOil = LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(2)).add(oil).when(toolMatches));

            output.accept(modLoc("pig"), lessOil);
            output.accept(modLoc("piglin"), moreOil);
            output.accept(modLoc("piglin_brute"), moreOil);
            output.accept(modLoc("hoglin"), moreOil);
        }

        @Override
        public void generate() {
        }

        @Override
        protected boolean canHaveLootTable(EntityType<?> type) {
            return true;
        }

        @Override
        protected Stream<EntityType<?>> getKnownEntityTypes() {
            return knownEntities.stream();
        }

        @Override
        protected void add(EntityType<?> type, LootTable.Builder builder) {
            this.add(type, type.getDefaultLootTable(), builder);
        }

        @Override
        protected void add(EntityType<?> type, ResourceLocation lootTable, LootTable.Builder builder) {
            super.add(type, lootTable, builder);
            knownEntities.add(type);
        }

        public ResourceLocation modLoc(String name) {
            return new ResourceLocation(KaleidoscopeCookery.MOD_ID, name);
        }
    }
}
