package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;

public interface EntityEffect {
    boolean shouldRemove();

    void initEvents(EntityState entityState);

    void deinitEvents();

    EntityEffectType getType();
}
