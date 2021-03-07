package io.github.stuff_stuffs.turnbasedcombat.common.network;

import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.ServerBattleCameraEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class BattleCameraSpawnS2CPacket {
    public static final Identifier IDENTIFIER = TurnBasedCombatExperiment.createId("spawn_battle_camera");
    public final UUID playerUuid;
    public final int id;
    public final double x;
    public final double y;
    public final double z;
    public final double velocityX;
    public final double velocityY;
    public final double velocityZ;
    public final float pitch;
    public final float yaw;


    public BattleCameraSpawnS2CPacket(final ServerBattleCameraEntity entity) {
        playerUuid = entity.getPlayerUuid();
        id = entity.getId();
        x = entity.getX();
        y = entity.getY();
        z = entity.getZ();
        velocityX = entity.getVelocity().x;
        velocityY = entity.getVelocity().y;
        velocityZ = entity.getVelocity().z;
        pitch = entity.pitch;
        yaw = entity.yaw;
    }

    public BattleCameraSpawnS2CPacket(final PacketByteBuf buf) {
        playerUuid = buf.readUuid();
        id = buf.readVarInt();
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        velocityX = buf.readDouble();
        velocityY = buf.readDouble();
        velocityZ = buf.readDouble();
        pitch = buf.readFloat();
        yaw = buf.readFloat();
    }

    public Packet<?> toPacket() {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(playerUuid);
        buf.writeVarInt(id);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(velocityX);
        buf.writeDouble(velocityY);
        buf.writeDouble(velocityZ);
        buf.writeFloat(pitch);
        buf.writeFloat(yaw);
        return ServerPlayNetworking.createS2CPacket(IDENTIFIER, buf);
    }
}
