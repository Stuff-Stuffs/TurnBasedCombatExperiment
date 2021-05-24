package io.github.stuff_stuffs.turnbasedcombat.common.item;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;

public interface BattleItem {
    boolean canUseBattleItem(EntityStateView entityState, BattleStateView battleState);

    boolean useBattleItem(EntityState entityState, BattleState battleState);
}
