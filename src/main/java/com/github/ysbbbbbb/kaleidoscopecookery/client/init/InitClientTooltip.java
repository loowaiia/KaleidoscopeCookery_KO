package com.github.ysbbbbbb.kaleidoscopecookery.client.init;

import com.github.ysbbbbbb.kaleidoscopecookery.client.tooltip.ClientItemContainerTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.ItemContainerTooltip;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class InitClientTooltip {
    @SubscribeEvent
    public static void onRegisterClientTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ItemContainerTooltip.class, ClientItemContainerTooltip::new);
    }
}
