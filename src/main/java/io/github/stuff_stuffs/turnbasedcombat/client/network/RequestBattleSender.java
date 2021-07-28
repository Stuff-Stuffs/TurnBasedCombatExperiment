package io.github.stuff_stuffs.turnbasedcombat.client.network;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class RequestBattleSender {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("request_battle_update");

    public static void send(final BattleHandle handle, final int currentSize, boolean exists) {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(handle.id());
        buf.writeBoolean(exists);
        buf.writeVarInt(currentSize);
        ClientPlayNetworking.send(IDENTIFIER, buf);
    }

    private RequestBattleSender() {
    }
}
