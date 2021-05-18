package io.github.stuff_stuffs.turnbasedcombat.client.network;

public final class ClientNetwork {

    public static void init() {
        BattleUpdateReceiver.init();
    }

    private ClientNetwork() {
    }
}
