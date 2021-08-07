package io.github.stuff_stuffs.tbcexcore.common.network;

import io.github.stuff_stuffs.tbcexcore.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleActionRegistry;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

import java.io.DataOutput;
import java.io.IOException;

public final class BattleUpdateSender {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("battle_update");
    private static final Logger LOGGER = TurnBasedCombatExperiment.LOGGER;

    public static void send(final BattleHandle handle, final Battle battle, final int sizeBefore, final boolean existing, final ServerPlayerEntity playerEntity) {
        final PacketByteBuf buf = PacketByteBufs.create();
        try {
            buf.writeVarInt(handle.id());
            buf.writeBoolean(existing);
            if (!existing) {
                battle.getState().getBounds().asStream().forEach(buf::writeInt);
            }
            buf.writeVarInt(sizeBefore);
            buf.writeVarInt(battle.getTimeline().getSize() - sizeBefore);
            final DataOutput output = new ByteBufOutputStream(buf);
            for (int i = sizeBefore; i < battle.getTimeline().getSize(); i++) {
                write(BattleActionRegistry.CODEC.encodeStart(NbtOps.INSTANCE, battle.getTimeline().get(i)).getOrThrow(false, s -> {
                    throw new EncoderException(s);
                }), output);
            }
            ServerPlayNetworking.send(playerEntity, IDENTIFIER, buf);
        } catch (final IOException e) {
            LOGGER.error("Could not write BattleUpdate packet: {}", e.getMessage());
            buf.release();
        } catch (final EncoderException e) {
            LOGGER.error("Could not write BattleUpdate packet, encode error: {}", e.getMessage());
            buf.release();
        }
    }

    private static void write(final NbtElement element, final DataOutput output) throws IOException {
        output.writeByte(element.getType());
        if (element.getType() != 0) {
            output.writeUTF("");
            element.write(output);
        }
    }
}
