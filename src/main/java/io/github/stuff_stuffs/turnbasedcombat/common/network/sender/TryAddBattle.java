package io.github.stuff_stuffs.turnbasedcombat.common.network.sender;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.api.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ServerBattleLog;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.Collection;

public final class TryAddBattle {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("new_battle");

    public static void send(final ServerWorld world, final Battle battle, final ServerBattleLog log) {
        final PacketByteBuf buf = PacketByteBufs.create();
        battle.getHandle().toBuf(buf);
        battle.getBounds().toBuf(buf);
        final Collection<ServerPlayerEntity> entities = PlayerLookup.world(world);
        for (final ServerPlayerEntity entity : entities) {
            ServerPlayNetworking.send(entity, IDENTIFIER, buf);
        }
        for (final ServerPlayerEntity entity : entities) {
            log.updatePlayer(entity, battle.getHandle());
        }
    }

    public static void send(final ServerPlayerEntity entity, final Battle battle, final ServerBattleLog log) {
        final PacketByteBuf buf = PacketByteBufs.create();
        battle.getHandle().toBuf(buf);
        battle.getBounds().toBuf(buf);
        ServerPlayNetworking.send(entity, IDENTIFIER, buf);
        log.updatePlayer(entity, battle.getHandle());
    }

    private TryAddBattle() {
    }
}
