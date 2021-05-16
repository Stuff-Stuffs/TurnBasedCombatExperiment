package io.github.stuff_stuffs.turnbasedcombat.common.network.sender;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class UpdateBattleLog {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("update_battle_log");

    public static void send(final ServerPlayerEntity entity, final PacketByteBuf buf) {
        ServerPlayNetworking.send(entity, IDENTIFIER, buf);
    }

    private UpdateBattleLog() {
    }
}
