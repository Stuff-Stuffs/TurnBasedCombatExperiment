package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import org.jetbrains.annotations.Nullable;

public interface BattleStateView {
    //TODO throws not enough participants exception?
    BattleParticipant getCurrentTurn();

    @Nullable BattleParticipantView getParticipant(BattleParticipantHandle handle);

    boolean isBattleEnded();

    int getParticipantCount();

    int getTeamCount();

    int getTurnCount();
}
