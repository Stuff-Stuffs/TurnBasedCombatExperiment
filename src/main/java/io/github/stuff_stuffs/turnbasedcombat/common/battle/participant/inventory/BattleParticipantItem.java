package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory;

import com.mojang.datafixers.util.Either;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action.UsableParticipantAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action.TargetableParticipantAction;

import java.util.List;

public interface BattleParticipantItem {
    List<Either<UsableParticipantAction, TargetableParticipantAction>> getActions(BattleStateView battleState, BattleParticipantStateView participantState, BattleParticipantInventoryHandle handle);

    BattleParticipantItemType getType();
}
