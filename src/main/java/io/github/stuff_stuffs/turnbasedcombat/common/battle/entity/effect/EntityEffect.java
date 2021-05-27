package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;

public interface EntityEffect {
    void tick(EntityState entityState, BattleStateView battleView);

    boolean shouldRemove();

    EntityEffect copy();

    EntityEffectRegistry.Type<?> getType();
}
