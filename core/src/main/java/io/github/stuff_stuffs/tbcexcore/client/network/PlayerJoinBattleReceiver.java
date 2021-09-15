package io.github.stuff_stuffs.tbcexcore.client.network;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.network.PlayerJoinBattleSender;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleAwareEntity;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public final class PlayerJoinBattleReceiver {
    private PlayerJoinBattleReceiver() {
    }

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(PlayerJoinBattleSender.IDENTIFIER, PlayerJoinBattleReceiver::receive);
    }

    private static void receive(final MinecraftClient minecraftClient, final ClientPlayNetworkHandler clientPlayNetworkHandler, final PacketByteBuf buf, final PacketSender packetSender) {
        final int id = buf.readVarInt();
        minecraftClient.execute(() -> {
            final BattleHandle handle = new BattleHandle(id);
            ((BattleAwareEntity) MinecraftClient.getInstance().player).tbcex_setCurrentBattle(handle);
            ((BattleWorldSupplier)MinecraftClient.getInstance().world).tbcex_getBattleWorld().getBattle(handle);
        });
    }
}
