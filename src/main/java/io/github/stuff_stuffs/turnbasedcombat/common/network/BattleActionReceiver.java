package io.github.stuff_stuffs.turnbasedcombat.common.network;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.data.ServerBattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.persistant.BattlePersistentState;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class BattleActionReceiver {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("battle_action");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(IDENTIFIER, BattleActionReceiver::receive);
    }

    private static void receive(final MinecraftServer minecraftServer, final ServerPlayerEntity serverPlayerEntity, final ServerPlayNetworkHandler serverPlayNetworkHandler, final PacketByteBuf packetByteBuf, final PacketSender packetSender) {
        final ServerBattleWorld battleWorld = BattlePersistentState.get(serverPlayerEntity.getServerWorld().getPersistentStateManager()).getData();
        final Battle battle = battleWorld.getBattle((BattleEntity) serverPlayerEntity);
        if (battle != null) {
            battle.push(BattleAction.deserialize(packetByteBuf.readUnlimitedNbt(), NbtOps.INSTANCE));
        }
    }

    private BattleActionReceiver() {
    }
}
