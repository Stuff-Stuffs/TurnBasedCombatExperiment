package io.github.stuff_stuffs.tbcexequipment.common.equipment;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.data.EquipmentData;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;
import io.github.stuff_stuffs.tbcexutil.common.StringInterpolator;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;

public final class EquipmentInstance {
    private static final StringInterpolator MISMATCH = new StringInterpolator("Mismatched equipment {} and data type {}");
    public static final Codec<EquipmentInstance> CODEC = CodecUtil.createDependentPairCodec(EquipmentTypes.REGISTRY, EquipmentType::getUncheckedCodec, EquipmentInstance::getType, EquipmentInstance::getData, EquipmentInstance::new);
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
