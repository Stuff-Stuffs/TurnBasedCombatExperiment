package io.github.stuff_stuffs.tbcexcore.client.network;

public final class ClientNetwork {
    private ClientNetwork() {
        BattleUpdateReceiver.init();
        PlayerJoinBattleReceiver.init();
    }

    public static void init() {
        BattleUpdateReceiver.init();
    }
}
