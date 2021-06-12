package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.equipment;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action.ParticipantAction;

import java.util.Collections;
import java.util.List;

public interface BattleEquipment {
    default List<? extends ParticipantAction> getActions(BattleStateView stateView, BattleParticipantStateView participantView) {
        return Collections.emptyList();
    }

    void initEvents(BattleParticipantState state);

    void deinitEvents();

    BattleEquipmentType getType();
}
