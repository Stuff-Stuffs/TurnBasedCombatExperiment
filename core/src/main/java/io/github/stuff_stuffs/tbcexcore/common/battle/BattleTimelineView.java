package io.github.stuff_stuffs.tbcexcore.common.battle;

import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;

public interface BattleTimelineView extends Iterable<BattleAction<?>> {
    BattleAction<?> get(int index);

    int getSize();
}
