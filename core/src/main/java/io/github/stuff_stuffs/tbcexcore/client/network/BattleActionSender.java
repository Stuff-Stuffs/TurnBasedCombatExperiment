package io.github.stuff_stuffs.tbcexcore.client.network;

import io.github.stuff_stuffs.tbcexcore.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleActionRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class BattleActionSender {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("battle_action");

    private BattleActionSender() {
    }

    public static void send(final BattleHandle handle, final BattleAction<?> action) {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.encode(BattleHandle.CODEC, handle);
        buf.encode(BattleActionRegistry.CODEC, action);
        ClientPlayNetworking.send(IDENTIFIER, buf);
    }
}
