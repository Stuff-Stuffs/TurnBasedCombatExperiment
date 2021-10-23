package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target;

import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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

    //TODO this might be too expensive, especially in case of mcts
    private static boolean canViewBox(final Vec3d start, final Box box, final Context context, final BattleParticipantHandle... exclusions) {
        return context.battle.getShapeCache().canSeeAny(start, getPoints(box), exclusions);
    }

    private static Vec3d[] getPoints(final Box box) {
        return new Vec3d[]{
                new Vec3d(box.minX, box.minY, box.minZ),
                new Vec3d(box.minX, box.minY, box.maxZ),
                new Vec3d(box.minX, box.maxY, box.minZ),
                new Vec3d(box.minX, box.maxY, box.maxZ),
                new Vec3d(box.maxX, box.minY, box.minZ),
                new Vec3d(box.maxX, box.minY, box.maxZ),
                new Vec3d(box.maxX, box.maxY, box.minZ),
                new Vec3d(box.maxX, box.maxY, box.maxZ),
                box.getCenter()
        };
    }

    public static Stream<BlockPos> getFloorPositions(final Context context, final int range, final World world) {
        final BlockPos pos = context.getSelfState().getPos();
        final BlockPos.Mutable mutable = new BlockPos.Mutable();
        final int sq = range * range;
        return Stream.iterate(new FloorFinder(range, pos.getX() - range, pos.getY() - range, pos.getZ() - range), f -> !(f.x + f.range == f.curX && f.y + f.range == f.curY && f.z + f.range == f.curZ), floorFinder -> {
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
            final BlockState floorState = world.getBlockState(mutable.set(f.curX, f.curY - 1, f.curZ));
            if (floorState.getCollisionShape(world, mutable).isEmpty()) {
                return false;
            }
            final BlockState blockState = world.getBlockState(mutable.set(f.curX, f.curY, f.curZ));
            return blockState.getCollisionShape(world, mutable).isEmpty();
        }).map(f -> new BlockPos(f.curX, f.curY, f.curZ));
    }

    public static Predicate<BlockPos> visibleBlockPos(final Context context, final Identifier eyePart) {
        final BattleParticipantBounds.Part part = context.getSelfState().getBounds().getPart(eyePart);
        final Vec3d center = part.box.getCenter();
        return pos -> canViewBox(center, new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), context);
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
