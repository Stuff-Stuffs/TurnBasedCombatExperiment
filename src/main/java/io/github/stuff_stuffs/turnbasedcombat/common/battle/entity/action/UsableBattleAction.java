package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;

public interface UsableBattleAction extends EntityAction {
    BattleAction apply(EntityStateView entityState);
}
