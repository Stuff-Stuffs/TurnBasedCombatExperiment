package io.github.stuff_stuffs.tbcexcore.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;

public final class EndTurnBattleAction extends BattleAction<EndTurnBattleAction> {
    public static final Codec<EndTurnBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(BattleParticipantHandle.CODEC.fieldOf("actor").forGetter(action -> action.actor)).apply(instance, EndTurnBattleAction::new));

    public EndTurnBattleAction(final BattleParticipantHandle actor) {
        super(actor, 0);
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
