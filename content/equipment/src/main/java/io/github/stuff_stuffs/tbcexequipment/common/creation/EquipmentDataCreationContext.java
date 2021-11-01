package io.github.stuff_stuffs.tbcexequipment.common.creation;

import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface EquipmentDataCreationContext {
    Map<Identifier, PartInstance> getParts();

    @Nullable Entity getEntity();

    static EquipmentDataCreationContext createForEntity(final Entity entity, final Map<Identifier, PartInstance> parts) {
        return new EquipmentDataCreationContext() {
            @Override
            public Map<Identifier, PartInstance> getParts() {
                return parts;
            }

            @Override
            public @Nullable Entity getEntity() {
                return entity;
            }
        };
    }
}
