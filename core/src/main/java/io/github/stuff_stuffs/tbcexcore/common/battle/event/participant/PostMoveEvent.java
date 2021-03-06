package io.github.stuff_stuffs.tbcexcore.common.battle.event.participant;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattlePath;
import io.github.stuff_stuffs.tbcexutil.common.path.Path;

public interface PostMoveEvent {
    void onMove(BattleParticipantStateView state, BattlePath path);

    interface Mut {
        void onMove(BattleParticipantState state, BattlePath path);
    }
}
