package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action.EntityAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory.BattleItem;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public interface BattleEquipment {
    BattleEquipmentType getType();

    default List<EntityAction> getActions(final EntityStateView entityState) {
        return Collections.emptyList();
    }

    void initEvents(EntityState entityState);

    void deinitEvents();

    @Nullable BattleItem toBattleItem();
}
