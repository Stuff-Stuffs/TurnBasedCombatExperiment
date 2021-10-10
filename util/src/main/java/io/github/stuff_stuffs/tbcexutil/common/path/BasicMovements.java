package io.github.stuff_stuffs.tbcexutil.common.path;

import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import io.github.stuff_stuffs.tbcexutil.common.WorldShapeCache;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public enum BasicMovements implements MovementType {
    FALL {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            final double groundHeight = MovementType.getGroundHeight(bounds.offset(0, -1, 0), cache);
            if (groundHeight < 1 && !MovementType.doesCollideWith(bounds.offset(0, -0.01, 0), cache)) {
                boolean validEnding = MovementType.doesCollideWith(bounds.offset(0, -2, 0), cache);
                return new Movement() {
                    @Override
                    public double getCost() {
                        return 0.1;
                    }

                    @Override
                    public BlockPos getStartPos() {
                        return pos;
                    }

                    @Override
                    public BlockPos getEndPos() {
                        return pos.offset(Direction.DOWN);
                    }

                    @Override
                    public double getLength() {
                        return 1 - groundHeight;
                    }

                    @Override
                    public Vec3d interpolate(final Vec3d start, final double t) {
                        return start.add(0, -1 * t, 0);
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
            return null;
        }
    }, WALK_NORTH {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 0, -1), cache) && MovementType.doesCollideWith(bounds.offset(0, -1, -1), cache)) {
                return createSimple(pos, pos.add(0, 0, -1), dir, HorizontalDirection.NORTH);
            }
            return null;
        }
    },
    WALK_SOUTH {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 0, 1), cache) && MovementType.doesCollideWith(bounds.offset(0, -1, 1), cache)) {
                return createSimple(pos, pos.add(0, 0, 1), dir, HorizontalDirection.SOUTH);
            }
            return null;
        }
    }, WALK_EAST {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(1, 0, 0), cache) && MovementType.doesCollideWith(bounds.offset(1, -1, 0), cache)) {
                return createSimple(pos, pos.add(1, 0, 0), dir, HorizontalDirection.EAST);
            }
            return null;
        }
    }, WALK_WEST {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final HorizontalDirection dir, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(-1, 0, 0), cache) && MovementType.doesCollideWith(bounds.offset(-1, -1, 0), cache)) {
                return createSimple(pos, pos.add(-1, 0, 0), dir, HorizontalDirection.EAST);
            }
            return null;
        }
    };

    private static Movement createSimple(final BlockPos start, final BlockPos end, final HorizontalDirection startDir, final HorizontalDirection endDir) {
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
            public HorizontalDirection getRotation(final double t) {
                return t < length / 2 ? startDir : endDir;
            }
        };
    }
}
