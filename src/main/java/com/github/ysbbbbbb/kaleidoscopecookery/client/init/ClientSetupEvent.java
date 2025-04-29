package com.github.ysbbbbbb.kaleidoscopecookery.client.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.client.gui.overlay.PotOverlay;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.FruitBasketBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.PotBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static net.minecraftforge.client.gui.overlay.VanillaGuiOverlay.CROSSHAIR;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = KaleidoscopeCookery.MOD_ID)
public class ClientSetupEvent {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    public static void onEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        BlockEntityRenderers.register(ModBlocks.POT_BE.get(), PotBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.FRUIT_BASKET_BE.get(), FruitBasketBlockEntityRender::new);
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(CROSSHAIR.id(), "pot_overlay", new PotOverlay());
    }
}
