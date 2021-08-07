package io.github.stuff_stuffs.tbcexcore.common.battle.event.battle;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;

public interface PreParticipantJoinEvent {
    void onParticipantJoin(BattleStateView battleStateView, BattleParticipantStateView participantView);

    interface Mut {
        boolean onParticipantJoin(BattleState battleState, BattleParticipantState participantState);
    }
}
