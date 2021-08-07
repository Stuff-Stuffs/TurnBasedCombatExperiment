package io.github.stuff_stuffs.tbcexcore.client.network;

import io.github.stuff_stuffs.tbcexcore.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class RequestBattleSender {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("request_battle_update");

    private RequestBattleSender() {
    }

    public static void send(final BattleHandle handle, final int currentSize, boolean exists) {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(handle.id());
        buf.writeBoolean(exists);
        buf.writeVarInt(currentSize);
        ClientPlayNetworking.send(IDENTIFIER, buf);
    }
}
