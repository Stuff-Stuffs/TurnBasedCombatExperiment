package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;

import java.util.UUID;

public interface EntityStateView {
    double getHealth();

    int getLevel();

    int getMaxHealth();

    UUID getId();

    Team getTeam();
}
