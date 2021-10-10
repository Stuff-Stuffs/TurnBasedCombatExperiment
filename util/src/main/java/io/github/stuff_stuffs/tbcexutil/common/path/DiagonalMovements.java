package io.github.stuff_stuffs.tbcexutil.common.path;

import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import io.github.stuff_stuffs.tbcexutil.common.WorldShapeCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public enum DiagonalMovements implements MovementType {
    NORTH_EAST {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && MovementType.doesCollideWith(bounds.offset(1, -1, -1), cache) && !MovementType.doesCollideWith(bounds.offset(1, 0, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 0, -1), cache) && !MovementType.doesCollideWith(bounds.offset(1, 0, -1), cache)) {
                return DiagonalMovements.create(pos, pos.add(1, 0, -1), dir, HorizontalDirection.NORTH, HorizontalDirection.EAST);
            }
            return null;
        }
    },
    NORTH_WEST {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && MovementType.doesCollideWith(bounds.offset(-1, -1, -1), cache) && !MovementType.doesCollideWith(bounds.offset(-1, 0, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 0, -1), cache) && !MovementType.doesCollideWith(bounds.offset(-1, 0, -1), cache)) {
                return DiagonalMovements.create(pos, pos.add(-1, 0, -1), dir, HorizontalDirection.NORTH, HorizontalDirection.EAST);
            }
            return null;
        }
    },
    SOUTH_EAST {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && MovementType.doesCollideWith(bounds.offset(1, -1, 1), cache) && !MovementType.doesCollideWith(bounds.offset(1, 0, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 0, 1), cache) && !MovementType.doesCollideWith(bounds.offset(1, 0, 1), cache)) {
                return DiagonalMovements.create(pos, pos.add(1, 0, 1), dir, HorizontalDirection.NORTH, HorizontalDirection.EAST);
            }
            return null;
        }
    },
    SOUTH_WEST {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && MovementType.doesCollideWith(bounds.offset(-1, -1, 1), cache) && !MovementType.doesCollideWith(bounds.offset(-1, 0, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 0, 1), cache) && !MovementType.doesCollideWith(bounds.offset(-1, 0, 1), cache)) {
                return DiagonalMovements.create(pos, pos.add(-1, 0, 1), dir, HorizontalDirection.NORTH, HorizontalDirection.EAST);
            }
            return null;
        }
    };

    private static Movement create(final BlockPos start, final BlockPos end, final HorizontalDirection startDir, final HorizontalDirection first, final HorizontalDirection second) {
        final BlockPos delta = end.subtract(start);
        final double lengthSq = delta.getX() * delta.getX() + delta.getY() * delta.getY() + delta.getZ() * delta.getZ();
        final double length = lengthSq * MathHelper.fastInverseSqrt(lengthSq);
        return new Movement() {
            @Override
            public double getCost() {
                return length;
            }

            @Override
            public BlockPos getStartPos() {
                return start;
            }

            @Override
            public BlockPos getEndPos() {
                return end;
            }

            @Override
            public double getLength() {
                return length;
            }

            @Override
            public Vec3d interpolate(final Vec3d start, double t) {
                t = t / length;
                return start.add(delta.getX() * t, delta.getY() * t, delta.getZ() * t);
            }

            @Override
            public HorizontalDirection getRotation(double t) {
                t /= length;
                if (t < 0.5) {
                    return startDir;
                }
                if (startDir == first) {
                    return first;
                }
                if (startDir == second) {
                    return second;
                }
                if (first.ordinal() < second.ordinal()) {
                    return first;
                }
                return second;
            }
        };
    }
}
