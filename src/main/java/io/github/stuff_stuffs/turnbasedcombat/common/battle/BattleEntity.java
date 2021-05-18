package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import org.jetbrains.annotations.Nullable;

public interface BattleEntity {
    Team getTeam();

    @Nullable BattleHandle getCurrentBattle();
}
