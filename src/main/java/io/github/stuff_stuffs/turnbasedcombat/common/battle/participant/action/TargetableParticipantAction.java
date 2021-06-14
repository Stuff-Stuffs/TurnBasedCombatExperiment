package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;

import java.util.List;

public interface TargetableParticipantAction extends ParticipantAction {
    //inclusive
    int getMinTargets();

    //inclusive
    int getMaxTargets();

    Iterable<BattleParticipantHandle> getValidTargets();

    BattleAction<?> getTargetAction(List<BattleParticipantHandle> targets);
}
