package io.github.stuff_stuffs.turnbasedcombat.common.battle.event.battle;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantStateView;

public interface PostParticipantJoinEvent {
    void onParticipantJoin(BattleStateView battleStateView, BattleParticipantStateView participantView);

    interface Mut {
        void onParticipantJoin(BattleState battleState, BattleParticipantState participant);
    }
}