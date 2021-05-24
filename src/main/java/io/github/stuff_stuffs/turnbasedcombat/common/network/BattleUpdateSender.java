package io.github.stuff_stuffs.turnbasedcombat.common.network;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleTimelineView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooser;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.io.DataOutput;
import java.io.IOException;

public final class BattleUpdateSender {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("battle_update");

    public static void send(final ServerPlayerEntity player, final BattleHandle handle, final TurnChooser chooser, final int timelineSizeBefore, final BattleTimelineView view, final boolean fresh) {
        if (timelineSizeBefore != view.size()) {
            try {
                final PacketByteBuf buf = PacketByteBufs.create();
                buf.writeVarInt(handle.id());

                buf.writeVarInt(timelineSizeBefore);
                buf.writeVarInt(view.size() - timelineSizeBefore);
                buf.writeBoolean(fresh);
                final DataOutput output = new ByteBufOutputStream(buf);
                if (fresh) {
                    write(chooser.getType().codec.encodeStart(NbtOps.INSTANCE, chooser).getOrThrow(false, s -> {
                        throw new RuntimeException(s);
                    }), output);
                }
                for (int i = timelineSizeBefore; i < view.size(); i++) {
                    write(view.get(i).serialize(NbtOps.INSTANCE), output);
                }
                ServerPlayNetworking.send(player, IDENTIFIER, buf);
            } catch (final IOException e) {
                throw new EncoderException(e);
            }
        }
    }

    private static void write(final NbtElement element, final DataOutput output) throws IOException {
        output.writeByte(element.getType());
        if (element.getType() != 0) {
            output.writeUTF("");
            element.write(output);
        }
    }

    private BattleUpdateSender() {
    }
}
