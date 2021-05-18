package io.github.stuff_stuffs.turnbasedcombat.common.network;

import io.github.stuff_stuffs.turnbasedcombat.client.network.RequestBattleUpdateSender;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.persistant.BattlePersistentState;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public final class RequestBattleUpdateReceiver {


    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(RequestBattleUpdateSender.IDENTIFIER, RequestBattleUpdateReceiver::receiver);
    }

    private static void receiver(final MinecraftServer minecraftServer, final ServerPlayerEntity entity, final ServerPlayNetworkHandler serverPlayNetworkHandler, final PacketByteBuf buf, final PacketSender packetSender) {
        final int battleId = buf.readVarInt();
        final int timelineSize = buf.readVarInt();
        final ServerWorld world = entity.getServerWorld();
        minecraftServer.execute(() -> BattlePersistentState.get(world.getPersistentStateManager()).getData().updateClient(entity, new BattleHandle(battleId), timelineSize));
    }

    private RequestBattleUpdateReceiver() {
    }
}
