package io.github.stuff_stuffs.tbcexutil.common;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public enum HorizontalRotation {
    R0,
    R90,
    R180,
    R270;

    public static final Codec<HorizontalRotation> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<HorizontalRotation, T>> decode(final DynamicOps<T> ops, final T input) {
            return DataResult.success(Pair.of(HorizontalRotation.valueOf(ops.getStringValue(input).getOrThrow(false, s -> {
                throw new RuntimeException();
            })), input));
        }

        @Override
        public <T> DataResult<T> encode(final HorizontalRotation input, final DynamicOps<T> ops, final T prefix) {
            return DataResult.success(ops.createString(input.name()));
        }
    };

    public HorizontalDirection rotate(final HorizontalDirection dir) {
        return switch (this) {
            case R0 -> dir;
            case R90 -> switch (dir) {
                case NORTH -> HorizontalDirection.EAST;
                case EAST -> HorizontalDirection.SOUTH;
                case SOUTH -> HorizontalDirection.WEST;
                case WEST -> HorizontalDirection.NORTH;
            };
            case R180 -> switch (dir) {
                case NORTH -> HorizontalDirection.SOUTH;
                case EAST -> HorizontalDirection.WEST;
                case SOUTH -> HorizontalDirection.NORTH;
                case WEST -> HorizontalDirection.EAST;
            };
            case R270 -> switch (dir) {
                case NORTH -> HorizontalDirection.WEST;
                case EAST -> HorizontalDirection.NORTH;
                case SOUTH -> HorizontalDirection.EAST;
                case WEST -> HorizontalDirection.SOUTH;
            };
        };
    }

    public Vec3d rotate(final Vec3d vec) {
        return switch (this) {
            case R0 -> vec;
            case R90 -> new Vec3d(vec.z, vec.y, vec.x);
            case R180 -> new Vec3d(-vec.x, vec.y, -vec.z);
            case R270 -> new Vec3d(-vec.z, vec.y, -vec.x);
        };
    }

    public Box rotateBox(final Box box) {
        if (this == R0) {
            return box;
        }
        final Vec3d first = new Vec3d(box.minX, box.minY, box.minZ);
        final Vec3d second = new Vec3d(box.maxX, box.maxY, box.maxZ);
        return new Box(rotate(first), rotate(second));
    }

    public static HorizontalRotation compute(final HorizontalDirection first, final HorizontalDirection second) {
        return switch (first) {
            case NORTH -> switch (second) {
                case NORTH -> R0;
                case EAST -> R90;
                case SOUTH -> R180;
                case WEST -> R270;
            };
            case EAST -> switch (second) {
                case EAST -> R0;
                case SOUTH -> R90;
                case WEST -> R180;
                case NORTH -> R270;
            };
            case SOUTH -> switch (second) {
                case SOUTH -> R0;
                case WEST -> R90;
                case NORTH -> R180;
                case EAST -> R270;
            };
            case WEST -> switch (second) {
                case WEST -> R0;
                case NORTH -> R90;
                case EAST -> R180;
                case SOUTH -> R270;
            };
        };
    }
}
