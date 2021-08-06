package io.github.stuff_stuffs.turnbasedcombat.common.battle.world;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleHandle;
import org.jetbrains.annotations.Nullable;

public interface BattleWorld {
    @Nullable Battle getBattle(BattleHandle handle);
}
