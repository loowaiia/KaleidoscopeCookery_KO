package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ai.CatLieOnBasketGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeCookery.MOD_ID)
public class EntityJoinWorldEvent {
    @SubscribeEvent
    public static void onCatJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Cat cat) {
            cat.goalSelector.addGoal(5, new CatLieOnBasketGoal(cat, 1.1, 8));
        }
    }
}
