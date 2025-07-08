package com.github.ysbbbbbb.kaleidoscopecookery.network.message;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FlatulenceMessage {
    public FlatulenceMessage() {
    }

    public static void encode(FlatulenceMessage message, FriendlyByteBuf buf) {
    }

    public static FlatulenceMessage decode(FriendlyByteBuf buf) {
        return new FlatulenceMessage();
    }

    public static void handle(FlatulenceMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> onEffect(context));
        }
        context.setPacketHandled(true);
    }

    private static void onEffect(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null && player.hasEffect(ModEffects.FLATULENCE.get())) {
            ServerLevel serverLevel = player.serverLevel();
            serverLevel.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.25, player.getZ(), 10, 0.25, 0.25, 0.25, 0.1);
            serverLevel.playSound(null, player.blockPosition(), ModSounds.ENTITY_FART.get(), SoundSource.PLAYERS, 1.0F, 0.8F + (float) Math.random() * 0.4F);
        }
    }
}
