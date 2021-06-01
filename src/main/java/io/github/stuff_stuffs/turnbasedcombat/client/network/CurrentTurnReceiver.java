package io.github.stuff_stuffs.turnbasedcombat.client.network;

import io.github.stuff_stuffs.turnbasedcombat.common.network.CurrentTurnSender;
import io.github.stuff_stuffs.turnbasedcombat.mixin.api.ClientPlayerExt;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public final class CurrentTurnReceiver {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(CurrentTurnSender.IDENTIFIER, CurrentTurnReceiver::receive);
    }

    private static void receive(final MinecraftClient client, final ClientPlayNetworkHandler clientPlayNetworkHandler, final PacketByteBuf packetByteBuf, final PacketSender packetSender) {
        final boolean b = packetByteBuf.readBoolean();
        client.execute(() -> {
            assert client.player != null;
            ((ClientPlayerExt) client.player).tbcex_setCurrentTurn(b);
        });
    }

    private CurrentTurnReceiver() {
    }
}
