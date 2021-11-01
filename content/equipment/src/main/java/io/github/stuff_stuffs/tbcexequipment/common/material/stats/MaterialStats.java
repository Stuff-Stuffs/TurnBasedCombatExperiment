package io.github.stuff_stuffs.tbcexequipment.common.material.stats;

import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;

public final class MaterialStats {
    public static final MaterialStat HARDNESS = TBCExEquipment.MATERIAL_STAT_MANAGER.getStat(TBCExEquipment.createId("hardness"));
    public static final MaterialStat WEIGHT = TBCExEquipment.MATERIAL_STAT_MANAGER.getStat(TBCExEquipment.createId("weight"));

    public static void init() {

    }

    private MaterialStats() {
    }
}
