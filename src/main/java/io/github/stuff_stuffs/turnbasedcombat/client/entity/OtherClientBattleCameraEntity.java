package io.github.stuff_stuffs.turnbasedcombat.client.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.entity.AbstractBattleCameraEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

import java.util.UUID;

public class OtherClientBattleCameraEntity extends AbstractBattleCameraEntity {

    public OtherClientBattleCameraEntity(EntityType<?> type, World world, UUID playerUuid) {
        super(type, world, playerUuid);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return null;
    }
}
