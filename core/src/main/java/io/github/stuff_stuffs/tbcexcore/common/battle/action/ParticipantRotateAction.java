package io.github.stuff_stuffs.tbcexcore.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexutil.common.HorizontalRotation;

public final class ParticipantRotateAction extends BattleAction<ParticipantRotateAction> {
    public static final Codec<ParticipantRotateAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(BattleParticipantHandle.CODEC.fieldOf("actor").forGetter(BattleAction::getActor), HorizontalRotation.CODEC.fieldOf("rotation").forGetter(action -> action.rotation)).apply(instance, ParticipantRotateAction::new));
    private final HorizontalRotation rotation;

    public ParticipantRotateAction(final BattleParticipantHandle actor, final HorizontalRotation rotation) {
        super(actor, 0.5);
        this.rotation = rotation;
    }

    @Override
    public void applyToState(final BattleState state) {
        final BattleParticipantState participantState = state.getParticipant(actor);
        if (participantState == null) {
            //TODO
            throw new RuntimeException();
        }
        if(participantState.getEnergyTracker().use(energyCost)) {
            participantState.setFacing(rotation.rotate(participantState.getFacing()));
        }
    }

    @Override
    public BattleActionRegistry.Type<ParticipantRotateAction> getType() {
        return BattleActionRegistry.PARTICIPANT_ROTATE_ACTION;
    }
}
