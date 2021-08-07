package io.github.stuff_stuffs.tbcexcore.mixin.api;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import org.jetbrains.annotations.Nullable;

public interface BattleAwareEntity {
    @Nullable BattleHandle tbcex_getCurrentBattle();

    void tbcex_setCurrentBattle(@Nullable BattleHandle handle);
}
