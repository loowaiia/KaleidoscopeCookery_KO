package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.RiceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.tag.TagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceBlockMatchTool;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceEntityMatchTool;
import com.google.common.collect.Sets;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;

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

            this.add(ModBlocks.TOMATO_CROP.get(), createCropDrops(ModBlocks.TOMATO_CROP.get(), ModItems.TOMATO.get(),
                    ModItems.TOMATO_SEED.get(), createCropBuilder(ModBlocks.TOMATO_CROP.get())));

            var chiliBuilder = createCropBuilder(ModBlocks.CHILI_CROP.get());
            LootPoolSingletonContainer.Builder<?> greenChili = LootItem.lootTableItem(ModItems.GREEN_CHILI.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.2F));
            this.add(ModBlocks.CHILI_CROP.get(), createCropDrops(ModBlocks.CHILI_CROP.get(), ModItems.RED_CHILI.get(), ModItems.CHILI_SEED.get(), chiliBuilder)
                    .withPool(LootPool.lootPool().when(chiliBuilder).add(greenChili)));

            var lettuceBuilder = createCropBuilder(ModBlocks.LETTUCE_CROP.get());
            LootPoolSingletonContainer.Builder<?> caterpillar = LootItem.lootTableItem(ModItems.CATERPILLAR.get())
                    .when(LootItemRandomChanceCondition.randomChance(0.1F));
            this.add(ModBlocks.LETTUCE_CROP.get(), createCropDrops(ModBlocks.LETTUCE_CROP.get(), ModItems.LETTUCE.get(), ModItems.LETTUCE_SEED.get(), lettuceBuilder)
                    .withPool(LootPool.lootPool().when(lettuceBuilder).add(caterpillar)));

            Item riceSeed = ModItems.RICE_SEED.get();
            LootItemCondition.Builder riceCropBuilder = createRiceCropBuilder();
            var countFunction = SetItemCountFunction.setCount(UniformGenerator.between(2, 4));
            LootPool.Builder ricePanicle = LootPool.lootPool().add(LootItem.lootTableItem(ModItems.RICE_PANICLE.get())
                    .when(riceCropBuilder).apply(countFunction).otherwise(LootItem.lootTableItem(riceSeed)));
            LootPool.Builder extraRiceSeeds = LootPool.lootPool().add(LootItem.lootTableItem(riceSeed))
                    .when(riceCropBuilder).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3));

            this.add(ModBlocks.RICE_CROP.get(), this.applyExplosionDecay(ModBlocks.RICE_CROP.get(),
                    LootTable.lootTable().withPool(ricePanicle).withPool(extraRiceSeeds)));

            FoodBiteRegistry.FOOD_DATA_MAP.forEach(this::dropFoodBite);
        }

        private LootItemCondition.Builder createCropBuilder(Block cropBlock) {
            StatePropertiesPredicate.Builder property = StatePropertiesPredicate.Builder
                    .properties().hasProperty(CropBlock.AGE, 7);
            return LootItemBlockStatePropertyCondition
                    .hasBlockStateProperties(cropBlock)
                    .setProperties(property);
        }

        private LootItemCondition.Builder createRiceCropBuilder() {
            StatePropertiesPredicate.Builder property = StatePropertiesPredicate.Builder
                    .properties().hasProperty(CropBlock.AGE, 7)
                    .hasProperty(RiceCropBlock.LOCATION, RiceCropBlock.DOWN);
            return LootItemBlockStatePropertyCondition
                    .hasBlockStateProperties(ModBlocks.RICE_CROP.get())
                    .setProperties(property);
        }

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
            super.generate(output);

            // 穿戴草帽掉落番茄辣椒等种子
            var tomato = getSeed(ModItems.TOMATO_SEED.get());
            var chili = getSeed(ModItems.CHILI_SEED.get());
            var lettuce = getSeed(ModItems.LETTUCE_SEED.get());
            var rice = getSeed(ModItems.RICE_SEED.get());
            LootTable.Builder dropSeed = LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                    .add(tomato).add(chili).add(lettuce).add(rice));
            output.accept(modLoc("straw_hat_seed_drop"), dropSeed);
        }

        private LootPoolSingletonContainer.Builder<?> getSeed(ItemLike item) {
            ItemPredicate hasHat = ItemPredicate.Builder.item().of(TagItem.STRAW_HAT).build();
            LootItemCondition.Builder hatMatches = AdvanceBlockMatchTool.toolMatches(EquipmentSlot.HEAD, hasHat);
            return LootItem.lootTableItem(item)
                    .when(LootItemRandomChanceCondition.randomChance(0.125F)).when(hatMatches)
                    .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 2));
        }

        private void dropFoodBite(ResourceLocation id, FoodBiteRegistry.FoodData data) {
            Block block = ForgeRegistries.BLOCKS.getValue(id);
            Item food = ForgeRegistries.ITEMS.getValue(id);
            if (!(block instanceof FoodBiteBlock foodBiteBlock)) {
                return;
            }
            if (food == null) {
                return;
            }
            ConstantValue exactly = ConstantValue.exactly(1);
            StatePropertiesPredicate.Builder notBite = StatePropertiesPredicate.Builder.properties().hasProperty(foodBiteBlock.getBites(), 0);
            LootItemCondition.Builder builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(foodBiteBlock).setProperties(notBite);

            LootTable.Builder lootTable = LootTable.lootTable();
            for (int i = 0; i < data.getLootItems().size(); i++) {
                ItemLike itemLike = data.getLootItems().get(i);
                LootPool.Builder rolls = LootPool.lootPool().setRolls(exactly).when(ExplosionCondition.survivesExplosion());
                if (i == 0) {
                    rolls.add(LootItem.lootTableItem(food).when(builder).otherwise(LootItem.lootTableItem(itemLike)));
                } else {
                    rolls.add(EmptyLootItem.emptyItem().when(builder).otherwise(LootItem.lootTableItem(itemLike)));
                }
                lootTable.withPool(rolls);
            }
            this.add(block, lootTable);
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

        public ResourceLocation modLoc(String name) {
            return new ResourceLocation(KaleidoscopeCookery.MOD_ID, name);
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

            // 厨具刀杀猪掉油
            ItemPredicate hasKnife = ItemPredicate.Builder.item().of(TagItem.KITCHEN_KNIFE).build();
            LootItemCondition.Builder toolMatches = AdvanceEntityMatchTool.toolMatches(EquipmentSlot.MAINHAND, hasKnife);
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
