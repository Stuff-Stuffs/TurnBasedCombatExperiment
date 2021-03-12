package io.github.stuff_stuffs.turnbasedcombat.common.network;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.api.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ServerBattleImpl;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public final class AddBattleS2C {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("add_battle");

    public static void sendBattle(final Battle battle, final ServerPlayerEntity entity) {
        final PacketByteBuf buf = battle.toBuf();
        for (final ServerPlayerEntity playerEntity : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(playerEntity, IDENTIFIER, buf);
        }
    }

    public static void sendBattle(final ServerBattleImpl battle, final ServerWorld world) {
        final PacketByteBuf buf = battle.toBuf();
        for (final ServerPlayerEntity playerEntity : PlayerLookup.world(world)) {
            ServerPlayNetworking.send(playerEntity, IDENTIFIER, buf);
        }
    }

    public static void sendBattleToPlayer(final ServerBattleImpl battle, final ServerPlayerEntity player) {
        final PacketByteBuf buf = battle.toBuf();
        ServerPlayNetworking.send(player, IDENTIFIER, buf);
    }

    private AddBattleS2C() {
    }
}
