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
        var helper = event.getExistingFileHelper();
        var pack = generator.getPackOutput();

        generator.addProvider(event.includeServer(), new LootTableProvider(pack, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(LootTableGenerator.BlockLootTables::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(LootTableGenerator.EntityLootTables::new, LootContextParamSets.ENTITY)
        )));
        var block = vanillaPack.addProvider(packOutput -> new TagBlock(packOutput, registries, helper));
        vanillaPack.addProvider(packOutput -> new TagItem(packOutput, registries, block.contentsGetter(), helper));
        vanillaPack.addProvider(packOutput -> new TagPoiType(packOutput, registries, helper));
        generator.addProvider(event.includeServer(), new ModRecipeGenerator(generator));

        generator.addProvider(true, new ForgeAdvancementProvider(
                pack, registries, helper,
                Collections.singletonList(new AdvancementGenerator())
        ));

        generator.addProvider(event.includeServer(), new GlobalLootModifier(pack));

        generator.addProvider(event.includeClient(), new ParticleDescriptionGenerator(pack, helper));
        generator.addProvider(event.includeClient(), new BlockModelGenerator(pack, helper));
        generator.addProvider(event.includeClient(), new BlockStateGenerator(pack, helper));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(pack, helper));

        generator.addProvider(event.includeServer(), new SoundDefinitionsGenerator(pack, helper));
    }
}
