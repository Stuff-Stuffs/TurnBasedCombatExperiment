package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;

public interface BattleTimelineView extends Iterable<BattleAction<?>> {
    BattleAction<?> get(int index);
}
