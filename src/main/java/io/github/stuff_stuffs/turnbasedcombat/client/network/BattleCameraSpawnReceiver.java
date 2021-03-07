package io.github.stuff_stuffs.turnbasedcombat.client.network;

import io.github.stuff_stuffs.turnbasedcombat.client.entity.ClientBattleCameraEntity;
import io.github.stuff_stuffs.turnbasedcombat.client.entity.OtherClientBattleCameraEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.AbstractBattleCameraEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.EntityTypes;
import io.github.stuff_stuffs.turnbasedcombat.common.network.BattleCameraSpawnS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;

public final class BattleCameraSpawnReceiver {

    private BattleCameraSpawnReceiver() {
    }

    public static void receive(final MinecraftClient client, final ClientPlayNetworkHandler clientPlayNetworkHandler, final PacketByteBuf buf, final PacketSender packetSender) {
        final BattleCameraSpawnS2CPacket packet = new BattleCameraSpawnS2CPacket(buf);
        final ClientPlayerEntity player = client.player;
        final ClientWorld world = client.world;
        if (player == null || world == null) {
            throw new NullPointerException();
        }
        final AbstractBattleCameraEntity cameraEntity;
        boolean current = packet.playerUuid.equals(player.getUuid());
        if (current) {
            cameraEntity = new ClientBattleCameraEntity(EntityTypes.BATTLE_CAMERA_ENTITY_TYPE, world, packet.playerUuid, client.player.input);
        } else {
            cameraEntity = new OtherClientBattleCameraEntity(EntityTypes.BATTLE_CAMERA_ENTITY_TYPE, world, packet.playerUuid);
        }
        cameraEntity.setPos(packet.x, packet.y, packet.z);
        cameraEntity.setEntityId(packet.id);
        cameraEntity.setVelocity(packet.velocityX, packet.velocityY, packet.velocityZ);
        cameraEntity.pitch = packet.pitch;
        cameraEntity.yaw = packet.yaw;
        world.addEntity(packet.id, cameraEntity);
    }
}
