package io.github.stuff_stuffs.turnbasedcombat.client.network;

import io.github.stuff_stuffs.turnbasedcombat.common.api.MovementPlayer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public final class MovementAbilityReceiver {


    private MovementAbilityReceiver() {
    }

    public static void receive(final MinecraftClient client, final ClientPlayNetworkHandler clientPlayNetworkHandler, final PacketByteBuf buf, final PacketSender packetSender) {
        if (client.player != null) {
            ((MovementPlayer) client.player).setMovementDisabled_turn_based_combat(buf.readBoolean());
        }
    }
}
