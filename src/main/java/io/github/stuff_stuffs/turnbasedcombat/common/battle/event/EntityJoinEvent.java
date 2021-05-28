package io.github.stuff_stuffs.turnbasedcombat.common.battle.event;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;

public interface EntityJoinEvent {
    void onEntityJoin(BattleState battleState, EntityState entityState);
}
