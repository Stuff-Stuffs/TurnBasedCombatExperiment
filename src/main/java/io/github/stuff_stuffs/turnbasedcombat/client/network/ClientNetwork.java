package io.github.stuff_stuffs.turnbasedcombat.client.network;

public final class ClientNetwork {
    private ClientNetwork() {
    }

    public static void init() {
        BattleUpdateReceiver.init();
    }
}
