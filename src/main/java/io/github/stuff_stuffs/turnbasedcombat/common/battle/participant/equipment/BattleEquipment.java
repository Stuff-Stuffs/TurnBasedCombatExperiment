package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.equipment;

import com.mojang.datafixers.util.Either;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action.ParticipantTargetableParticipantAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action.PositionTargetableParticipantAction;

import java.util.Collections;
import java.util.List;

public interface BattleEquipment {
    default List<Either<PositionTargetableParticipantAction, ParticipantTargetableParticipantAction>> getActions(final BattleStateView stateView, final BattleParticipantStateView participantViSew) {
        return Collections.emptyList();
    }

    boolean validSlot(BattleEquipmentSlot slot);

    void initEvents(BattleParticipantState state);

    void uninitEvents();

    BattleEquipmentType getType();
}
