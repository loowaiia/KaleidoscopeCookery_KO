package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KaleidoscopeCookery.MOD_ID);

    public static RegistryObject<CreativeModeTab> COOKERY_MAIN_TAB = TABS.register("cookery_main", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.kaleidoscope_cookery.cookery_main.name"))
            .icon(() -> ModItems.STOVE.get().getDefaultInstance())
            .displayItems((par, output) -> {
                output.accept(ModItems.STOVE.get());
            }).build());
}
