package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;

public interface UsableParticipantAction extends ParticipantAction {
    BattleAction<?> getUseAction();
}
