package io.github.stuff_stuffs.turnbasedcombat.client.network;

import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.api.ClientBattleImpl;
import io.github.stuff_stuffs.turnbasedcombat.common.impl.component.ClientBattleWorldComponent;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public final class AddBattleReceiver {

    private AddBattleReceiver() {
    }

    public static void receive(final MinecraftClient client, final ClientPlayNetworkHandler clientPlayNetworkHandler, final PacketByteBuf buf, final PacketSender packetSender) {
        final int playerId = buf.readVarInt();
        final int entities = buf.readVarInt();
        final IntSet ids = new IntOpenHashSet(entities);
        for (int i = 0; i < entities; i++) {
            ids.add(buf.readVarInt());
        }
        final BattleHandle handle = BattleHandle.fromBuf(buf);
        if (client.world != null && client.world.getRegistryKey().equals(handle.getDimension())) {
            final ClientBattleWorldComponent component = (ClientBattleWorldComponent) Components.BATTLE_WORLD_COMPONENT_KEY.get(client.world);
            component.addBattle(handle, new ClientBattleImpl(handle, playerId, ids, client.world));
        }
    }
}
