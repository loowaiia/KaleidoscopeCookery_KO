package com.github.ysbbbbbb.kaleidoscopecookery.event.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KaleidoscopeCookery.MOD_ID)
public class SatiatedShieldEvent {
    @SubscribeEvent
    public static void onPlayerHurt(LivingDamageEvent event) {
        int amount = Math.round(event.getAmount());
        if (event.getEntity() instanceof Player player && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (player.getFoodData().getFoodLevel() > 0 && player.hasEffect(ModEffects.SATIATED_SHIELD.get())) {
                int level = player.getFoodData().getFoodLevel() - amount;
                // 原版是 4 点 Exhaustion 对应 1 点 Food Level
                player.causeFoodExhaustion(Math.max(0, level / 4f));
                event.setCanceled(true);
            }
        }
    }
}
