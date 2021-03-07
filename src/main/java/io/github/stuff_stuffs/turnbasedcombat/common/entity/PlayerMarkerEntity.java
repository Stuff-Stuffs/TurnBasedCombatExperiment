package io.github.stuff_stuffs.turnbasedcombat.common.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class PlayerMarkerEntity extends Entity {
    public PlayerMarkerEntity(final World world) {
        super(EntityTypes.PLAYER_MARKER_ENTITY_TYPE, world);
    }

    @Override
    public void tick() {
        if (world.isClient()) {
            final ClientPlayerEntity player = MinecraftClient.getInstance().player;
            setPos(player.getX(), player.getY(), player.getZ());
        } else {
            discard();
        }
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
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public Box getVisibilityBoundingBox() {
        return MinecraftClient.getInstance().player.getVisibilityBoundingBox();
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return null;
    }
}
