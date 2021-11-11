package io.github.stuff_stuffs.tbcexequipment.common.part.stats;

import io.github.stuff_stuffs.tbcexequipment.common.equipment.data.EquipmentData;
import org.jetbrains.annotations.Nullable;

public interface PartStatContext {
    @Nullable EquipmentData getParent();

    static PartStatContext empty() {
        return () -> null;
    }

    static PartStatContext of(final EquipmentData data) {
        return () -> data;
    }
}
