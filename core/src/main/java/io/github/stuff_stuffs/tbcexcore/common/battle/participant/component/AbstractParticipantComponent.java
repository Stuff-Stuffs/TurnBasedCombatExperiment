package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;

public abstract class AbstractParticipantComponent implements ParticipantComponent {
    protected BattleParticipantState state = null;

    @Override
    public void init(final BattleParticipantState state) {
        this.state = state;
    }

    @Override
    public void deinitEvents() {
        state = null;
    }
}
