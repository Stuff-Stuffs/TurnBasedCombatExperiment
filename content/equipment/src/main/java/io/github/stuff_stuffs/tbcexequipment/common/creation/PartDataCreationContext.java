package io.github.stuff_stuffs.tbcexequipment.common.creation;

import net.minecraft.entity.Entity;

public interface PartDataCreationContext {
    static PartDataCreationContext createForEntity(final Entity entity) {
        return new PartDataCreationContext() {
        };
    }
}
