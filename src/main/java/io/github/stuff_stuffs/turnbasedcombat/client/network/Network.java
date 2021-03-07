package io.github.stuff_stuffs.turnbasedcombat.client.network;

import io.github.stuff_stuffs.turnbasedcombat.common.network.BattleCameraSpawnS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class Network {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(BattleCameraSpawnS2CPacket.IDENTIFIER, BattleCameraSpawnReceiver::receive);
    }

    private Network() {
    }
}
