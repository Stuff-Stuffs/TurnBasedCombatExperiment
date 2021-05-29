package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory.BattleItem;
import org.jetbrains.annotations.Nullable;

public interface BattleEquipment {
    BattleEquipmentType getType();

    void initEvents(EntityState entityState);

    void deinitEvents();

    @Nullable BattleItem toBattleItem();
}
