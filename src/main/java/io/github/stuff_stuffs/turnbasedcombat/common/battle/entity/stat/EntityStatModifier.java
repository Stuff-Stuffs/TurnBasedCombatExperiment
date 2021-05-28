package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;

public interface EntityStatModifier<T> {
    T modify(T input, EntityStateView view);

    int getApplicationStage();
}
