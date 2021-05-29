package io.github.stuff_stuffs.turnbasedcombat.common.battle.event;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;

public interface EntityDeathEvent {
    void onDeath(EntityStateView entityState);
}
