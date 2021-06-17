package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface PositionTargetableParticipantAction extends ParticipantAction {
    //inclusive
    int getMinTargets();

    //inclusive
    int getMaxTargets();

    Iterable<BlockPos> getValidTargets();

    BattleAction<?> getTargetAction(List<BlockPos> targets);
}
