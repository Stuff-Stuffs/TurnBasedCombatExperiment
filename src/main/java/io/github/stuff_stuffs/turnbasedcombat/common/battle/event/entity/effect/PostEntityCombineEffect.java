package io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity.effect;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect.EntityEffect;

public interface PostEntityCombineEffect {
    void onCombineEffect(EntityState state, EntityEffect first, EntityEffect second, EntityEffect combined);
}
