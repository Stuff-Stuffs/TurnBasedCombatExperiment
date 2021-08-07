package io.github.stuff_stuffs.tbcexcore.common.battle.event.battle;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;

//TODO reason
public interface PostParticipantLeaveEvent {
    void onParticipantLeave(BattleStateView battleState, BattleParticipantStateView participantView);

    interface Mut {
        void onParticipantLeave(BattleState battleState, BattleParticipantState participantState);
    }
}