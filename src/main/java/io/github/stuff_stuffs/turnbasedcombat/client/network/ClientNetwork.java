package io.github.stuff_stuffs.turnbasedcombat.client.network;

public final class ClientNetwork {
    private ClientNetwork() {
        BattleUpdateReceiver.init();
        PlayerJoinBattleReceiver.init();
    }

    public static void init() {
        BattleUpdateReceiver.init();
    }
}
