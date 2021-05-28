package io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.effect;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffect;

public interface PreEntityAddEffect {
    /**
     * @return true if should cancel
     */
    boolean onEntityAddEffect(EntityState state, EntityEffect effect);
}
