package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;

public interface EntityEffectFactory {
    EntityEffect create(EntityState state);

    EntityEffectFactoryType getType();
}
