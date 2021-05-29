package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action.EntityAction;

import java.util.List;

public interface TargetableBattleEquipment extends BattleEquipment {
    int getMinTargets();

    int getMaxTargets();

    boolean canApply(EntityStateView user, BattleStateView battleState, List<EntityStateView> targets);

    EntityAction apply(EntityStateView user, BattleStateView battleState, List<EntityStateView> targets);
}
