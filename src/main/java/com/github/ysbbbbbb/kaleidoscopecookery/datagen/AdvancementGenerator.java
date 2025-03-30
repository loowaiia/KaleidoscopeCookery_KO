package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

public class AdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
    }

    private static Advancement.Builder make(ItemLike item, String key) {
        MutableComponent title = Component.translatable(String.format("advancements.kaleidoscope_cookery.cookery.%s.title", key));
        MutableComponent desc = Component.translatable(String.format("advancements.kaleidoscope_cookery.cookery.%s.description", key));

        return Advancement.Builder.advancement().display(item, title, desc,
                new ResourceLocation(KaleidoscopeCookery.MOD_ID, "textures/advancement/background.png"),
                FrameType.TASK, false, false, false);
    }

    private static Advancement.Builder makeChallenge(ItemLike item, String key) {
        MutableComponent title = Component.translatable(String.format("advancements.kaleidoscope_cookery.cookery.%s.title", key));
        MutableComponent desc = Component.translatable(String.format("advancements.kaleidoscope_cookery.cookery.%s.description", key));

        return Advancement.Builder.advancement().display(item, title, desc,
                new ResourceLocation(KaleidoscopeCookery.MOD_ID, "textures/advancement/background.png"),
                FrameType.CHALLENGE, true, true, false);
    }

    private static ResourceLocation id(String id) {
        return new ResourceLocation(KaleidoscopeCookery.MOD_ID, id);
    }
}
