package io.github.stuff_stuffs.turnbasedcombat.client.network;

import io.github.stuff_stuffs.turnbasedcombat.common.network.sender.MovementAbility;
import io.github.stuff_stuffs.turnbasedcombat.common.network.sender.TryAddBattle;
import io.github.stuff_stuffs.turnbasedcombat.common.network.sender.RemoveBattle;
import io.github.stuff_stuffs.turnbasedcombat.common.network.sender.UpdateBattleLog;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class Network {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(UpdateBattleLog.IDENTIFIER, UpdateBattleLogReceiver::receive);
        ClientPlayNetworking.registerGlobalReceiver(TryAddBattle.IDENTIFIER, AddBattleReceiver::receive);
        ClientPlayNetworking.registerGlobalReceiver(RemoveBattle.IDENTIFIER, RemoveBattleReceiver::receive);
        ClientPlayNetworking.registerGlobalReceiver(MovementAbility.IDENTIFIER, MovementAbilityReceiver::receive);
    }

    private Network() {
    }
}
