package io.github.stuff_stuffs.turnbasedcombat.common.battle.event;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;

public interface EntityLeaveEvent {
    void onEntityLeave(BattleState battleState, EntityStateView entityState);
}
