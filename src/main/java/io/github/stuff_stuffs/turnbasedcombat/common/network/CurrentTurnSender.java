package io.github.stuff_stuffs.turnbasedcombat.common.network;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class CurrentTurnSender {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("current_turn");

    public static void send(final ServerPlayerEntity entity, final boolean turn) {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(turn);
        ServerPlayNetworking.send(entity, IDENTIFIER, buf);
    }

    private CurrentTurnSender() {
    }
}
