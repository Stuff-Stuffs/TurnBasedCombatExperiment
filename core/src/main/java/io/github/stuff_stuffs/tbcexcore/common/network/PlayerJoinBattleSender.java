package io.github.stuff_stuffs.tbcexcore.common.network;

import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class PlayerJoinBattleSender {
    public static final Identifier IDENTIFIER = TBCExCore.createId("player_join_battle");

    private PlayerJoinBattleSender() {
    }

    public static void send(ServerPlayerEntity entity, BattleHandle handle) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(handle.id());
        ServerPlayNetworking.send(entity, IDENTIFIER, buf);
    }
}
