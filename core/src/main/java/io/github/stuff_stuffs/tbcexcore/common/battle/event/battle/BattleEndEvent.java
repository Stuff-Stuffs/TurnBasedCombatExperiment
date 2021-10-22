package io.github.stuff_stuffs.tbcexcore.common.battle.event.battle;

import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;

public interface BattleEndEvent {
    void onBattleEnd(BattleStateView battleStateView);

    interface Mut {
        void onBattleEnd(BattleState battleState);
    }
}
