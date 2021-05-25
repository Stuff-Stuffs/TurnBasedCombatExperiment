package io.github.stuff_stuffs.turnbasedcombat.common.battle.effect;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;

public interface EntityEffect {
    int BASE = 0;
    int ADDITIVE = 1;
    int MULTIPLY_BASE = 2;
    int MULTIPLY_TOTAL = 3;

    void tick(EntityState entityState, BattleStateView battleView);

    boolean shouldRemove();

    int getApplicationStage();

    EntityEffectRegistry.Type<?> getType();
}
