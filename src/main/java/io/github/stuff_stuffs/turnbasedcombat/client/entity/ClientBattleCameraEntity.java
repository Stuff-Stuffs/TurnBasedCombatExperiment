package io.github.stuff_stuffs.turnbasedcombat.client.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.entity.AbstractBattleCameraEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.PlayerMarkerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class ClientBattleCameraEntity extends AbstractBattleCameraEntity {
    public final Input input;
    private final PlayerMarkerEntity playerMarkerEntity;

    public ClientBattleCameraEntity(final EntityType<?> type, final World world, final UUID playerUuid, final Input input) {
        super(type, world, playerUuid);
        this.input = input;
        playerMarkerEntity = new PlayerMarkerEntity(world);
        ((ClientWorld) world).addEntity(Integer.MAX_VALUE, playerMarkerEntity);
    }

    @Override
    public void setRemoved(RemovalReason reason) {
        super.setRemoved(reason);
        playerMarkerEntity.discard();
    }

    @Override
    public void tick() {
        setPos(getX() + getVelocity().x, getY() + getVelocity().y, getZ() + getVelocity().z);
        final ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
        final double dist = getPos().distanceTo(playerEntity.getPos());
        if (dist > 32) {
            final Vec3d delta = getPos().subtract(playerEntity.getPos()).normalize();
            final Vec3d d = delta.multiply(dist - 32);
            final Vec3d p = getPos().subtract(d);
            setPos(p.x, p.y, p.z);
        }
        input.tick(false);
        final Vec3d forward = getRotationVector();
        final Vec3d up = new Vec3d(0, 1, 0);
        final Vec3d right = forward.crossProduct(up).normalize();
        setVelocity(forward.multiply(input.movementForward));
        setVelocity(getVelocity().add(right.multiply(-input.movementSideways)));
        double upVel = 0;
        upVel += input.jumping ? 1 : 0;
        upVel -= input.sneaking ? 1 : 0;
        setVelocity(getVelocity().add(up.multiply(upVel)));
        super.tick();
        updateTrackedPosition(getX(), getY(), getZ());
    }

    @Override
    public boolean shouldRender(final double distance) {
        return true;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return null;
    }
}
