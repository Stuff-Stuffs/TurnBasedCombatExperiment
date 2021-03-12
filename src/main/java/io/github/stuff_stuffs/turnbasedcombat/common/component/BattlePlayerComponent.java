package io.github.stuff_stuffs.turnbasedcombat.common.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import org.jetbrains.annotations.Nullable;

public interface BattlePlayerComponent extends PlayerComponent<BattlePlayerComponent>, CommonTickingComponent, AutoSyncedComponent {
    boolean isInBattle();

    @Nullable BattleHandle getBattleHandle();
}
