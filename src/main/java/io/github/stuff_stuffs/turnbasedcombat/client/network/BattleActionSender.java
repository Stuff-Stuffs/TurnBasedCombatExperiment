package io.github.stuff_stuffs.turnbasedcombat.client.network;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.network.BattleActionReceiver;
import io.netty.buffer.ByteBufOutputStream;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;

import java.io.DataOutput;
import java.io.IOException;

public final class BattleActionSender {
    public static void send(final BattleAction action) {
        final PacketByteBuf buf = PacketByteBufs.create();
        try {
            write(action.serialize(NbtOps.INSTANCE), new ByteBufOutputStream(buf));
            ClientPlayNetworking.send(BattleActionReceiver.IDENTIFIER, buf);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(final NbtElement element, final DataOutput output) throws IOException {
        output.writeByte(element.getType());
        if (element.getType() != 0) {
            output.writeUTF("");
            element.write(output);
        }
    }

    private BattleActionSender() {
    }
}
