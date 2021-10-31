package io.github.stuff_stuffs.tbcexequipment.common.creation;

import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface EquipmentDataCreationContext {
    List<PartInstance> getParts();

    @Nullable Entity getEntity();

    static EquipmentDataCreationContext createForEntity(final Entity entity, final List<PartInstance> parts) {
        return new EquipmentDataCreationContext() {
            @Override
            public List<PartInstance> getParts() {
                return parts;
            }

            @Override
            public @Nullable Entity getEntity() {
                return entity;
            }
        };
    }
}
