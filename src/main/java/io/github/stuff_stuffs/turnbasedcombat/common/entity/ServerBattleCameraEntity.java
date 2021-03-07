package io.github.stuff_stuffs.turnbasedcombat.common.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.network.BattleCameraSpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.UUID;

public class ServerBattleCameraEntity extends AbstractBattleCameraEntity {
    public ServerBattleCameraEntity(EntityType<?> type, World world, UUID playerUuid) {
        super(type, world, playerUuid);
    }

    @Override
    public void tick() {
        super.tick();
        ServerWorld serverWorld = (ServerWorld) world;
        final Entity entity = serverWorld.getEntity(getPlayerUuid());
        if(entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) entity).setCameraEntity(this);
        } else {
            discard();
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new BattleCameraSpawnS2CPacket(this).toPacket();
    }
}
