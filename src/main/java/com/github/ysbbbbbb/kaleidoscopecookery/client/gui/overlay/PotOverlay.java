package com.github.ysbbbbbb.kaleidoscopecookery.client.gui.overlay;

import com.github.ysbbbbbb.kaleidoscopecookery.block.PotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.entity.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Optional;

public class PotOverlay implements IGuiOverlay {
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = gui.getMinecraft();
        if (minecraft.gameMode == null || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        HitResult hitResult = minecraft.hitResult;
        if (!(hitResult instanceof BlockHitResult result)) {
            return;
        }
        if (result.getType() != HitResult.Type.BLOCK) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        Level level = player.level();
        BlockPos blockPos = result.getBlockPos();
        BlockState blockState = player.level().getBlockState(blockPos);
        if (!blockState.is(ModBlocks.POT.get())) {
            return;
        }
        if (!(level.getBlockEntity(blockPos) instanceof PotBlockEntity pot)) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        int x = screenWidth / 2 - 120;
        int y = screenHeight / 2 - 10;

        Optional<Boolean> value = level.getBlockState(blockPos.below()).getOptionalValue(BlockStateProperties.LIT);
        if (value.isEmpty() || !value.get()) {
            drawWordWrap(guiGraphics, font, Component.translatable("tip.kaleidoscope_cookery.pot.need_lit_stove"), x, y, ChatFormatting.RED.getColor());
            return;
        }

        if (!blockState.getValue(PotBlock.HAS_OIL)) {
            drawWordWrap(guiGraphics, font, Component.translatable("tip.kaleidoscope_cookery.pot.need_oil"), x, y, ChatFormatting.AQUA.getColor());
        } else {
            int status = pot.getStatus();
            int tick = pot.getCurrentTick();

            if (status == PotBlockEntity.HEAT_THE_OIL) {
                if (tick >= PotBlockEntity.INGREDIENT_TICK.start()) {
                    drawWordWrap(guiGraphics, font, Component.translatable("tip.kaleidoscope_cookery.pot.add_ingredient"), x, y, ChatFormatting.RED.getColor());
                } else {
                    drawWordWrap(guiGraphics, font, Component.translatable("tip.kaleidoscope_cookery.pot.heat_the_oil"), x, y, ChatFormatting.GRAY.getColor());
                }
                return;
            }

            if (status == PotBlockEntity.COOKING && pot.getCookingTick() != null) {
                if (pot.getCookingTick().contains(tick)) {
                    drawWordWrap(guiGraphics, font, Component.translatable("tip.kaleidoscope_cookery.pot.done"), x, y, ChatFormatting.RED.getColor());
                } else {
                    drawWordWrap(guiGraphics, font, Component.translatable("tip.kaleidoscope_cookery.pot.need_stir_fry"), x, y, ChatFormatting.GRAY.getColor());
                }
                return;
            }

            if (status == PotBlockEntity.FAIL) {
                drawWordWrap(guiGraphics, font, Component.translatable("tip.kaleidoscope_cookery.pot.fail"), x, y, ChatFormatting.AQUA.getColor());
            }
        }
    }

    private static void drawWordWrap(GuiGraphics graphics, Font font, MutableComponent text, int pX, int pY, int color) {
        for (FormattedCharSequence sequence : font.split(text, 100)) {
            graphics.drawString(font, sequence, pX - font.width(sequence) / 2, pY, color);
            pY += font.lineHeight;
        }
    }
}
