package io.github.stuff_stuffs.tbcexcore.client.network;

import io.github.stuff_stuffs.tbcexcore.common.network.PlayerLeaveBattleSender;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleAwareEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public final class PlayerLeaveBattleReceiver {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(PlayerLeaveBattleSender.IDENTIFIER, PlayerLeaveBattleReceiver::receive);
    }

    private static void receive(final MinecraftClient minecraftClient, final ClientPlayNetworkHandler clientPlayNetworkHandler, final PacketByteBuf buf, final PacketSender packetSender) {
        final PlayerLeaveBattleSender.Reason reason = buf.readEnumConstant(PlayerLeaveBattleSender.Reason.class);
        minecraftClient.execute(() -> {
            ((BattleAwareEntity) MinecraftClient.getInstance().player).tbcex_setCurrentBattle(null);
        });
    }

    private PlayerLeaveBattleReceiver() {
    }
}
