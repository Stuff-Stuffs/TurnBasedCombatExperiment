package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory;

import com.mojang.datafixers.util.Either;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action.PositionTargetableParticipantAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action.ParticipantTargetableParticipantAction;

import java.util.List;

public interface BattleParticipantItem {
    List<Either<PositionTargetableParticipantAction, ParticipantTargetableParticipantAction>> getActions(BattleStateView battleState, BattleParticipantStateView participantState, BattleParticipantInventoryHandle handle);

    BattleParticipantItemType getType();
}
