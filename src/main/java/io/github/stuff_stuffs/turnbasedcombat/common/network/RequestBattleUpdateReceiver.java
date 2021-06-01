package io.github.stuff_stuffs.turnbasedcombat.common.network;

import io.github.stuff_stuffs.turnbasedcombat.client.network.RequestBattleUpdateSender;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.data.ServerBattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.common.persistant.BattlePersistentState;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public final class RequestBattleUpdateReceiver {


    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(RequestBattleUpdateSender.BATTLE_UPDATE, RequestBattleUpdateReceiver::receiver);
        ServerPlayNetworking.registerGlobalReceiver(RequestBattleUpdateSender.ENTITY_BATTLE_UPDATE, RequestBattleUpdateReceiver::receiverEntity);
    }

    private static void receiver(final MinecraftServer minecraftServer, final ServerPlayerEntity entity, final ServerPlayNetworkHandler serverPlayNetworkHandler, final PacketByteBuf buf, final PacketSender packetSender) {
        final int battleId = buf.readVarInt();
        final int timelineSize = buf.readVarInt();
        final boolean fresh = buf.readBoolean();
        final ServerWorld world = entity.getServerWorld();
        minecraftServer.execute(() -> BattlePersistentState.get(world.getPersistentStateManager(), world).getData().updateClient(entity, new BattleHandle(battleId), timelineSize, fresh));
    }

    private static void receiverEntity(final MinecraftServer minecraftServer, final ServerPlayerEntity entity, final ServerPlayNetworkHandler serverPlayNetworkHandler, final PacketByteBuf buf, final PacketSender packetSender) {
        final UUID id = buf.readUuid();
        ServerWorld world = entity.getServerWorld();
        minecraftServer.execute(() -> {
            final ServerBattleWorld data = BattlePersistentState.get(world.getPersistentStateManager(), world).getData();
            final Battle battle = data.getBattle(id);
            if(battle!=null) {
                data.updateClient(entity, battle.getBattleId(), 0, true);
            }
        });
    }

    private RequestBattleUpdateReceiver() {
    }
}
