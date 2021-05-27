package io.github.stuff_stuffs.turnbasedcombat.common.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;

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
            if (prefix != null && prefix != ops.empty()) {
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

    private CodecUtil() {
    }
}
