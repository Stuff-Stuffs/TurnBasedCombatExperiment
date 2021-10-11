package io.github.stuff_stuffs.tbcexutil.common.path;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface Movement {
    Codec<Movement> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Movement, T>> decode(final DynamicOps<T> ops, final T input) {
            return DataResult.success(Pair.of(deserialize(ops, input), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final Movement input, final DynamicOps<T> ops, final T prefix) {
            if(prefix!=ops.empty()) {
                throw new RuntimeException("Non empty prefix");
            }
            return DataResult.success(serialize(ops, input));
        }
    };

    double getCost();

    BlockPos getStartPos();

    BlockPos getEndPos();

    double getLength();

    Vec3d interpolate(Vec3d start, double t);

    HorizontalDirection getRotation(double t);

    default boolean isValidEnding() {
        return true;
    }

    MovementType getType();

    static <T> T serialize(final DynamicOps<T> ops, final Movement movement) {
        final RecordBuilder<T> builder = ops.mapBuilder();
        builder.add("data", movement.getType().serialize(ops, movement));
        builder.add("type", MovementTypes.REGISTRY.encodeStart(ops, movement.getType()));
        return builder.build(ops.empty()).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }

    static <T> Movement deserialize(final DynamicOps<T> ops, final T serialized) {
        final MapLike<T> mapLike = ops.getMap(serialized).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
        final T data = mapLike.get("data");
        final MovementType type = MovementTypes.REGISTRY.parse(ops, mapLike.get("type")).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
        return type.deserialize(ops, data);
    }
}
