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
                output.accept(ModItems.POT.get());
                output.accept(ModItems.OIL.get());
                output.accept(ModItems.KITCHEN_KNIFE.get());
                output.accept(ModItems.KITCHEN_SHOVEL.get());
                output.accept(ModItems.SUSPICIOUS_STIR_FRY.get());
                output.accept(ModItems.DARK_CUISINE.get());
                output.accept(ModItems.TOMATO.get());
                output.accept(ModItems.FRIED_EGG.get());
                output.accept(ModItems.SCRAMBLE_EGG_WITH_TOMATOES.get());
                output.accept(ModItems.FONDANT_PIE.get());
                output.accept(ModItems.SLIME_BALL_MEAL.get());
                output.accept(ModItems.CHOPPING_BOARD.get());
                output.accept(ModItems.COOK_STOOL_OAK.get());
                output.accept(ModItems.COOK_STOOL_SPRUCE.get());
                output.accept(ModItems.COOK_STOOL_ACACIA.get());
                output.accept(ModItems.COOK_STOOL_BAMBOO.get());
                output.accept(ModItems.COOK_STOOL_BIRCH.get());
                output.accept(ModItems.COOK_STOOL_CHERRY.get());
                output.accept(ModItems.COOK_STOOL_CRIMSON.get());
                output.accept(ModItems.COOK_STOOL_DARK_OAK.get());
                output.accept(ModItems.COOK_STOOL_JUNGLE.get());
                output.accept(ModItems.COOK_STOOL_MANGROVE.get());
                output.accept(ModItems.COOK_STOOL_WARPED.get());
                output.accept(ModItems.FRUIT_BASKET.get());
                output.accept(ModItems.SCARECROW.get());
                output.accept(ModItems.STRAW_HAT.get());
                output.accept(ModItems.STRAW_HAT_FLOWER.get());
                output.accept(ModItems.TOMATO_SEED.get());
            }).build());
}
