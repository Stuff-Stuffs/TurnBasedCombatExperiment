package io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage.DamagePacket;

public interface PostEntityDamageEvent {
    void onEntityDamage(EntityState state, DamagePacket damagePacket);
}
