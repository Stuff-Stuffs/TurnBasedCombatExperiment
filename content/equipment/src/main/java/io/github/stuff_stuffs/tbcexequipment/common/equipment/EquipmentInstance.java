package io.github.stuff_stuffs.tbcexequipment.common.equipment;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.data.EquipmentData;
import io.github.stuff_stuffs.tbcexutil.common.StringInterpolator;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;

public final class EquipmentInstance {
    private static final StringInterpolator MISMATCH = new StringInterpolator("Mismatched equipment {} and data type {}");
    public static final Codec<EquipmentInstance> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<EquipmentInstance, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new TBCExException(s);
            });
            final EquipmentType<?> part = EquipmentTypes.REGISTRY.parse(ops, map.get("type")).getOrThrow(false, s -> {
                throw new TBCExException(s);
            });
            final EquipmentData data = part.getDataCodec().parse(ops, map.get("data")).getOrThrow(false, s -> {
                throw new TBCExException(s);
            });
            return DataResult.success(Pair.of(new EquipmentInstance(part, data), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final EquipmentInstance input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add("type", EquipmentTypes.REGISTRY.encodeStart(ops, input.type)).add("data", input.type.getUncheckedCodec().encodeStart(ops, input.data)).build(prefix);
        }
    };
    private final EquipmentType<?> type;
    private final EquipmentData data;

    public EquipmentInstance(final EquipmentType<?> type, final EquipmentData data) {
        this.type = type;
        this.data = data;
        if (type != data.getType()) {
            throw new TBCExException(MISMATCH.interpolate(type, data.getType()));
        }
    }

    public EquipmentType<?> getType() {
        return type;
    }

    public EquipmentData getData() {
        return data;
    }
}
