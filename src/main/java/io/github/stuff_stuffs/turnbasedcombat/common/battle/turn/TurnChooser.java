package io.github.stuff_stuffs.turnbasedcombat.common.battle.turn;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;

import java.util.Collection;

public interface TurnChooser {
    EntityStateView choose(Collection<? extends EntityStateView> participants, BattleStateView state);

    EntityStateView getCurrent(Collection<? extends EntityStateView> participants, BattleStateView state);

    void reset();

    TurnChooserTypeRegistry.Type getType();
}
