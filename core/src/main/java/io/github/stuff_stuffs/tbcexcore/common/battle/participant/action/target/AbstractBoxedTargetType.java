package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;

public abstract class AbstractBoxedTargetType<T> implements TargetType {
    protected final BiFunction<Battle, BattleParticipantHandle, Iterable<Pair<Box, BiFunction<Battle, BattleParticipantHandle, TargetInstance>>>> boxFunction;
    protected final T source;

    protected AbstractBoxedTargetType(final T source) {
        boxFunction = createFunc(source);
        this.source = source;
    }

    protected abstract BiFunction<Battle, BattleParticipantHandle, Iterable<Pair<Box, BiFunction<Battle, BattleParticipantHandle, TargetInstance>>>> createFunc(T source);

    @Override
    public @Nullable TargetInstance find(final Vec3d pos, final Vec3d direction, final BattleParticipantHandle user, final Battle battle) {
        double minDistSq = Double.POSITIVE_INFINITY;
        TargetInstance closest = null;
        final Vec3d end = pos.add(direction.multiply(64));
        for (final Pair<Box, BiFunction<Battle, BattleParticipantHandle, TargetInstance>> boxPair : boxFunction.apply(battle, user)) {
            final Box box = boxPair.getFirst();
            if (minDistSq(pos, box) < minDistSq) {
                final Optional<Vec3d> raycast = box.raycast(pos, end);
                if (raycast.isPresent()) {
                    final double distSq = pos.squaredDistanceTo(raycast.get());
                    if (distSq < minDistSq) {
                        minDistSq = distSq;
                        closest = boxPair.getSecond().apply(battle, user);
                    }
                }
            }
        }
        return closest;
    }

    private static double minDistSq(final Vec3d pos, final Box box) {
        final Vec3d center = box.getCenter();
        final Direction.AxisDirection x = pos.x >= center.x ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        final Direction.AxisDirection y = pos.y >= center.y ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        final Direction.AxisDirection z = pos.z >= center.z ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        return pos.squaredDistanceTo(getPoint(box, Direction.Axis.X, x), getPoint(box, Direction.Axis.Y, y), getPoint(box, Direction.Axis.Z, z));
    }

    private static double getPoint(final Box box, final Direction.Axis axis, final Direction.AxisDirection direction) {
        if (direction == Direction.AxisDirection.POSITIVE) {
            return box.getMax(axis);
        } else {
            return box.getMin(axis);
        }
    }
}
