package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target;

import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.MathUtil;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class TargetStreams {
    public static final class Context {
        private final BattleStateView battle;
        private final BattleParticipantHandle self;
        private Vec3d pos;

        public Context(final BattleStateView battle, final BattleParticipantHandle self) {
            this.battle = battle;
            this.self = self;
        }

        public Vec3d getPos() {
            if (pos == null) {
                final BattleParticipantStateView participant = battle.getParticipant(self);
                if (participant == null) {
                    throw new TBCExException("Context has missing self");
                }
                pos = Vec3d.ofCenter(participant.getPos());
            }
            return pos;
        }

        public BattleParticipantStateView getSelfState() {
            return getState(self);
        }

        public BattleParticipantStateView getState(final BattleParticipantHandle handle) {
            if (!handle.battleId().equals(battle.getHandle())) {
                throw new TBCExException("Battle handle mismatch");
            }
            final BattleParticipantStateView state = battle.getParticipant(handle);
            if (state == null) {
                throw new TBCExException("Missing participant in battle, probably cme");
            }
            return state;
        }
    }

    public static BiFunction<BattleStateView, BattleParticipantHandle, Iterable<BattleParticipantHandle>> setupParticipantContext(final Function<Context, Iterable<BattleParticipantHandle>> function) {
        return (battleView, self) -> {
            final Context context = new Context(battleView, self);
            return function.apply(context);
        };
    }

    public static Stream<BattleParticipantHandle> getParticipantStream(final Context context, final boolean includeSelf) {
        final Stream<BattleParticipantHandle> stream = StreamSupport.stream(context.battle.getSpliteratorParticipants(), false);
        if (includeSelf) {
            return stream;
        } else {
            return stream.filter(h -> !h.equals(context.self));
        }
    }

    public static Predicate<BattleParticipantHandle> withinRange(final Context context, final double range) {
        final double sq = range * range;
        return handle -> context.getState(handle).getBounds().getDistanceSquared(context.getPos()) < sq;
    }

    public static Predicate<BattleParticipantHandle> visibleParticipant(final Context context, final Identifier eyePart) {
        final BattleParticipantBounds.Part part = context.getSelfState().getBounds().getPart(eyePart);
        final Vec3d center = part.box.getCenter();
        return handle -> context.getState(handle).getBounds().stream().map(p -> p.box).anyMatch(box -> canViewBox(center, box, context, context.self, handle));
    }

    public static Predicate<BattleParticipantHandle> projectileVisibleParticipant(final Context context, final Identifier eyePart, final double velocity) {
        final BattleParticipantBounds.Part part = context.getSelfState().getBounds().getPart(eyePart);
        final Vec3d center = part.box.getCenter();
        return handle -> context.getState(handle).getBounds().stream().map(p -> p.box).anyMatch(box -> ProjectileUtil.getLaunchAngles(center, velocity, box).map(ProjectileUtil.createArcFunction(center)).anyMatch(l -> ProjectileUtil.raycastArc(l, context.battle.getShapeCache(), context.self)));
    }

    //TODO this might be too expensive, especially in case of mcts
    private static boolean canViewBox(final Vec3d start, final Box box, final Context context, final BattleParticipantHandle... exclusions) {
        return context.battle.getShapeCache().canSeeAny(start, MathUtil.getPoints(box), exclusions);
    }


    public static Stream<BlockPos> getFloorPositions(final Context context, final int range) {
        final BlockPos pos = context.getSelfState().getPos();
        final BlockPos.Mutable mutable = new BlockPos.Mutable();
        final int sq = range * range;
        return Stream.iterate(new FloorFinder(range, pos.getX() - range, pos.getY() - range, pos.getZ() - range), f -> f.curZ < f.z + f.range + 1, floorFinder -> {
            if (floorFinder.curX == floorFinder.x + range) {
                floorFinder.curX = floorFinder.x - floorFinder.range;
                if (floorFinder.curY == floorFinder.y + floorFinder.range) {
                    floorFinder.curY = floorFinder.y - floorFinder.range;
                    floorFinder.curZ++;
                } else {
                    floorFinder.curY++;
                }
            } else {
                floorFinder.curX++;
            }
            return floorFinder;
        }).filter(f -> (f.curX - f.x) * (f.curX - f.x) + (f.curY - f.y) * (f.curY - f.y) + (f.curZ - f.z) * (f.curZ - f.z) <= sq).filter(f -> {
            if (context.battle.getShapeCache().getShape(mutable.set(f.curX, f.curY - 1, f.curZ)).isEmpty()) {
                return false;
            }
            return context.battle.getShapeCache().getShape(mutable.set(f.curX, f.curY, f.curZ)).isEmpty();
        }).map(f -> new BlockPos(f.curX, f.curY, f.curZ));
    }

    public static Predicate<BlockPos> visibleBlockPos(final Context context, final Identifier eyePart) {
        final BattleParticipantBounds.Part part = context.getSelfState().getBounds().getPart(eyePart);
        final Vec3d center = part.box.getCenter();
        return pos -> canViewBox(center, new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), context);
    }

    public static Predicate<BlockPos> projectileVisiblePos(final Context context, final Identifier eyePart, final double velocity) {
        final BattleParticipantBounds.Part part = context.getSelfState().getBounds().getPart(eyePart);
        final Vec3d center = part.box.getCenter();
        return pos -> {
            final Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
            return ProjectileUtil.getLaunchAngles(center, velocity, box).map(ProjectileUtil.createArcFunction(center)).anyMatch(l -> ProjectileUtil.raycastArc(l, context.battle.getShapeCache(), context.self));
        };
    }

    public static Predicate<BattleParticipantHandle> team(final Context context, final boolean same) {
        final Team team = context.getSelfState().getTeam();
        return handle -> context.getState(handle).getTeam().equals(team) == same;
    }

    private static final class FloorFinder {
        private final int range;
        private final int x;
        private final int y;
        private final int z;
        private int curX;
        private int curY;
        private int curZ;

        private FloorFinder(final int range, final int x, final int y, final int z) {
            this.range = range;
            this.x = x;
            this.y = y;
            this.z = z;
            curX = x - range;
            curY = y - range;
            curZ = z - range;
        }
    }

    private TargetStreams() {
    }
}
