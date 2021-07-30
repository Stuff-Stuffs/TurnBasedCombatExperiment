package io.github.stuff_stuffs.turnbasedcombat.mixin.api;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import org.jetbrains.annotations.Nullable;

public interface BattleAwarePlayer {
    @Nullable BattleHandle tbcex_getCurrentBattle();

    void tbcex_setCurrentBattle(@Nullable BattleHandle handle);
}
