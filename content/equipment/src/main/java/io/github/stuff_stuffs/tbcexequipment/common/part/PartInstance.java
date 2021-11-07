package io.github.stuff_stuffs.tbcexequipment.common.part;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexequipment.common.part.data.PartData;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;
import io.github.stuff_stuffs.tbcexutil.common.StringInterpolator;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;

public final class PartInstance {
    public static final Codec<PartInstance> CODEC = CodecUtil.createDependentPairCodec(Parts.REGISTRY, Part::getUncheckedCodec, PartInstance::getPart, PartInstance::getData, PartInstance::new);
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
