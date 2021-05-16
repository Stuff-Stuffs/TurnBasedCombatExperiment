package io.github.stuff_stuffs.turnbasedcombat.common.network.sender;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public final class RemoveBattle {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("remove_battle");

    public static void send(final ServerWorld world, final BattleHandle handle) {
        final PacketByteBuf buf = PacketByteBufs.create();
        handle.toBuf(buf);
        for (final ServerPlayerEntity entity : PlayerLookup.world(world)) {
            ServerPlayNetworking.send(entity, IDENTIFIER, buf);
        }
    }

    private RemoveBattle() {
    }
}
