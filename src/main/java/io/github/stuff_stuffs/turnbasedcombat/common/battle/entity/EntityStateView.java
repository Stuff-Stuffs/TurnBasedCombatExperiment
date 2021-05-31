package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action.EntityAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.inventory.EntityInventoryView;

import java.util.Collection;
import java.util.UUID;

public interface EntityStateView {
    double getHealth();

    int getLevel();

    UUID getId();

    Team getTeam();

    BattleStateView getBattle();

    BattleParticipantHandle getHandle();

    EntityInventoryView getInventory();

    Collection<EntityAction> getActions();

    WorldEntityInfo getWorldEntityInfo();
}
