package io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.effect;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffect;

public interface PreEntityCombineEffect {
    /**
     * @return true if should cancel
     */
    boolean onCombineEffect(EntityState state, EntityEffect first, EntityEffect second);
}
