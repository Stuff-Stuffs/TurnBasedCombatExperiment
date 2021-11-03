package io.github.stuff_stuffs.tbcexequipment.common.part;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.tbcexequipment.common.part.data.PartData;
import io.github.stuff_stuffs.tbcexutil.common.StringInterpolator;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;

public final class PartInstance {
    public static final Codec<PartInstance> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<PartInstance, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new TBCExException(s);
            });
            final Part<?> part = Parts.REGISTRY.parse(ops, map.get("type")).getOrThrow(false, s -> {
                throw new TBCExException(s);
            });
            final PartData data = part.getDataCodec().parse(ops, map.get("data")).getOrThrow(false, s -> {
                throw new TBCExException(s);
            });
            return DataResult.success(Pair.of(new PartInstance(part, data), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final PartInstance input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add("type", Parts.REGISTRY.encodeStart(ops, input.part)).add("data", input.part.getUncheckedCodec().encodeStart(ops, input.data)).build(prefix);
        }
    };
    private static final StringInterpolator MISMATCH_PART_DATA = new StringInterpolator("Mismatched part {} and data part {}");
    private final Part<?> part;
    private final PartData data;

    public PartInstance(final Part<?> part, final PartData data) {
        this.part = part;
        this.data = data;
        if (part != data.getType()) {
            throw new TBCExException(MISMATCH_PART_DATA.interpolate(part, data.getType()));
        }
    }

    public Part<?> getPart() {
        return part;
    }

    public PartData getData() {
        return data;
    }
}
