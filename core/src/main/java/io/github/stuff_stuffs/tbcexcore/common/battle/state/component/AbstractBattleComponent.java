package io.github.stuff_stuffs.tbcexcore.common.battle.state.component;

import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;

public abstract class AbstractBattleComponent implements BattleComponent {
    protected BattleState state;

    @Override
    public void init(final BattleState state) {
        this.state = state;
    }

    @Override
    public void deinitEvents() {
        state = null;
    }
}
