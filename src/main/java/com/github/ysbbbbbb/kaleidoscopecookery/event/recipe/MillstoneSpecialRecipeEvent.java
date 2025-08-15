package com.github.ysbbbbbb.kaleidoscopecookery.event.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.MillstoneTakeItemEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.OilPotItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class MillstoneSpecialRecipeEvent {
    @SubscribeEvent
    public static void onMillstoneTakeItem(MillstoneTakeItemEvent event) {
        MillstoneBlockEntity millstone = event.getMillstone();
        ItemStack output = millstone.getOutput();

        if (!output.is(ModItems.OIL_POT.get())) {
            return;
        }

        // 油壶是 8 个取出 1 滴油
        LivingEntity user = event.getUser();
        ItemStack heldItem = event.getHeldItem();

        // 兼容容器是否正确
        if (heldItem.isEmpty() || !heldItem.is(ModItems.OIL_POT.get())) {
            Component carrierName = ModItems.OIL_POT.get().getDefaultInstance().getHoverName();
            millstone.sendActionBarMessage(user, "tip.kaleidoscope_cookery.pot.need_carrier", carrierName);
            event.setResult(false);
            event.setCanceled(true);
            return;
        }

        // 油壶满了
        int oilCount = OilPotItem.getOilCount(heldItem);
        if (oilCount >= OilPotItem.MAX_COUNT) {
            millstone.sendActionBarMessage(user, "tip.kaleidoscope_cookery.millstone.oil_pot_is_full");
            event.setResult(false);
            event.setCanceled(true);
            return;
        }

        // 不足 8 个时，概率产出
        RandomSource random = user.level().getRandom();
        if (random.nextInt(8) < output.getCount()) {
            ItemStack takeItem = heldItem.split(1);
            OilPotItem.setOilCount(takeItem, oilCount + 1);
            ItemUtils.getItemToLivingEntity(user, takeItem);
        }

        millstone.resetWhenTakeout();
        event.setResult(true);
        event.setCanceled(true);
    }
}
