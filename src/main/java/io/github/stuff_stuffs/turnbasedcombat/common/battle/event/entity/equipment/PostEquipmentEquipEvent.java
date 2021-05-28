package io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.equipment;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipment;

public interface PostEquipmentEquipEvent {
    void onEquip(EntityState state, BattleEquipment equipment);
}
