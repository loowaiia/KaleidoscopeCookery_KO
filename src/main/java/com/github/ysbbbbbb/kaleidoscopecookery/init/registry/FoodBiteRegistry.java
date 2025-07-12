package com.github.ysbbbbbb.kaleidoscopecookery.init.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FoodBiteRegistry {
    public static final Map<ResourceLocation, FoodData> FOOD_DATA_MAP = Maps.newLinkedHashMap();

    public static ResourceLocation DARK_CUISINE;
    public static ResourceLocation SUSPICIOUS_STIR_FRY;
    public static ResourceLocation SLIME_BALL_MEAL;
    public static ResourceLocation FONDANT_PIE;
    public static ResourceLocation DONGPO_PORK;
    public static ResourceLocation FONDANT_SPIDER_EYE;
    public static ResourceLocation CHORUS_FRIED_EGG;
    public static ResourceLocation BRAISED_FISH;
    public static ResourceLocation GOLDEN_SALAD;
    public static ResourceLocation SPICY_CHICKEN;
    public static ResourceLocation YAKITORI;
    public static ResourceLocation CRYSTAL_LAMB_CHOP;
    public static ResourceLocation NETHER_STYLE_SASHIMI;
    public static ResourceLocation PAN_SEARED_KNIGHT_STEAK;
    public static ResourceLocation STARGAZY_PIE;
    public static ResourceLocation SWEET_AND_SOUR_ENDER_PEARLS;
    public static ResourceLocation BLAZE_LAMB_CHOP;
    public static ResourceLocation FROST_LAMB_CHOP;
    public static ResourceLocation END_STYLE_SASHIMI;
    public static ResourceLocation DESERT_STYLE_SASHIMI;
    public static ResourceLocation TUNDRA_STYLE_SASHIMI;
    public static ResourceLocation COLD_STYLE_SASHIMI;

    public static void init() {
        FoodBiteRegistry registry = new FoodBiteRegistry();

        DARK_CUISINE = registry.registerFoodData("dark_cuisine", FoodData
                .maxBites(3).setBlockFood(2, 0.1f)
                .setBlockFood(ModFoods.DARK_CUISINE_BLOCK).setItemFood(ModFoods.DARK_CUISINE_ITEM)
                .setAnimateTick(FoodBiteAnimateTicks.DARK_CUISINE_ANIMATE_TICK));

        SUSPICIOUS_STIR_FRY = registry.registerFoodData("suspicious_stir_fry", FoodData
                .maxBites(1).setBlockFood(2, 0.1f)
                .setBlockFood(ModFoods.SUSPICIOUS_STIR_FRY).setItemFood(ModFoods.SUSPICIOUS_STIR_FRY)
                .setAnimateTick(FoodBiteAnimateTicks.SUSPICIOUS_STIR_FRY_ANIMATE_TICK));

        SLIME_BALL_MEAL = registry.registerFoodData("slime_ball_meal", FoodData
                .maxBites(3).setFood(3, 0.2f));

        FONDANT_PIE = registry.registerFoodData("fondant_pie", FoodData
                .maxBites(4).setFood(4, 0.2f));

        DONGPO_PORK = registry.registerFoodData("dongpo_pork", FoodData
                .maxBites(3).setFood(3, 0.2f)
                .addLootItems(Items.BAMBOO));

        FONDANT_SPIDER_EYE = registry.registerFoodData("fondant_spider_eye", FoodData
                .maxBites(4).setFood(4, 0.2f));

        CHORUS_FRIED_EGG = registry.registerFoodData("chorus_fried_egg", FoodData
                .maxBites(3).setFood(3, 0.2f));

        BRAISED_FISH = registry.registerFoodData("braised_fish", FoodData
                .maxBites(4).setFood(4, 0.2f)
                .addLootItems(Items.BONE, Items.BONE_MEAL));

        GOLDEN_SALAD = registry.registerFoodData("golden_salad", FoodData
                .maxBites(6).setFood(6, 0.2f));

        SPICY_CHICKEN = registry.registerFoodData("spicy_chicken", FoodData
                .maxBites(4).setFood(4, 0.2f));

        YAKITORI = registry.registerFoodData("yakitori", FoodData
                .maxBites(4).setFood(4, 0.2f));

        CRYSTAL_LAMB_CHOP = registry.registerFoodData("crystal_lamb_chop", FoodData
                .maxBites(3).setFood(3, 0.2f)
                .addLootItems(Items.AMETHYST_SHARD));

        NETHER_STYLE_SASHIMI = registry.registerFoodData("nether_style_sashimi", FoodData
                .maxBites(4).setFood(4, 0.2f)
                .addLootItems(Items.CRIMSON_FUNGUS, Items.WARPED_FUNGUS));

        PAN_SEARED_KNIGHT_STEAK = registry.registerFoodData("pan_seared_knight_steak", FoodData
                .maxBites(4).setFood(4, 0.2f)
                .addLootItems(Items.BONE, Items.BONE_MEAL));

        STARGAZY_PIE = registry.registerFoodData("stargazy_pie", FoodData
                .maxBites(4).setFood(4, 0.2f));

        SWEET_AND_SOUR_ENDER_PEARLS = registry.registerFoodData("sweet_and_sour_ender_pearls", FoodData
                .maxBites(3).setFood(3, 0.2f));

        BLAZE_LAMB_CHOP = registry.registerFoodData("blaze_lamb_chop", FoodData
                .maxBites(3).setFood(3, 0.2f)
                .addLootItems(Items.BLAZE_ROD));

        FROST_LAMB_CHOP = registry.registerFoodData("frost_lamb_chop", FoodData
                .maxBites(3).setFood(3, 0.2f)
                .addLootItems(Items.BLUE_ICE));

        END_STYLE_SASHIMI = registry.registerFoodData("end_style_sashimi", FoodData
                .maxBites(4).setFood(4, 0.2f)
                .addLootItems(Items.CHORUS_FRUIT));

        DESERT_STYLE_SASHIMI = registry.registerFoodData("desert_style_sashimi", FoodData
                .maxBites(4).setFood(4, 0.2f)
                .addLootItems(Items.CACTUS));

        TUNDRA_STYLE_SASHIMI = registry.registerFoodData("tundra_style_sashimi", FoodData
                .maxBites(4).setFood(4, 0.2f));

        COLD_STYLE_SASHIMI = registry.registerFoodData("cold_style_sashimi", FoodData
                .maxBites(4).setFood(4, 0.2f)
                .addLootItems(Items.SNOWBALL, Items.SNOWBALL));
    }

    public ResourceLocation registerFoodData(ResourceLocation foodName, FoodData data) {
        FOOD_DATA_MAP.put(foodName, data);
        return foodName;
    }

    public ResourceLocation registerFoodData(String foodName, FoodData data) {
        ResourceLocation id = mcLoc(foodName);
        FOOD_DATA_MAP.put(id, data);
        return id;
    }

    public static ResourceLocation mcLoc(String name) {
        return new ResourceLocation(KaleidoscopeCookery.MOD_ID, name);
    }

    public static Item getItem(ResourceLocation name) {
        return ForgeRegistries.ITEMS.getValue(name);
    }

    public static Block getBlock(ResourceLocation name) {
        return ForgeRegistries.BLOCKS.getValue(name);
    }

    public static final class FoodData {
        private final int maxBites;
        private final List<ItemLike> lootItems = Lists.newArrayList();
        private FoodProperties blockFood = new FoodProperties.Builder().nutrition(0).saturationMod(0).alwaysEat().build();
        private FoodProperties itemFood = new FoodProperties.Builder().nutrition(0).saturationMod(0).alwaysEat().build();
        private @Nullable FoodBiteAnimateTicks.AnimateTick animateTick = null;

        private FoodData(int maxBites) {
            this.maxBites = maxBites;
            this.lootItems.add(Items.BOWL);
        }

        public static FoodData maxBites(int maxBites) {
            return new FoodData(maxBites);
        }

        public FoodData setFood(int nutrition, float saturationModifier) {
            int count = maxBites - 1;
            this.blockFood = new FoodProperties.Builder().nutrition(nutrition / count)
                    .saturationMod(saturationModifier / count).alwaysEat().build();
            this.itemFood = new FoodProperties.Builder().nutrition(nutrition)
                    .saturationMod(saturationModifier).alwaysEat().build();
            return this;
        }

        public FoodData setBlockFood(FoodProperties blockFood) {
            this.blockFood = blockFood;
            return this;
        }

        public FoodData setBlockFood(int nutrition, float saturationModifier) {
            this.blockFood = new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturationModifier).alwaysEat().build();
            return this;
        }

        public FoodData setItemFood(FoodProperties itemFood) {
            this.itemFood = itemFood;
            return this;
        }

        public FoodData setItemFood(int nutrition, float saturationModifier) {
            this.itemFood = new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturationModifier).alwaysEat().build();
            return this;
        }

        public FoodData setAnimateTick(FoodBiteAnimateTicks.AnimateTick animateTick) {
            this.animateTick = animateTick;
            return this;
        }

        public FoodData addLootItems(ItemLike... lootItems) {
            this.lootItems.addAll(Arrays.stream(lootItems).toList());
            return this;
        }

        public int maxBites() {
            return maxBites;
        }

        @Nullable
        public FoodBiteAnimateTicks.AnimateTick animateTick() {
            return animateTick;
        }

        public List<ItemLike> getLootItems() {
            return lootItems;
        }

        public FoodProperties blockFood() {
            return blockFood;
        }

        public FoodProperties itemFood() {
            return itemFood;
        }
    }
}
