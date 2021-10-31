package io.github.stuff_stuffs.tbcexequipment.common.part;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.material.Materials;
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
            final Material material = Materials.REGISTRY.parse(ops, map.get("material")).getOrThrow(false, s -> {
                throw new TBCExException(s);
            });
            return DataResult.success(Pair.of(new PartInstance(part, data, material), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final PartInstance input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add("type", Parts.REGISTRY.encodeStart(ops, input.part)).add("data", input.part.getUncheckedCodec().encodeStart(ops, input.data)).add("material", Materials.REGISTRY.encodeStart(ops, input.material)).build(prefix);
        }
    };
    private final Part<?> part;
    private final PartData data;
    private final Material material;

    public PartInstance(final Part<?> part, final PartData data, final Material material) {
        this.part = part;
        this.data = data;
        if (part != data.getType()) {
            throw new TBCExException("Mismatched part and data");
        }
        this.material = material;
    }

    public Part<?> getPart() {
        return part;
    }

    public PartData getData() {
        return data;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isValid() {
        return part.isValidMaterial(material);
    }
}
