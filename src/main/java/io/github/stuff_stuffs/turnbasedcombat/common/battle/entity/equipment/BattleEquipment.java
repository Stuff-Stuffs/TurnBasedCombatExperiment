package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;

public interface BattleEquipment {
    BattleEquipmentType getType();

    void initEvents(EntityState entityState);

    void deinitEvents();
}
