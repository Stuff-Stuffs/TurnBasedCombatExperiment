package io.github.stuff_stuffs.tbcexutil.common;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class CodecUtil {
    public static final Codec<Text> TEXT_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Text, T>> decode(final DynamicOps<T> ops, final T input) {
            final JsonElement jsonElement = ops.convertTo(JsonOps.INSTANCE, input);
            try {
                return DataResult.success(Pair.of(Text.Serializer.fromJson(jsonElement), ops.empty()));
            } catch (final Exception e) {
                return DataResult.error(e.getMessage());
            }
        }

        @Override
        public <T> DataResult<T> encode(final Text input, final DynamicOps<T> ops, final T prefix) {
            if (prefix != ops.empty()) {
                throw new IllegalArgumentException();
            }
            final JsonElement element = Text.Serializer.toJsonTree(input);
            return DataResult.success(JsonOps.INSTANCE.convertTo(ops, element));
        }
    };

    public static final Codec<UUID> UUID_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("hi").forGetter(UUID::getMostSignificantBits),
            Codec.LONG.fieldOf("lo").forGetter(UUID::getLeastSignificantBits)
    ).apply(instance, UUID::new));

    public static final Codec<NbtElement> NBT_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<NbtElement, T>> decode(final DynamicOps<T> ops, final T input) {
            return DataResult.success(Pair.of(ops.convertTo(NbtOps.INSTANCE, input), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final NbtElement input, final DynamicOps<T> ops, final T prefix) {
            return DataResult.success(NbtOps.INSTANCE.convertTo(ops, input));
        }
    };

    public static final Codec<Vec3d> VEC3D_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Vec3d, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final double x = ops.getNumberValue(map.get("x")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }).doubleValue();
            final double y = ops.getNumberValue(map.get("y")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }).doubleValue();
            final double z = ops.getNumberValue(map.get("z")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }).doubleValue();
            return DataResult.success(Pair.of(new Vec3d(x, y, z), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final Vec3d input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add(
                    "x",
                    ops.createDouble(input.x)
            ).add(
                    "y",
                    ops.createDouble(input.y)
            ).add(
                    "z",
                    ops.createDouble(input.z)
            ).build(prefix);
        }
    };

    public static final Codec<Box> BOX_CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.DOUBLE.fieldOf("minX").forGetter(b -> b.minX), Codec.DOUBLE.fieldOf("minY").forGetter(b -> b.minY), Codec.DOUBLE.fieldOf("minZ").forGetter(b -> b.minZ), Codec.DOUBLE.fieldOf("maxX").forGetter(b -> b.maxX), Codec.DOUBLE.fieldOf("maxY").forGetter(b -> b.maxY), Codec.DOUBLE.fieldOf("maxZ").forGetter(b -> b.maxZ)).apply(instance, Box::new));

    private CodecUtil() {
    }

    public static <K, V> Codec<Map<K, V>> createLinkedMapCodec(final Codec<K> keyCodec, final Codec<V> valueCodec) {
        final Codec<List<Pair<K, V>>> listCodec = Codec.list(Codec.pair(keyCodec, valueCodec));
        return listCodec.xmap(list -> {
            final LinkedHashMap<K, V> map = new LinkedHashMap<>();
            for (final Pair<K, V> pair : list) {
                map.put(pair.getFirst(), pair.getSecond());
            }
            return map;
        }, map -> {
            final List<Pair<K, V>> list = new ArrayList<>(map.size());
            for (final Map.Entry<K, V> entry : map.entrySet()) {
                list.add(Pair.of(entry.getKey(), entry.getValue()));
            }
            return list;
        });
    }

    public static <U> U copy(final U val, final Codec<U> codec) {
        return codec.parse(NbtOps.INSTANCE, codec.encodeStart(NbtOps.INSTANCE, val).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        })).getOrThrow(false, s -> {
            throw new RuntimeException(s);
        });
    }

    public static <L, R> Codec<Pair<L, R>> createDependentPairCodec(final Codec<L> leftCodec, final DependentEncoder<L, R> rightEncoder, final DependentDecoder<L, R> rightDecoder) {
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<Pair<L, R>, T>> decode(final DynamicOps<T> ops, final T input) {
                final MapLike<T> mapLike = ops.getMap(input).getOrThrow(false, s -> {
                    throw new TBCExException(s);
                });
                final Either<L, DataResult.PartialResult<L>> leftResultOrError = leftCodec.parse(ops, mapLike.get("left")).get();
                final Optional<DataResult.PartialResult<L>> leftError = leftResultOrError.right();
                if (leftError.isPresent()) {
                    return DataResult.error(leftError.get().message());
                }
                final Optional<L> leftResult = leftResultOrError.left();
                if (leftResult.isEmpty()) {
                    throw new TBCExException("Either is empty");
                }
                final L left = leftResult.get();
                final Either<R, DataResult.PartialResult<R>> rightResultOrError = rightDecoder.decode(left, mapLike.get("right"), ops).get();
                final Optional<DataResult.PartialResult<R>> rightError = rightResultOrError.right();
                if (rightError.isPresent()) {
                    return DataResult.error(rightError.get().message());
                }
                final Optional<R> rightResult = rightResultOrError.left();
                if (rightResult.isEmpty()) {
                    throw new TBCExException("Either is empty");
                }
                final R right = rightResult.get();
                return DataResult.success(Pair.of(Pair.of(left, right), ops.empty()));
            }

            @Override
            public <T> DataResult<T> encode(final Pair<L, R> input, final DynamicOps<T> ops, final T prefix) {
                if (!ops.empty().equals(prefix)) {
                    throw new TBCExException("Prefix not allowed");
                }
                return ops.mapBuilder().add("left", input.getFirst(), leftCodec).add("right", rightEncoder.encode(input.getFirst(), input.getSecond(), ops)).build(ops.empty());
            }
        };
    }

    public static <L, R> Codec<R> createDependentPairCodecFirst(final Codec<L> leftCodec, final DependentEncoder<L, R> rightEncoder, final DependentDecoder<L, R> rightDecoder, final Function<R, L> leftExtractor) {
        return createDependentPairCodec(leftCodec, rightEncoder, rightDecoder).xmap(Pair::getSecond, v -> Pair.of(leftExtractor.apply(v), v));
    }

    public static <L, R> Codec<Pair<L, R>> createDependentPairCodec(final Codec<L> leftCodec, final Function<L, Codec<R>> rightCodecExtractor) {
        return createDependentPairCodec(leftCodec, new DependentEncoder<>() {
            @Override
            public <T> DataResult<T> encode(final L coValue, final R value, final DynamicOps<T> ops) {
                return rightCodecExtractor.apply(coValue).encodeStart(ops, value);
            }
        }, new DependentDecoder<>() {
            @Override
            public <T> DataResult<R> decode(final L coValue, final T value, final DynamicOps<T> ops) {
                return rightCodecExtractor.apply(coValue).parse(ops, value);
            }
        });
    }

    public static <L, R, T> Codec<T> createDependentPairCodec(final Codec<L> leftCodec, final Function<L, Codec<R>> rightCodecExtractor, final Function<T, L> leftExtractor, final Function<T, R> rightExtractor, final BiFunction<L, R, T> combiner) {
        return createDependentPairCodec(leftCodec, rightCodecExtractor).xmap(p -> combiner.apply(p.getFirst(), p.getSecond()), v -> Pair.of(leftExtractor.apply(v), rightExtractor.apply(v)));
    }

    public static <L, R> Codec<R> createDependentPairCodecFirst(final Codec<L> leftCodec, final Function<L, Codec<R>> rightCodecExtractor, final Function<R, L> leftExtractor) {
        return createDependentPairCodec(leftCodec, rightCodecExtractor).xmap(Pair::getSecond, v -> Pair.of(leftExtractor.apply(v), v));
    }

    public interface DependentEncoder<L, R> {
        <T> DataResult<T> encode(L coValue, R value, DynamicOps<T> ops);
    }

    public interface DependentDecoder<L, R> {
        <T> DataResult<R> decode(L coValue, T value, DynamicOps<T> ops);
    }
}
