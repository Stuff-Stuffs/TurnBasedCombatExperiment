package io.github.stuff_stuffs.turnbasedcombat.client.network;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public final class RequestBattleUpdateSender {
    public static final Identifier BATTLE_UPDATE = TurnBasedCombatExperiment.createId("request_battle_update");
    public static final Identifier ENTITY_BATTLE_UPDATE = TurnBasedCombatExperiment.createId("request_battle_update_entity");

    public static void send(final BattleHandle handle, final int timelineSize, final boolean fresh) {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(handle.id());
        buf.writeVarInt(timelineSize);
        buf.writeBoolean(fresh);
        ClientPlayNetworking.send(BATTLE_UPDATE, buf);
    }

    public static void sendEntity(final UUID id) {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(id);
        ClientPlayNetworking.send(ENTITY_BATTLE_UPDATE, buf);
    }

    private RequestBattleUpdateSender() {
    }
}
