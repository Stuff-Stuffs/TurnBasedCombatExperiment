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

public enum SimpleJumpMovements implements MovementType {
    NORTH_JUMP {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 1, -1), cache) && MovementType.doesCollideWith(bounds.offset(0, 0, -1), cache)) {
                return SimpleJumpMovements.create(pos, pos.add(0, 1, -1), HorizontalDirection.NORTH, true);
            }
            return null;
        }
    }, SOUTH_JUMP {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 1, 1), cache) && MovementType.doesCollideWith(bounds.offset(0, 0, 1), cache)) {
                return SimpleJumpMovements.create(pos, pos.add(0, 1, 1), HorizontalDirection.SOUTH, true);
            }
            return null;
        }
    }, EAST_JUMP {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(1, 1, 0), cache) && MovementType.doesCollideWith(bounds.offset(1, 0, 0), cache)) {
                return SimpleJumpMovements.create(pos, pos.add(1, 1, 0), HorizontalDirection.EAST, true);
            }
            return null;
        }
    }, WEST_JUMP {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(-1, 1, 0), cache) && MovementType.doesCollideWith(bounds.offset(-1, 0, 0), cache)) {
                return SimpleJumpMovements.create(pos, pos.add(-1, 1, 0), HorizontalDirection.WEST, true);
            }
            return null;
        }
    }, NORTH_FALL {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 0, -1), cache) && !MovementType.doesCollideWith(bounds.offset(0, -1, -1), cache)) {
                final boolean validEnding = MovementType.doesCollideWith(bounds.offset(0, -2, -1), cache);
                return SimpleJumpMovements.create(pos, pos.add(0, -1, -1), HorizontalDirection.NORTH, validEnding);
            }
            return null;
        }
    }, SOUTH_FALL {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 0, 1), cache) && !MovementType.doesCollideWith(bounds.offset(0, -1, 1), cache)) {
                final boolean validEnding = MovementType.doesCollideWith(bounds.offset(0, -2, 1), cache);
                return SimpleJumpMovements.create(pos, pos.add(0, -1, 1), HorizontalDirection.SOUTH, validEnding);
            }
            return null;
        }
    }, EAST_FALL {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(1, 0, 0), cache) && !MovementType.doesCollideWith(bounds.offset(1, -1, 0), cache)) {
                final boolean validEnding = MovementType.doesCollideWith(bounds.offset(1, -2, 0), cache);
                return SimpleJumpMovements.create(pos, pos.add(1, -1, 0), HorizontalDirection.EAST, validEnding);
            }
            return null;
        }
    }, WEST_FALL {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(-1, 0, 0), cache) && !MovementType.doesCollideWith(bounds.offset(-1, -1, 0), cache)) {
                final boolean validEnding = MovementType.doesCollideWith(bounds.offset(-1, -2, 0), cache);
                return SimpleJumpMovements.create(pos, pos.add(-1, -1, 0), HorizontalDirection.WEST, validEnding);
            }
            return null;
        }
    };

    private static Movement create(final BlockPos start, final BlockPos end, final HorizontalDirection dir, final boolean validEnding) {
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
                final double tY;
                if (delta.getY() > 0) {
                    tY = 1 - ((1 - t) * (1 - t));
                } else {
                    tY = t * t;
                }
                return start.add(delta.getX() * t, delta.getY() * tY, delta.getZ() * t);
            }

            @Override
            public HorizontalDirection getRotation(final double t) {
                return dir;
            }

            @Override
            public boolean isValidEnding() {
                return validEnding;
            }
        };
    }
}
