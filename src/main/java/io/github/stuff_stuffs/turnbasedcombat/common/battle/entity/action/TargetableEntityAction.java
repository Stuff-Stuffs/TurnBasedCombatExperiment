package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;

import java.util.List;

public interface TargetableEntityAction extends EntityAction {
    int getMinTargets();

    int getMaxTargets();

    boolean canApply(EntityStateView user, BattleStateView battleState, List<EntityStateView> targets);

    BattleAction apply(EntityStateView user, BattleStateView battleState, List<EntityStateView> targets);
}
