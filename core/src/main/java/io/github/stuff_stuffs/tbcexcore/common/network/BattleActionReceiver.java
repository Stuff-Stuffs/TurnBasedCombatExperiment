package io.github.stuff_stuffs.tbcexcore.common.network;

import io.github.stuff_stuffs.tbcexcore.client.network.BattleActionSender;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleActionRegistry;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public final class BattleActionReceiver {
    private BattleActionReceiver() {
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(BattleActionSender.IDENTIFIER, BattleActionReceiver::receive);
    }

    private static void receive(final MinecraftServer minecraftServer, final ServerPlayerEntity entity, final ServerPlayNetworkHandler serverPlayNetworkHandler, final PacketByteBuf packetByteBuf, final PacketSender packetSender) {
        final BattleHandle handle = packetByteBuf.decode(BattleHandle.CODEC);
        final BattleAction<?> action = packetByteBuf.decode(BattleActionRegistry.CODEC);
        minecraftServer.execute(() -> {
            final Battle battle = ((BattleWorldSupplier) entity.world).tbcex_getBattleWorld().getBattle(handle);
            if (battle == null) {
                LoggerUtil.LOGGER.error("Missing battle {}", handle);
            } else {
                //TODO turn verification
                battle.push(action);
            }
        });
    }
}
