package io.github.stuff_stuffs.turnbasedcombat.common.battle.event.battle;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantStateView;

//TODO reason
public interface PreParticipantLeaveEvent {
    void onParticipantLeave(BattleStateView battleState, BattleParticipantStateView participantView);

    interface Mut {
        boolean onParticipantLeave(BattleState battleState, BattleParticipantState participantState);
    }
}
