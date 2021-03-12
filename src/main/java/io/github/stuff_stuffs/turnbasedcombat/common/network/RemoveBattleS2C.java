package io.github.stuff_stuffs.turnbasedcombat.common.network;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public final class RemoveBattleS2C {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("remove_battle");

    public static void removeBattle(final BattleHandle handle, final ServerPlayerEntity entity) {
        final PacketByteBuf buf = PacketByteBufs.create();
        handle.toBuf(buf);
        for (final ServerPlayerEntity playerEntity : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(playerEntity, IDENTIFIER, buf);
        }
    }

    public static void removeBattle(final BattleHandle handle, final ServerWorld world) {
        final PacketByteBuf buf = PacketByteBufs.create();
        handle.toBuf(buf);
        for (final ServerPlayerEntity playerEntity : PlayerLookup.world(world)) {
            ServerPlayNetworking.send(playerEntity, IDENTIFIER, buf);
        }
    }

    public static void removeBattleFromPlayerWorld(final BattleHandle handle, final ServerPlayerEntity playerEntity) {
        final PacketByteBuf buf = PacketByteBufs.create();
        handle.toBuf(buf);
        ServerPlayNetworking.send(playerEntity, IDENTIFIER, buf);
    }

    private RemoveBattleS2C() {
    }
}
