package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.entity.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.util.CodecUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public final class WorldEntityInfo {
    public static final Codec<WorldEntityInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("player").forGetter(info -> info.player),
            CodecUtil.NBT_CODEC.fieldOf("nbt").forGetter(info -> info.nbt),
            CodecUtil.VEC3D_CODEC.fieldOf("position").forGetter(info -> info.position)
    ).apply(instance, WorldEntityInfo::new));

    private final boolean player;
    private final NbtElement nbt;
    private final Vec3d position;

    private WorldEntityInfo(final boolean player, final NbtElement nbt, final Vec3d position) {
        this.player = player;
        this.nbt = nbt;
        this.position = position;
    }

    public WorldEntityInfo(final BattleEntity entity) {
        player = entity instanceof PlayerEntity;
        nbt = new NbtCompound();
        if (!player) {
            ((Entity) entity).saveNbt((NbtCompound) nbt);
        }
        position = ((Entity) entity).getPos();
    }

    public boolean isPlayer() {
        return player;
    }

    public void spawnIntoWorld(final ServerWorld world, final EntityStateView entityState) {
        final Entity entity;
        if (!player) {
            entity = EntityType.loadEntityWithPassengers((NbtCompound) nbt, world, e -> {
                e.refreshPositionAndAngles(position.x, position.y, position.z, 0, 0);
                return e;
            });
        } else {
            entity = world.getEntity(entityState.getHandle().participantId());
        }
        if (entity != null) {
            entity.setPosition(position);
            ((BattleEntity) entity).onLeaveBattle(entityState);
        }
    }
}
