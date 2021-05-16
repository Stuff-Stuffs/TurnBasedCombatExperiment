package io.github.stuff_stuffs.turnbasedcombat.common.network.sender;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class MovementAbility {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("disabled_movement");

    public static void send(final PlayerEntity entity, final boolean enabled) {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(enabled);
        ServerPlayNetworking.send((ServerPlayerEntity) entity, IDENTIFIER, buf);
    }

    private MovementAbility() {
    }
}
