package io.github.stuff_stuffs.turnbasedcombat.common.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import org.jetbrains.annotations.Nullable;

public interface BattleEntityComponent extends ComponentV3, AutoSyncedComponent, CommonTickingComponent {
    @Nullable BattleHandle getBattleHandle();

    boolean isInBattle();
}
