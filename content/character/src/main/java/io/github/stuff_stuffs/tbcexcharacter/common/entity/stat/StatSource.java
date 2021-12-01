package io.github.stuff_stuffs.tbcexcharacter.common.entity.stat;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import net.minecraft.text.Text;

public final class StatSource<T> {
    public static final Codec<StatSource<?>> CODEC = new Codec<StatSource<?>>() {
        @Override
        public <T> DataResult<Pair<StatSource<?>, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> mapLike = ops.getMap(input).getOrThrow(false, s -> {
                //TODO
                throw new TBCExException(s);
            });
            final StatSources.Type<?> type = StatSources.REGISTRY.getCodec().parse(ops, mapLike.get("type")).getOrThrow(false, s -> {
                //TODO
                throw new TBCExException(s);
            });
            final double amount = ops.getNumberValue(mapLike.get("amount")).getOrThrow(false, s -> {
                throw new TBCExException(s);
            }).doubleValue();
            return DataResult.success(Pair.of(decode(type, amount, mapLike.get("data"), ops), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final StatSource<?> input, final DynamicOps<T> ops, final T prefix) {
            if (!ops.empty().equals(prefix)) {
                throw new TBCExException("Non empty prefix");
            }
            return encode(input, ops);
        }

        private <T, K> DataResult<T> encode(final StatSource<K> stat, final DynamicOps<T> ops) {
            return ops.mapBuilder().add("type", StatSources.REGISTRY.getCodec().encodeStart(ops, stat.getType())).add("amount", Codec.DOUBLE.encodeStart(ops, stat.getAmount())).add("data", stat.getType().getDataCodec().encodeStart(ops, stat.getData())).build(ops.empty());
        }

        private <T, K> StatSource<K> decode(final StatSources.Type<K> stat, final double amount, final T data, final DynamicOps<T> ops) {
            return new StatSource<K>(stat, stat.getDataCodec().decode(ops, data).getOrThrow(false, s -> {
                throw new TBCExException(s);
            }).getFirst(), amount);
        }
    };
    private final StatSources.Type<T> type;
    private final T data;
    private final double amount;

    public StatSource(final StatSources.Type<T> type, final T data, final double amount) {
        this.type = type;
        this.data = data;
        this.amount = amount;
    }

    public StatSources.Type<T> getType() {
        return type;
    }

    public T getData() {
        return data;
    }

    public double getAmount() {
        return amount;
    }

    public Text getText() {
        return type.extract(data);
    }
}
