package io.github.stuff_stuffs.turnbasedcombat.client.network;

import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ClientBattleImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.ClientBattleWorldComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.util.OptionalEither;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public final class UpdateBattleLogReceiver {
    private UpdateBattleLogReceiver() {
    }

    public static void receive(final MinecraftClient client, final ClientPlayNetworkHandler clientPlayNetworkHandler, final PacketByteBuf buf, final PacketSender packetSender) {
        final BattleHandle handle = BattleHandle.fromBuf(buf);
        final int count = buf.readVarInt();
        final ClientBattleWorldComponent battleWorld = (ClientBattleWorldComponent) Components.BATTLE_WORLD_COMPONENT_KEY.get(client.world);
        final OptionalEither<ClientBattleImpl, ClientBattleWorldComponent.PendingBattle> either = battleWorld.eitherFromHandle(handle);
        for (int i = 0; i < count; i++) {
            final BattleAction action = BattleAction.fromBuf(buf);
            either.consume(battle -> battle.pushAction(action), battle -> battle.pushAction(action), () -> {
                throw new RuntimeException();
            });
        }
    }
}
