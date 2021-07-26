package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action.ParticipantAction;

import java.util.List;

public interface BattleParticipantItem {
    List<ParticipantAction> getActions(BattleStateView battleState, BattleParticipantStateView participantState, BattleParticipantInventoryHandle handle);

    BattleParticipantItemType getType();
}
