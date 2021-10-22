package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target;

import io.github.stuff_stuffs.tbcexcore.client.TBCExCoreClient;
import io.github.stuff_stuffs.tbcexcore.client.render.BoxInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ParticipantTargetType implements TargetType {
    private final BiFunction<BattleStateView, BattleParticipantHandle, Iterable<BattleParticipantHandle>> source;

    public ParticipantTargetType(final BiFunction<BattleStateView, BattleParticipantHandle, Iterable<BattleParticipantHandle>> source) {
        this.source = source;
    }

    @Override
    public @Nullable TargetInstance find(final Vec3d pos, final Vec3d direction, final BattleParticipantHandle user, final Battle battle) {
        final BattleStateView battleState = battle.getState();
        final Iterable<BattleParticipantHandle> targets = source.apply(battleState, user);
        double closestDistance = Double.MAX_VALUE;
        TargetInstance closest = null;

        final Vec3d end = pos.add(direction.multiply(64));
        for (final BattleParticipantHandle target : targets) {
            final BattleParticipantStateView targetState = battleState.getParticipant(target);
            if (targetState == null) {
                throw new TBCExException("missing battle participant in battle");
            }
            final BattleParticipantBounds bounds = targetState.getBounds();
            if (checkDistance(bounds, pos, closestDistance)) {
                final BattleParticipantBounds.RaycastResult raycast = bounds.raycast(pos, end);
                if (raycast != null && raycast.hitPoint().squaredDistanceTo(pos) < closestDistance) {
                    closestDistance = raycast.hitPoint().squaredDistanceTo(pos);
                    closest = new ParticipantTargetInstance(target, closestDistance, this, raycast.part());
                }
            }
        }
        return closest;
    }

    private static boolean checkDistance(final BattleParticipantBounds bounds, final Vec3d pos, final double distance) {
        for (final BattleParticipantBounds.Part part : bounds) {
            if (AbstractBoxedTargetType.minDistSq(pos, part.box) <= distance) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(@Nullable final TargetInstance hovered, final List<TargetInstance> targeted, final BattleParticipantHandle user, final BattleStateView battle, final float tickDelta) {
        final Iterable<BattleParticipantHandle> locations = source.apply(battle, user);
        final boolean inst = hovered != null && hovered.getType() == this && hovered instanceof ParticipantTargetInstance;
        for (final BattleParticipantHandle targetHandle : locations) {
            final BattleParticipantStateView target = battle.getParticipant(targetHandle);
            if (target == null) {
                throw new TBCExException("missing battle participant in battle");
            }

            final boolean targetedTarget = inst && targetHandle.equals(((ParticipantTargetInstance) hovered).getHandle());

            final BattleParticipantBounds bounds = target.getBounds();
            for (final BattleParticipantBounds.Part part : bounds) {
                final double r;
                final double g;
                if (targetedTarget) {
                    r = part.name.equals(((ParticipantTargetInstance) hovered).part) ? 1 : 0;
                    g = 1;
                } else {
                    r = 1;
                    g = 0;
                }
                TBCExCoreClient.addBoxInfo(new BoxInfo(part.box, r, g, 0, 1));
            }
        }
    }

    public static final class ParticipantTargetInstance implements TargetInstance {
        private final BattleParticipantHandle handle;
        private final double distance;
        private final ParticipantTargetType type;
        private final Identifier part;

        public ParticipantTargetInstance(final BattleParticipantHandle handle, final double distance, final ParticipantTargetType type, final Identifier part) {
            this.handle = handle;
            this.distance = distance;
            this.type = type;
            this.part = part;
        }

        public BattleParticipantHandle getHandle() {
            return handle;
        }

        @Override
        public TargetType getType() {
            return type;
        }

        @Override
        public double getDistance() {
            return distance;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ParticipantTargetInstance that)) {
                return false;
            }
            if (!handle.equals(that.handle)) {
                return false;
            }
            return type.equals(that.type);
        }

        @Override
        public int hashCode() {
            int result = handle.hashCode();
            result = 31 * result + type.hashCode();
            return result;
        }

        public BattleParticipantHandle handle() {
            return handle;
        }

        public double distance() {
            return distance;
        }

        public ParticipantTargetType type() {
            return type;
        }

        @Override
        public String toString() {
            return "ParticipantTargetInstance[" +
                    "handle=" + handle + ", " +
                    "distance=" + distance + ", " +
                    "type=" + type + ']';
        }
    }

    public static Stream<BattleParticipantHandle> getWithinRange(final BattleStateView battleState, final BattleParticipantHandle self, final boolean includeSelf, final double range) {
        if (range < 0) {
            throw new IllegalArgumentException();
        }
        if (range == 0) {
            if (includeSelf) {
                return Stream.of(self);
            } else {
                return Stream.empty();
            }
        }
        final BattleParticipantStateView selfState = battleState.getParticipant(self);
        if (selfState == null) {
            throw new TBCExException("missing participant in battle");
        }
        final Vec3d pos = new Vec3d(selfState.getPos().getX() + 0.5, selfState.getPos().getY() + 0.5, selfState.getPos().getZ() + 0.5);
        if (includeSelf) {
            return StreamSupport.stream(battleState.getSpliteratorParticipants(), false).filter(handle -> {
                final BattleParticipantStateView curState = battleState.getParticipant(handle);
                if (curState == null) {
                    throw new TBCExException("missing participant in battle");
                }
                return curState.getBounds().getDistanceSquared(pos) <= range * range;
            });
        } else {
            return StreamSupport.stream(battleState.getSpliteratorParticipants(), false).filter(handle -> {
                if (handle.equals(self)) {
                    return false;
                }
                final BattleParticipantStateView curState = battleState.getParticipant(handle);
                if (curState == null) {
                    throw new TBCExException("missing participant in battle");
                }
                return curState.getBounds().getDistanceSquared(pos) <= range * range;
            });
        }
    }
}
