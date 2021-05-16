package io.github.stuff_stuffs.turnbasedcombat.common.impl.api;

import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleLog;
import io.github.stuff_stuffs.turnbasedcombat.common.network.sender.UpdateBattleLog;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class ServerBattleLog implements BattleLog {
    private final List<BattleAction> actions;
    private final Reference2IntMap<ServerPlayerEntity> playerIndexes;

    public ServerBattleLog() {
        actions = new ObjectArrayList<>();
        playerIndexes = new Reference2IntOpenHashMap<>();
        playerIndexes.defaultReturnValue(0);
    }

    public void push(final BattleAction battleAction) {
        actions.add(battleAction);
    }

    public void updatePlayer(final ServerPlayerEntity entity, BattleHandle handle) {
        final int lastUpdate = playerIndexes.getInt(entity);
        if (lastUpdate != size()) {
            final PacketByteBuf buf = PacketByteBufs.create();
            handle.toBuf(buf);
            buf.writeVarInt(size() - lastUpdate);
            for (int i = lastUpdate; i < size(); i++) {
                actions.get(i).toBuf(buf);
            }
            UpdateBattleLog.send(entity, buf);
            playerIndexes.put(entity, size());
        }
    }

    public void toBuf(PacketByteBuf buf) {
        buf.writeVarInt(actions.size());
        for (BattleAction action : actions) {
            action.toBuf(buf);
        }
    }

    @Override
    public int size() {
        return actions.size();
    }

    @Override
    public BattleAction getAction(final int index) {
        return actions.get(index);
    }
}
