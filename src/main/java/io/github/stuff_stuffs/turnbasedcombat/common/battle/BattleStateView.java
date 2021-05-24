package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface BattleStateView {
    //TODO throws not enough participants exception?
    EntityStateView getCurrentTurn();

    @Nullable EntityStateView getParticipant(BattleParticipantHandle handle);

    boolean isBattleEnded();

    int getParticipantCount();

    int getTeamCount();

    int getTurnCount();

    boolean contains(UUID id);
}
