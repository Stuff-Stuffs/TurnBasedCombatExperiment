package io.github.stuff_stuffs.tbcexcore.common.battle.participant;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleTimelineView;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.function.Function;

public class ParticipantRestoreData {
    public static final Codec<ParticipantRestoreData> CODEC = Codec.either(CodecUtil.NBT_CODEC.xmap(nbt -> (NbtCompound) nbt, Function.identity()), Codec.pair(CodecUtil.UUID_CODEC, CodecUtil.VEC3D_CODEC)).xmap(either -> new ParticipantRestoreData(either), data -> data.restoreData);
    private final Either<NbtCompound, Pair<UUID, Vec3d>> restoreData;

    public ParticipantRestoreData(final BattleHandle handle, final BattleEntity entity) {
        if (entity.onBattleJoin(handle)) {
            final NbtCompound compound = new NbtCompound();
            ((Entity) entity).saveNbt(compound);
            restoreData = Either.left(compound);
        } else {
            restoreData = Either.right(Pair.of(((Entity) entity).getUuid(), ((Entity) entity).getPos()));
        }
    }

    private ParticipantRestoreData(final Either<NbtCompound, Pair<UUID, Vec3d>> restoreData) {
        this.restoreData = restoreData;
    }

    public void restore(final World world, final BattleTimelineView actions) {
        restoreData.ifLeft(c -> {
            final Entity e = EntityType.loadEntityWithPassengers(c, world, Function.identity());
            if (e instanceof BattleEntity) {
                ((BattleEntity) e).onBattleEnd(actions);
            }
        }).ifRight(vec -> {
            if (world instanceof ServerWorld serverWorld) {
                final Entity entity = serverWorld.getEntity(vec.getFirst());
                if (entity != null) {
                    entity.setPos(vec.getSecond().x, vec.getSecond().y, vec.getSecond().z);
                    if (entity instanceof BattleEntity) {
                        ((BattleEntity) entity).onBattleEnd(actions);
                    }
                }
            }
        });
    }
}
