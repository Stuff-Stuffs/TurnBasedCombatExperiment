package io.github.stuff_stuffs.turnbasedcombat.client.network;

import io.github.stuff_stuffs.turnbasedcombat.client.battle.data.ClientBattleWorld;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooser;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooserTypeRegistry;
import io.github.stuff_stuffs.turnbasedcombat.common.network.BattleUpdateSender;
import io.netty.buffer.ByteBufInputStream;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;

public final class BattleUpdateReceiver {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(BattleUpdateSender.IDENTIFIER, BattleUpdateReceiver::receive);
    }

    private static void receive(final MinecraftClient client, final ClientPlayNetworkHandler clientPlayNetworkHandler, final PacketByteBuf buf, final PacketSender packetSender) {
        try {
            final int handleId = buf.readVarInt();
            final int timelineSizeBefore = buf.readVarInt();
            final int delta = buf.readVarInt();
            final boolean fresh = buf.readBoolean();
            if (delta != 0) {
                final List<BattleAction> actions = new ReferenceArrayList<>(delta);
                final DataInput input = new ByteBufInputStream(buf);
                if (fresh) {
                    final TurnChooser chooser = TurnChooserTypeRegistry.CODEC.parse(NbtOps.INSTANCE, read(input, 0, NbtTagSizeTracker.EMPTY)).getOrThrow(false, s -> {
                        throw new RuntimeException(s);
                    });
                    for (int i = 0; i < delta; i++) {
                        actions.add(BattleAction.deserialize(read(input, 0, NbtTagSizeTracker.EMPTY), NbtOps.INSTANCE));
                    }
                    client.execute(() -> {
                        final BattleHandle handle = new BattleHandle(handleId);
                        final Battle battle = ClientBattleWorld.get(client.world).create(handle, chooser);
                        for (final BattleAction action : actions) {
                            battle.push(action);
                        }
                    });
                } else {
                    for (int i = 0; i < delta; i++) {
                        actions.add(BattleAction.deserialize(read(input, 0, NbtTagSizeTracker.EMPTY), NbtOps.INSTANCE));
                    }
                    client.execute(() -> {
                        final BattleHandle handle = new BattleHandle(handleId);
                        final Battle battle = ClientBattleWorld.get(client.world).getBattle(handle);
                        if (battle == null) {
                            throw new RuntimeException();
                        }
                        battle.trimToSize(timelineSizeBefore);
                        for (final BattleAction action : actions) {
                            battle.push(action);
                        }
                    });
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static NbtElement read(final DataInput input, final int depth, final NbtTagSizeTracker tracker) throws IOException {
        final byte b = input.readByte();
        if (b == 0) {
            return NbtNull.INSTANCE;
        } else {
            input.readUTF();
            try {
                return NbtTypes.byId(b).read(input, depth, tracker);
            } catch (final IOException var7) {
                final CrashReport crashReport = CrashReport.create(var7, "Loading NBT data");
                final CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
                crashReportSection.add("Tag type", b);
                throw new CrashException(crashReport);
            }
        }
    }

    private BattleUpdateReceiver() {
    }
}
