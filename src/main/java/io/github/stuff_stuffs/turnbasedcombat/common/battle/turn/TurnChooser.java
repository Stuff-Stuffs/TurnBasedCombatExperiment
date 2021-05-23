package io.github.stuff_stuffs.turnbasedcombat.common.battle.turn;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipant;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;

import java.util.Collection;

public interface TurnChooser {
    BattleParticipant choose(Collection<BattleParticipant> participants, BattleStateView state);

    BattleParticipant getCurrent(Collection<BattleParticipant> participants, BattleStateView state);

    void reset();

    TurnChooserTypeRegistry.Type getType();
}
