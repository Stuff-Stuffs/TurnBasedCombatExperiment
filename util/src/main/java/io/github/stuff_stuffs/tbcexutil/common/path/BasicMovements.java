package io.github.stuff_stuffs.tbcexutil.common.path;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import io.github.stuff_stuffs.tbcexutil.common.BattleParticipantBounds;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import io.github.stuff_stuffs.tbcexutil.common.WorldShapeCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public enum BasicMovements implements MovementType {
    FALL {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            final double groundHeight = MovementType.getGroundHeight(bounds.offset(0, -1, 0), cache);
            if (groundHeight < 1 && !MovementType.doesCollideWith(bounds.offset(0, -0.01, 0), cache)) {
                final boolean validEnding = MovementType.doesCollideWith(bounds.offset(0, -2, 0), cache);
                return new Fall(pos, validEnding);
            }
            return null;
        }

        @Override
        public <T> T serialize(final DynamicOps<T> ops, final Movement movement) {
            if (movement.getType() != FALL) {
                throw new RuntimeException();
            }
            final Fall fall = (Fall) movement;
            final RecordBuilder<T> builder = ops.mapBuilder();
            builder.add("start_pos", BlockPos.CODEC.encodeStart(ops, fall.getStartPos()));
            builder.add("valid_ending", Codec.BOOL.encodeStart(ops, fall.validEnding));
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
            final boolean validEnding = Codec.BOOL.parse(ops, mapLike.get("valid_ending")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            return new Fall(startPos, validEnding);
        }
    }, WALK_NORTH {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 0, -1), cache) && MovementType.doesCollideWith(bounds.offset(0, -1, -1), cache)) {
                return createSimple(pos, pos.add(0, 0, -1), HorizontalDirection.NORTH, WALK_NORTH);
            }
            return null;
        }

        @Override
        public <T> T serialize(final DynamicOps<T> ops, final Movement movement) {
            if (movement.getType() != WALK_NORTH) {
                throw new RuntimeException();
            }
            final Simple simple = (Simple) movement;
            final RecordBuilder<T> builder = ops.mapBuilder();
            builder.add("start_pos", BlockPos.CODEC.encodeStart(ops, simple.start));
            builder.add("end_pos", BlockPos.CODEC.encodeStart(ops, simple.end));
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
            return new Simple(startPos, endPos, WALK_NORTH);
        }
    },
    WALK_SOUTH {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(0, 0, 1), cache) && MovementType.doesCollideWith(bounds.offset(0, -1, 1), cache)) {
                return createSimple(pos, pos.add(0, 0, 1), HorizontalDirection.SOUTH, WALK_SOUTH);
            }
            return null;
        }

        @Override
        public <T> T serialize(final DynamicOps<T> ops, final Movement movement) {
            if (movement.getType() != WALK_SOUTH) {
                throw new RuntimeException();
            }
            final Simple simple = (Simple) movement;
            final RecordBuilder<T> builder = ops.mapBuilder();
            builder.add("start_pos", BlockPos.CODEC.encodeStart(ops, simple.start));
            builder.add("end_pos", BlockPos.CODEC.encodeStart(ops, simple.end));
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
            return new Simple(startPos, endPos, WALK_SOUTH);
        }
    }, WALK_EAST {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(1, 0, 0), cache) && MovementType.doesCollideWith(bounds.offset(1, -1, 0), cache)) {
                return createSimple(pos, pos.add(1, 0, 0), HorizontalDirection.EAST, WALK_EAST);
            }
            return null;
        }

        @Override
        public <T> T serialize(final DynamicOps<T> ops, final Movement movement) {
            if (movement.getType() != WALK_EAST) {
                throw new RuntimeException();
            }
            final Simple simple = (Simple) movement;
            final RecordBuilder<T> builder = ops.mapBuilder();
            builder.add("start_pos", BlockPos.CODEC.encodeStart(ops, simple.start));
            builder.add("end_pos", BlockPos.CODEC.encodeStart(ops, simple.end));
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
            return new Simple(startPos, endPos, WALK_EAST);
        }
    }, WALK_WEST {
        @Override
        public @Nullable Movement modify(final BattleParticipantBounds bounds, final BlockPos pos, final Box pathBounds, final World world, final WorldShapeCache cache) {
            if (MovementType.doesCollideWith(bounds.offset(0, -1, 0), cache) && !MovementType.doesCollideWith(bounds.offset(-1, 0, 0), cache) && MovementType.doesCollideWith(bounds.offset(-1, -1, 0), cache)) {
                return createSimple(pos, pos.add(-1, 0, 0), HorizontalDirection.EAST, WALK_WEST);
            }
            return null;
        }

        @Override
        public <T> T serialize(final DynamicOps<T> ops, final Movement movement) {
            if (movement.getType() != WALK_WEST) {
                throw new RuntimeException();
            }
            final Simple simple = (Simple) movement;
            final RecordBuilder<T> builder = ops.mapBuilder();
            builder.add("start_pos", BlockPos.CODEC.encodeStart(ops, simple.start));
            builder.add("end_pos", BlockPos.CODEC.encodeStart(ops, simple.end));
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
            return new Simple(startPos, endPos, WALK_WEST);
        }
    };

    private static Movement createSimple(final BlockPos start, final BlockPos end, final HorizontalDirection endDir, final MovementType type) {
        return new Simple(start, end, type);
    }

    private static final class Simple implements Movement {
        private final BlockPos start;
        private final BlockPos end;
        private final BlockPos delta;
        private final MovementType type;

        private Simple(final BlockPos start, final BlockPos end, final MovementType type) {
            this.start = start;
            this.end = end;
            this.type = type;
            delta = end.subtract(start);
        }

        @Override
        public double getCost() {
            return 1;
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
            return 1;
        }

        @Override
        public Vec3d interpolate(final Vec3d start, final double t) {
            return start.add(delta.getX() * t, delta.getY() * t, delta.getZ() * t);
        }

        @Override
        public MovementType getType() {
            return type;
        }
    }

    private static final class Fall implements Movement {
        private static final Set<MovementFlag> FLAGS = Set.of(MovementFlag.FALL);
        private final BlockPos start;
        private final boolean validEnding;

        private Fall(final BlockPos start, final boolean validEnding) {
            this.start = start;
            this.validEnding = validEnding;
        }

        @Override
        public double getCost() {
            return 4;
        }

        @Override
        public BlockPos getStartPos() {
            return start;
        }

        @Override
        public BlockPos getEndPos() {
            return start.down();
        }

        @Override
        public double getLength() {
            return 1;
        }

        @Override
        public Vec3d interpolate(final Vec3d start, final double t) {
            return start.add(0, -1 * t, 0);
        }

        @Override
        public boolean isValidEnding() {
            return validEnding;
        }

        @Override
        public MovementType getType() {
            return FALL;
        }

        @Override
        public Set<MovementFlag> getFlags() {
            return FLAGS;
        }
    }
}
