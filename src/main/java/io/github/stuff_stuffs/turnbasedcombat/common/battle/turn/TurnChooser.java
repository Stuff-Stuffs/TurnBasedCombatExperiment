package io.github.stuff_stuffs.turnbasedcombat.common.battle.turn;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;

import java.util.Collection;

public interface TurnChooser {
    BattleParticipantView choose(Collection<? extends BattleParticipantView> participants, BattleStateView state);

    BattleParticipantView getCurrent(Collection<? extends BattleParticipantView> participants, BattleStateView state);

    TurnChooserTypeRegistry.Type getType();
}
