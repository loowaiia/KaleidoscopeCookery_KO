package com.github.ysbbbbbb.kaleidoscopecookery.loot;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModLootModifier;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public class AdvanceMatchTool implements LootItemCondition {
    private final EquipmentSlot slot;
    private final ItemPredicate predicate;

    public AdvanceMatchTool(EquipmentSlot slot, ItemPredicate predicate) {
        this.slot = slot;
        this.predicate = predicate;
    }

    @Override
    public LootItemConditionType getType() {
        return ModLootModifier.ADVANCE_MATCH_TOOL;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.LAST_DAMAGE_PLAYER);
    }

    @Override
    public boolean test(LootContext context) {
        if (context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)) {
            Player player = context.getParam(LootContextParams.LAST_DAMAGE_PLAYER);
            ItemStack stack = player.getItemBySlot(this.slot);
            return this.predicate.matches(stack);
        }
        return false;
    }

    public static LootItemCondition.Builder toolMatches(EquipmentSlot slot, ItemPredicate builder) {
        return () -> new AdvanceMatchTool(slot, builder);
    }

    public static class AdvanceMatchToolSerializer implements Serializer<AdvanceMatchTool> {
        @Override
        public void serialize(JsonObject object, AdvanceMatchTool matchTool, JsonSerializationContext context) {
            object.addProperty("slot", matchTool.slot.getName());
            object.add("predicate", matchTool.predicate.serializeToJson());
        }

        @Override
        public AdvanceMatchTool deserialize(JsonObject object, JsonDeserializationContext context) {
            EquipmentSlot slot = EquipmentSlot.byName(object.get("slot").getAsString());
            ItemPredicate itemPredicate = ItemPredicate.fromJson(object.get("predicate"));
            return new AdvanceMatchTool(slot, itemPredicate);
        }
    }
}
