package io.github.stuff_stuffs.tbcexcore.client.network;

import io.github.stuff_stuffs.tbcexcore.client.TurnBasedCombatExperimentClient;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleActionRegistry;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleBounds;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.ClientBattleWorld;
import io.github.stuff_stuffs.tbcexcore.common.network.BattleUpdateSender;
import io.github.stuff_stuffs.tbcexcore.mixin.api.ClientBattleWorldSupplier;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.DecoderException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import org.apache.logging.log4j.Logger;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class BattleUpdateReceiver {
    private static final Logger LOGGER = TurnBasedCombatExperimentClient.LOGGER;

    private BattleUpdateReceiver() {
    }

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(BattleUpdateSender.IDENTIFIER, BattleUpdateReceiver::receive);
    }

    private static void receive(final MinecraftClient minecraftClient, final ClientPlayNetworkHandler clientPlayNetworkHandler, final PacketByteBuf buf, final PacketSender packetSender) {
        try {
            final BattleHandle handle = new BattleHandle(buf.readVarInt());
            final boolean existing = buf.readBoolean();
            int turnTimerRemaining = buf.readVarInt();
            int turnTimerMax = buf.readVarInt();
            BattleBounds bounds = null;
            if (!existing) {
                bounds = new BattleBounds(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
            }
            final int trimmedSize = buf.readVarInt();
            final int length = buf.readVarInt();
            final DataInput input = new ByteBufInputStream(buf);
            final List<BattleAction<?>> actions = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                actions.add(BattleActionRegistry.CODEC.parse(NbtOps.INSTANCE, read(input, 0, NbtTagSizeTracker.EMPTY)).getOrThrow(false, s -> {
                    throw new DecoderException(s);
                }));
            }
            final ClientBattleWorld battleWorld = ((ClientBattleWorldSupplier) minecraftClient.world).tbcex_getBattleWorld();
            if (!existing) {
                battleWorld.addBattle(handle, bounds, turnTimerRemaining, turnTimerMax);
            }
            battleWorld.update(handle, trimmedSize, actions, turnTimerRemaining, turnTimerMax);
        } catch (final IOException e) {
            LOGGER.error("Error reading nbt in BattleAction decoding: {}", e.getMessage());
        } catch (final DecoderException e) {
            LOGGER.error("Error in BattleAction decoding: {}", e.getMessage());
        }
    }

    private static NbtElement read(final DataInput input, final int depth, final NbtTagSizeTracker tracker) throws IOException {
        final byte b = input.readByte();
        if (b == 0) {
            return NbtNull.INSTANCE;
        } else {
            input.readUTF();
            return NbtTypes.byId(b).read(input, depth, tracker);
        }
    }
}
