package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooser;
import org.jetbrains.annotations.Nullable;

public interface BattleStateView {
    //TODO throws not enough participants exception?
    BattleParticipant getCurrentTurn(TurnChooser chooser);

    @Nullable BattleParticipantView getParticipant(BattleParticipantHandle handle);

    boolean isBattleEnded();

    int getParticipantCount();

    int getTeamCount();
}
