package io.github.stuff_stuffs.tbcexcore.client.network;

public final class ClientNetwork {
    private ClientNetwork() {
    }

    public static void init() {
        BattleUpdateReceiver.init();
        PlayerJoinBattleReceiver.init();
        PlayerLeaveBattleReceiver.init();
    }
}
