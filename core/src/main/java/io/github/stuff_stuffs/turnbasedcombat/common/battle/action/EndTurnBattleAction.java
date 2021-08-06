package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;

public class EndTurnBattleAction extends BattleAction<EndTurnBattleAction> {
    public static final Codec<EndTurnBattleAction> CODEC = BattleParticipantHandle.CODEC.xmap(EndTurnBattleAction::new, BattleAction::getActor);

    public EndTurnBattleAction(final BattleParticipantHandle actor) {
        super(actor);
    }

    @Override
    public void applyToState(final BattleState state) {
        state.advanceTurn();
    }

    @Override
    public BattleActionRegistry.Type<EndTurnBattleAction> getType() {
        return BattleActionRegistry.END_TURN_BATTLE_ACTION;
    }
}
