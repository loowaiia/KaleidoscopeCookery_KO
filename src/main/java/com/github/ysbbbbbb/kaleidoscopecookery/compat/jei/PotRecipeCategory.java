package com.github.ysbbbbbb.kaleidoscopecookery.compat.jei;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.recipe.PotRecipe;
import com.google.common.collect.Lists;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PotRecipeCategory implements IRecipeCategory<PotRecipe> {
    public static final RecipeType<PotRecipe> TYPE = RecipeType.create(KaleidoscopeCookery.MOD_ID, "pot", PotRecipe.class);
    private static final MutableComponent TITLE = Component.translatable("block.kaleidoscope_cookery.pot");
    public static final int WIDTH = 150;
    public static final int HEIGHT = 76;
    private final IGuiHelper guiHelper;
    private final IDrawable slotDraw;
    private final IDrawable iconDraw;

    public PotRecipeCategory(IGuiHelper guiHelper) {
        this.slotDraw = guiHelper.getSlotDrawable();
        this.iconDraw = guiHelper.createDrawableItemLike(ModItems.POT.get());
        this.guiHelper = guiHelper;
    }

    public static List<PotRecipe> getRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        List<PotRecipe> potRecipes = Lists.newArrayList();
        potRecipes.addAll(level.getRecipeManager().getAllRecipesFor(ModRecipes.POT_RECIPE));
        return potRecipes;
    }

    @Override
    public void draw(PotRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Component stirFryCount = Component.translatable("jei.kaleidoscope_cookery.pot.stir_fry_count", recipe.getStirFryCount());
        drawCenteredString(guiGraphics, stirFryCount, WIDTH / 2, 66);
        if (recipe.isNeedBowl()) {
            guiHelper.createDrawableItemLike(Items.BOWL).draw(guiGraphics, 128, 5);
        }
        int scale = 3;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, -1.5, 0);
        guiGraphics.pose().scale(scale, scale, 1);
        guiHelper.createDrawableItemLike(ModBlocks.STOVE.get()).draw(guiGraphics, 70 / scale, 12 / scale);
        guiGraphics.pose().translate(0.5, 0, 8);
        guiHelper.createDrawableItemLike(ModBlocks.POT.get()).draw(guiGraphics, 70 / scale, -14 / scale);
        guiGraphics.pose().popPose();
    }

    private void drawCenteredString(GuiGraphics guiGraphics, Component text, int centerX, int y) {
        Font font = Minecraft.getInstance().font;
        FormattedCharSequence sequence = text.getVisualOrderText();
        guiGraphics.drawString(font, sequence, centerX - font.width(sequence) / 2, y, 0x555555, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PotRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> inputs = recipe.getIngredients();
        ItemStack output = recipe.getResult();
        for (int i = 0; i < inputs.size(); i++) {
            int xOffset = (i % 3) * 18 + 5;
            int yOffset = (i / 3) * 18 + 5;
            builder.addSlot(RecipeIngredientRole.INPUT, xOffset, yOffset).addIngredients(inputs.get(i)).setBackground(slotDraw, -1, -1);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 128, 41).addItemStack(output).setBackground(slotDraw, -1, -1);
    }

    @Override
    public RecipeType<PotRecipe> getRecipeType() {
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
