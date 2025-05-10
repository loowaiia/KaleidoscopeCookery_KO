package com.github.ysbbbbbb.kaleidoscopecookery.datagen;


import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdditionLootModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

import java.util.Optional;

public class GlobalLootModifier extends GlobalLootModifierProvider {
    private static final ResourceLocation ENTITY = new ResourceLocation("entity");

    public GlobalLootModifier(PackOutput output) {
        super(output, KaleidoscopeCookery.MOD_ID);
    }

    @Override
    public void start() {
        addEntityLootModifier("pig", EntityType.PIG);
        addEntityLootModifier("piglin", EntityType.PIGLIN);
        addEntityLootModifier("piglin_brute", EntityType.PIGLIN_BRUTE);
        addEntityLootModifier("hoglin", EntityType.HOGLIN);
    }

    private void addEntityLootModifier(String name, EntityType<?> type) {
        var conditions = new LootItemCondition[]{};
        this.add(name, new AdditionLootModifier(conditions, ENTITY, Optional.of(type.getDefaultLootTable()), modLoc(name)));
    }

    public ResourceLocation modLoc(String name) {
        return new ResourceLocation(KaleidoscopeCookery.MOD_ID, name);
    }
}
