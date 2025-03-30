package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.model.BlockModelGenerator;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.model.BlockStateGenerator;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.model.ItemModelGenerator;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.tag.TagBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.tag.TagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.tag.TagPoiType;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var registries = event.getLookupProvider();
        var vanillaPack = generator.getVanillaPack(true);
        var existingFileHelper = event.getExistingFileHelper();
        var pack = generator.getPackOutput();

        generator.addProvider(event.includeServer(), new LootTableProvider(pack, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(LootTableGenerator.BlockLootTables::new, LootContextParamSets.BLOCK))));
        var blockTagsProvider = vanillaPack.addProvider(packOutput ->
                new TagBlock(packOutput, registries, existingFileHelper));
        vanillaPack.addProvider(packOutput ->
                new TagItem(packOutput, registries, blockTagsProvider.contentsGetter(), existingFileHelper));
        vanillaPack.addProvider(packOutput -> new TagPoiType(packOutput, registries, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(generator));

        generator.addProvider(true, new ForgeAdvancementProvider(
                pack, registries, existingFileHelper,
                Collections.singletonList(new AdvancementGenerator())
        ));

        generator.addProvider(event.includeClient(), new BlockModelGenerator(pack, existingFileHelper));
        generator.addProvider(event.includeClient(), new BlockStateGenerator(pack, existingFileHelper));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(pack, existingFileHelper));
    }
}
