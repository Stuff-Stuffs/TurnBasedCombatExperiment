package io.github.stuff_stuffs.tbcexutil.common;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public enum HorizontalDirection {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    public static final Codec<HorizontalDirection> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<HorizontalDirection, T>> decode(final DynamicOps<T> ops, final T input) {
            return DataResult.success(Pair.of(ops.getStringValue(input).map(HorizontalDirection::valueOf).getOrThrow(false, s -> {
                //TODO
                throw new RuntimeException();
            }), input));
        }

        @Override
        public <T> DataResult<T> encode(final HorizontalDirection input, final DynamicOps<T> ops, final T prefix) {
            return DataResult.success(ops.createString(input.name()));
        }
    };

    public static @Nullable HorizontalDirection fromDirection(final Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP, DOWN -> null;
        };
    }

    public static Direction fromHorizontal(final HorizontalDirection direction) {
        return switch (direction) {
            case NORTH -> Direction.NORTH;
            case SOUTH -> Direction.SOUTH;
            case EAST -> Direction.EAST;
            case WEST -> Direction.WEST;
        };
    }
}
