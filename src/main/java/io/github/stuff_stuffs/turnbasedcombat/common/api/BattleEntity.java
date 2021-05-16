package io.github.stuff_stuffs.turnbasedcombat.common.api;

/**
 * Interface for entities that can join a battle
 */
public interface BattleEntity {
    Team getTeam();
    boolean isActiveEntity();
}
