package io.github.stuff_stuffs.tbcexcore.common.battle.action;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;

public abstract class BattleAction<SELF extends BattleAction<SELF>> {
    protected final BattleParticipantHandle actor;
    protected final double energyCost;

    protected BattleAction(final BattleParticipantHandle actor, double energyCost) {
        this.actor = actor;
        this.energyCost = energyCost;
    }

    public BattleParticipantHandle getActor() {
        return actor;
    }

    public abstract void applyToState(BattleState state);

    public abstract BattleActionRegistry.Type<SELF> getType();
}
