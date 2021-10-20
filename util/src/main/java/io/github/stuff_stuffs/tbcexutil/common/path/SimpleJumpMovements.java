package io.github.stuff_stuffs.tbcexutil.common.path;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.WorldShapeCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public enum SimpleJumpMovements implements MovementType {
    NORTH_JUMP {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 1, -1), cache) && MovementType.doesCollideWith(bounds.offset(0, 0, -1), cache)) {
                return SimpleJumpMovements.create(pos, pos.add(0, 1, -1), true, NORTH_JUMP);
            }
            return null;
        }
    }, SOUTH_JUMP {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 1, 1), cache) && MovementType.doesCollideWith(bounds.offset(0, 0, 1), cache)) {
                return SimpleJumpMovements.create(pos, pos.add(0, 1, 1), true, SOUTH_JUMP);
            }
            return null;
        }
    }, EAST_JUMP {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(1, 1, 0), cache) && MovementType.doesCollideWith(bounds.offset(1, 0, 0), cache)) {
                return SimpleJumpMovements.create(pos, pos.add(1, 1, 0), true, EAST_JUMP);
            }
            return null;
        }
    }, WEST_JUMP {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(-1, 1, 0), cache) && MovementType.doesCollideWith(bounds.offset(-1, 0, 0), cache)) {
                return SimpleJumpMovements.create(pos, pos.add(-1, 1, 0), true, WEST_JUMP);
            }
            return null;
        }
    }, NORTH_FALL {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 0, -1), cache) && !MovementType.doesCollideWith(bounds.offset(0, -1, -1), cache)) {
                final boolean validEnding = MovementType.doesCollideWith(bounds.offset(0, -2, -1), cache);
                return SimpleJumpMovements.create(pos, pos.add(0, -1, -1), validEnding, NORTH_FALL);
            }
            return null;
        }
    }, SOUTH_FALL {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 0, 1), cache) && !MovementType.doesCollideWith(bounds.offset(0, -1, 1), cache)) {
                final boolean validEnding = MovementType.doesCollideWith(bounds.offset(0, -2, 1), cache);
                return SimpleJumpMovements.create(pos, pos.add(0, -1, 1), validEnding, SOUTH_FALL);
            }
            return null;
        }
    }, EAST_FALL {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(1, 0, 0), cache) && !MovementType.doesCollideWith(bounds.offset(1, -1, 0), cache)) {
                final boolean validEnding = MovementType.doesCollideWith(bounds.offset(1, -2, 0), cache);
                return SimpleJumpMovements.create(pos, pos.add(1, -1, 0), validEnding, EAST_FALL);
            }
            return null;
        }
    }, WEST_FALL {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(-1, 0, 0), cache) && !MovementType.doesCollideWith(bounds.offset(-1, -1, 0), cache)) {
                final boolean validEnding = MovementType.doesCollideWith(bounds.offset(-1, -2, 0), cache);
                return SimpleJumpMovements.create(pos, pos.add(-1, -1, 0), validEnding, WEST_FALL);
            }
            return null;
        }
    };

    private static Movement create(final BlockPos start, final BlockPos end, final boolean validEnding, final MovementType self) {
        return new Simple(start, end, validEnding, self);
    }

    @Override
    public <T> T serialize(final DynamicOps<T> ops, final Movement movement) {
        final Simple simple = (Simple) movement;
        final RecordBuilder<T> builder = ops.mapBuilder();
        builder.add("start_pos", BlockPos.CODEC.encodeStart(ops, simple.start));
        builder.add("end_pos", BlockPos.CODEC.encodeStart(ops, simple.end));
        builder.add("validEnding", Codec.BOOL.encodeStart(ops, simple.validEnding));
        return builder.build(ops.empty()).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }

    @Override
    public <T> Movement deserialize(final DynamicOps<T> ops, final T serialized) {
        final MapLike<T> mapLike = ops.getMap(serialized).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
        final BlockPos startPos = BlockPos.CODEC.parse(ops, mapLike.get("start_pos")).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
        final BlockPos endPos = BlockPos.CODEC.parse(ops, mapLike.get("end_pos")).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
        final boolean validEnding = Codec.BOOL.parse(ops, mapLike.get("validEnding")).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
        return new Simple(startPos, endPos, validEnding, this);
    }

    private static final class Simple implements Movement {
        private static final Set<MovementFlag> FALL_FLAGS = Set.of(MovementFlag.FALL, MovementFlag.FALL_RESET_TAKE_DAMAGE);
        private final BlockPos start;
        private final BlockPos end;
        private final BlockPos delta;
        private final double length;
        private final boolean validEnding;
        private final MovementType type;

        private Simple(final BlockPos start, final BlockPos end, final boolean validEnding, final MovementType type) {
            this.start = start;
            this.end = end;
            this.type = type;
            delta = end.subtract(start);
            final double lengthSq = delta.getX() * delta.getX() + delta.getY() * delta.getY() + delta.getZ() * delta.getZ();
            length = lengthSq * MathHelper.fastInverseSqrt(lengthSq);
            this.validEnding = validEnding;
        }

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
        public boolean isValidEnding() {
            return validEnding;
        }

        @Override
        public MovementType getType() {
            return type;
        }

        @Override
        public Set<MovementFlag> getFlags() {
            return start.getY() - end.getY() == 1 ? FALL_FLAGS : Movement.super.getFlags();
        }
    }
}
