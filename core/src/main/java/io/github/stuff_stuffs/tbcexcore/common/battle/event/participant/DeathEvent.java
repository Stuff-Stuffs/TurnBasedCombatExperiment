package io.github.stuff_stuffs.tbcexcore.common.battle.event.participant;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;

public interface DeathEvent {
    void onDeath(BattleParticipantStateView state);

    interface Mut {
        void onDeath(BattleParticipantState state);
    }
}
