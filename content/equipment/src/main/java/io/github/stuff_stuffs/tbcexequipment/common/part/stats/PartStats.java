package io.github.stuff_stuffs.tbcexequipment.common.part.stats;

import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.material.stats.MaterialStats;

public final class PartStats {
    public static final PartStat<Double> LONG_SWORD_BLADE_DAMAGE = TBCExEquipment.PART_STAT_MANAGER.getStat(TBCExEquipment.createId("long_sword_blade_damage"), Double.class, (data, ctx) -> {
        //TODO config?
        final Material material = data.getMaterial();
        final int level = data.getLevel();
        final double hardness = TBCExEquipment.MATERIAL_STAT_MANAGER.get(material, MaterialStats.HARDNESS);
        final double edgeRetention = TBCExEquipment.MATERIAL_STAT_MANAGER.get(material, MaterialStats.EDGE_RETENTION);
        final double weight = TBCExEquipment.MATERIAL_STAT_MANAGER.get(material, MaterialStats.WEIGHT);
        return Math.floor((hardness * level * 0.15 + edgeRetention * level * 0.3 + weight / 5.0) * 10.0) / 10.0;
    });

    private PartStats() {
    }
}
