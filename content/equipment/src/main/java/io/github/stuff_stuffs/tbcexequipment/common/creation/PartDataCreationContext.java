package io.github.stuff_stuffs.tbcexequipment.common.creation;

import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;

public interface PartDataCreationContext {
    Material getMaterial();

    int getLevel();

    static PartDataCreationContext createForEntity(final BattleEntity entity, final Material material) {
        return new PartDataCreationContext() {
            @Override
            public Material getMaterial() {
                return material;
            }

            @Override
            public int getLevel() {
                return entity.tbcex_getLevel();
            }
        };
    }
}
