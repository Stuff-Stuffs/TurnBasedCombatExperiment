package io.github.stuff_stuffs.tbcexcore.common.battle.event.battle;

import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;

public interface AdvanceTurnEvent {
    void onAdvanceTurn(BattleStateView battleState, BattleParticipantHandle current);

    interface Mut {
        void onAdvanceTurn(BattleState battleState, BattleParticipantHandle current);
    }
}
