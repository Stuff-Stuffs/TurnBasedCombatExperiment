package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;

import java.util.List;

public interface TargetableBattleEquipment extends BattleEquipment {
    int getMinTargets();

    int getMaxTargets();

    boolean canApply(EntityStateView user, BattleStateView battleState, List<EntityStateView> targets);

    BattleAction apply(EntityStateView user, BattleStateView battleState, List<EntityStateView> targets);
}
