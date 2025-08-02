package com.github.ysbbbbbb.kaleidoscopecookery.init.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.carryon.CarryOnBlackList;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import static com.github.ysbbbbbb.kaleidoscopecookery.compat.carryon.CarryOnBlackList.CARRY_ON_ID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CompatRegistry {
    @SubscribeEvent
    public static void onEnqueue(final InterModEnqueueEvent event) {
        event.enqueueWork(() -> checkModLoad(CARRY_ON_ID, CarryOnBlackList::addBlackList));
    }

    private static void checkModLoad(String modId, Runnable runnable) {
        if (ModList.get().isLoaded(modId)) {
            runnable.run();
        }
    }
}
