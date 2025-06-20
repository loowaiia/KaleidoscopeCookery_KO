package com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.mixin.MobBucketItemAccessor;
import com.github.ysbbbbbb.kaleidoscopecookery.recipe.StockpotRecipe;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class StockpotRecipeCategory implements IRecipeCategory<StockpotRecipe> {
    public static final RecipeType<StockpotRecipe> TYPE = RecipeType.create(KaleidoscopeCookery.MOD_ID, "stockpot", StockpotRecipe.class);
    private static final ResourceLocation BG = new ResourceLocation(KaleidoscopeCookery.MOD_ID, "textures/gui/jei/stockpot.png");
    private static final MutableComponent TITLE = Component.translatable("block.kaleidoscope_cookery.stockpot");
    private static final Map<ResourceLocation, Item> RECIPE_TO_BUCKET_MAP = Maps.newHashMap();
    public static final int WIDTH = 176;
    public static final int HEIGHT = 102;
    private final IDrawable bgDraw;
    private final IDrawable iconDraw;
    private final IDrawable slotDraw;
    private final IGuiHelper guiHelper;

    public StockpotRecipeCategory(IGuiHelper guiHelper) {
        this.bgDraw = guiHelper.createDrawable(BG, 0, 0, WIDTH, HEIGHT);
        this.iconDraw = guiHelper.createDrawableItemLike(ModItems.STOCKPOT.get());
        this.slotDraw = guiHelper.getSlotDrawable();
        this.guiHelper = guiHelper;
    }

    public static List<StockpotRecipe> getRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        // 先尝试获取所有的生物桶
        Map<EntityType<?>, Item> entityTypeToBucketMap = Maps.newHashMap();
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof MobBucketItemAccessor)
                .forEach(item -> {
                    MobBucketItemAccessor mobBucketItem = (MobBucketItemAccessor) item;
                    entityTypeToBucketMap.put(mobBucketItem.kaleidoscope$GetFishType(), item);
                });

        // 读取合成表
        List<StockpotRecipe> stockpotRecipes = Lists.newArrayList();
        stockpotRecipes.addAll(level.getRecipeManager().getAllRecipesFor(ModRecipes.STOCKPOT_RECIPE));

        // 将生物桶映射到对应的配方
        stockpotRecipes.forEach(recipe -> {
            EntityType<?> entityType = recipe.getInputEntityType();
            if (entityType != null && entityTypeToBucketMap.containsKey(entityType)) {
                Item bucketItem = entityTypeToBucketMap.get(entityType);
                RECIPE_TO_BUCKET_MAP.put(recipe.getId(), bucketItem);
            } else {
                Item bucket = recipe.getSoupBase().getBucket();
                RECIPE_TO_BUCKET_MAP.put(recipe.getId(), bucket);
            }
        });
        return stockpotRecipes;
    }

    @Override
    public void draw(StockpotRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.bgDraw.draw(guiGraphics);
        guiHelper.createDrawableItemLike(Items.BOWL).draw(guiGraphics, 133, 18);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, StockpotRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> inputs = recipe.getIngredients();
        ItemStack output = recipe.getResult();
        for (int i = 0; i < inputs.size(); i++) {
            int xOffset = (i % 3) * 18 + 15;
            int yOffset = (i / 3) * 18 + 25;
            builder.addSlot(RecipeIngredientRole.INPUT, xOffset, yOffset).addIngredients(inputs.get(i)).setBackground(slotDraw, -1, -1);
        }
        Item bucket = RECIPE_TO_BUCKET_MAP.get(recipe.getId());
        if (bucket != null) {
            builder.addSlot(RecipeIngredientRole.INPUT, 72, 61).addIngredients(Ingredient.of(bucket));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 143, 60).addItemStack(output);
    }

    @Override
    public RecipeType<StockpotRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    @Nullable
    public IDrawable getIcon() {
        return iconDraw;
    }
}
