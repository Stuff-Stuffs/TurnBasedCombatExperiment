package io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentSlot;

public interface PreEquipmentUnEquipEvent {
    /**
     * @return true if should cancel
     */
    boolean onUnEquip(EntityState state, BattleEquipmentSlot slot, BattleEquipment equipment);
}
