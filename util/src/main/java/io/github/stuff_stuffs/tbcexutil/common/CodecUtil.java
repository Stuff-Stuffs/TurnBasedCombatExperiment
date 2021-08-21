package io.github.stuff_stuffs.tbcexutil.common;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.*;

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
}
