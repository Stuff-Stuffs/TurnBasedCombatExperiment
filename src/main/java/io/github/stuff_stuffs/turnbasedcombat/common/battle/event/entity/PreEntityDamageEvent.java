package io.github.stuff_stuffs.turnbasedcombat.common.battle.event.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage.DamagePacket;
import org.jetbrains.annotations.Nullable;

public interface PreEntityDamageEvent {
    int ARMOR = 0;
    int MAGIC_EFFECTS = -1;

    /**
     * @return The updated damage packet, null to cancel
     */
    @Nullable DamagePacket onEntityDamage(EntityState state, DamagePacket damagePacket);

    int getPriority();
}
