package io.github.stuff_stuffs.turnbasedcombat.common.network;

import io.github.stuff_stuffs.turnbasedcombat.client.network.RequestBattleSender;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.mixin.api.BattleWorldSupplier;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public final class RequestBattleReceiver {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(RequestBattleSender.IDENTIFIER, RequestBattleReceiver::receive);
    }

    private static void receive(final MinecraftServer minecraftServer, final ServerPlayerEntity playerEntity, final ServerPlayNetworkHandler serverPlayNetworkHandler, final PacketByteBuf buf, final PacketSender packetSender) {
        final BattleHandle handle = new BattleHandle(buf.readVarInt());
        final boolean exists = buf.readBoolean();
        final int currentSize = buf.readVarInt();
        final Battle battle = ((BattleWorldSupplier) playerEntity.world).tbcex_getBattleWorld().getBattle(handle);
        if (battle != null) {
            BattleUpdateSender.send(handle, battle, currentSize, exists, playerEntity);
        }
    }

    private RequestBattleReceiver() {
    }
}
