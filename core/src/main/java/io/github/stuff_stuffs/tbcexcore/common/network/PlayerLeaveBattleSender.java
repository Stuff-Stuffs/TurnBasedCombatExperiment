package io.github.stuff_stuffs.tbcexcore.common.network;

import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class PlayerLeaveBattleSender {
    public static final Identifier IDENTIFIER = TBCExCore.createId("leave_battle");

    public static void send(final ServerPlayerEntity player, final Reason reason) {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumConstant(reason);
        ServerPlayNetworking.send(player, IDENTIFIER, buf);
    }

    public enum Reason {
        ENDED,
        LEFT
    }

    private PlayerLeaveBattleSender() {
    }
}
