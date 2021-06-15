package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.turnbasedcombat.common.util.CodecUtil;

public final class ParticipantJoinBattleAction extends BattleAction<ParticipantJoinBattleAction> {
    public static final Codec<ParticipantJoinBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("actor").forGetter(action -> action.actor),
            BattleParticipantState.CODEC.fieldOf("participantState").forGetter(action -> action.participantState)
    ).apply(instance, ParticipantJoinBattleAction::new));
    private final BattleParticipantState participantState;

    public ParticipantJoinBattleAction(final BattleParticipantHandle actor, final BattleParticipantState participantState) {
        super(actor);
        this.participantState = participantState;
    }

    @Override
    public void applyToState(final BattleState state) {
        if (!state.join(CodecUtil.copy(participantState, BattleParticipantState.CODEC))) {
            throw new RuntimeException();
        }
    }

    @Override
    public BattleActionRegistry.Type<ParticipantJoinBattleAction> getType() {
        return BattleActionRegistry.PARTICIPANT_JOIN_BATTLE_ACTION;
    }
}
