package io.github.stuff_stuffs.turnbasedcombat.client.network;

import io.github.stuff_stuffs.turnbasedcombat.common.network.AddBattleS2C;
import io.github.stuff_stuffs.turnbasedcombat.common.network.RemoveBattleS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class Network {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(AddBattleS2C.IDENTIFIER, AddBattleReceiver::receive);
        ClientPlayNetworking.registerGlobalReceiver(RemoveBattleS2C.IDENTIFIER, RemoveBattleReceiver::receive);
    }

    private Network() {
    }
}
