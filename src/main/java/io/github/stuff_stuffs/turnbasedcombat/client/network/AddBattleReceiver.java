package io.github.stuff_stuffs.turnbasedcombat.client.network;

import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleBounds;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.ClientBattleWorldComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public final class AddBattleReceiver {

    private AddBattleReceiver() {
    }

    public static void receive(final MinecraftClient client, final ClientPlayNetworkHandler clientPlayNetworkHandler, final PacketByteBuf buf, final PacketSender packetSender) {
        final BattleHandle handle = BattleHandle.fromBuf(buf);
        final BattleBounds bounds = BattleBounds.fromBuf(buf);
        final ClientBattleWorldComponent battleWorld = (ClientBattleWorldComponent) Components.BATTLE_WORLD_COMPONENT_KEY.get(client.world);
        battleWorld.addPendingBattle(handle, bounds);
    }
}
