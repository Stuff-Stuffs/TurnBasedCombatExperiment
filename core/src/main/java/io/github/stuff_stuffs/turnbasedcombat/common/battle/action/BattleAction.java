package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;

public abstract class BattleAction<SELF extends BattleAction<SELF>> {
    protected final BattleParticipantHandle actor;

    protected BattleAction(final BattleParticipantHandle actor) {
        this.actor = actor;
    }

    public BattleParticipantHandle getActor() {
        return actor;
    }

    public abstract void applyToState(BattleState state);

    public abstract BattleActionRegistry.Type<SELF> getType();
}
