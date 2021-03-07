package io.github.stuff_stuffs.turnbasedcombat.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

import java.util.UUID;

public abstract class AbstractBattleCameraEntity extends Entity {
    private final UUID playerUuid;

    public AbstractBattleCameraEntity(final EntityType<?> type, final World world, final UUID playerUuid) {
        super(type, world);
        this.playerUuid = playerUuid;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(final CompoundTag tag) {

    }

    @Override
    protected void writeCustomDataToNbt(final CompoundTag tag) {

    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public boolean isSpectator() {
        return true;
    }

    @Override
    public boolean isLogicalSideForUpdatingMovement() {
        return world.isClient();
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }
}
