package io.github.stuff_stuffs.turnbasedcombat.common.battle.event;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;

public interface AdvanceTurnEvent {
    //TODO cancellable
    void onAdvanceTurn(BattleState battleState);
}
