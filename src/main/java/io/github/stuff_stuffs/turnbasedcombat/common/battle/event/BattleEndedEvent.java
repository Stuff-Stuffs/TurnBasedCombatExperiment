package io.github.stuff_stuffs.turnbasedcombat.common.battle.event;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;

public interface BattleEndedEvent {
    void onBattleEnded(BattleStateView battleState);
}
