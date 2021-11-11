package io.github.stuff_stuffs.tbcexequipment.common.battle.equipment;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentType;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.Registry;

public final class BattleEquipmentTypes {
    public static final BattleEquipmentType EQUIPMENT_BATTLE_EQUIPMENT_TYPE = new BattleEquipmentType(new LiteralText("Equipment"), EquipmentBattleEquipment.CODEC);

    public static void init() {
        Registry.register(BattleEquipmentType.REGISTRY, TBCExEquipment.createId("equipment"), EQUIPMENT_BATTLE_EQUIPMENT_TYPE);
    }

    private BattleEquipmentTypes() {
    }
}
