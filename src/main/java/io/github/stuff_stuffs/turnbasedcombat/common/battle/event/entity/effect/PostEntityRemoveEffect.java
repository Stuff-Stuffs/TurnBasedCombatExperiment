package io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.effect;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffect;

public interface PostEntityRemoveEffect {
    void onRemoveEffect(EntityState state, EntityEffect effect);
}
