package io.github.stuff_stuffs.tbcexcore.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattlePath;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantInfoComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantPosComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;
import io.github.stuff_stuffs.tbcexutil.common.path.Movement;

import java.util.List;

public final class ParticipantMoveBattleAction extends BattleAction<ParticipantMoveBattleAction> {
    public static final Codec<ParticipantMoveBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("actor").forGetter(action -> action.actor),
            BattlePath.CODEC.fieldOf("path").forGetter(action -> action.path)
    ).apply(instance, ParticipantMoveBattleAction::new));
    private final BattlePath path;

    public ParticipantMoveBattleAction(final BattleParticipantHandle actor, final BattlePath path) {
        //TODO path independent costs
        super(actor, path.getCost());
        this.path = path;
    }

    @Override
    public void applyToState(final BattleState state) {
        final BattleParticipantState participant = state.getParticipant(actor);
        if (participant == null) {
            throw new RuntimeException();
        }
        final ParticipantInfoComponent infoComponent = participant.getMutComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        final ParticipantPosComponent posComponent = participant.getMutComponent(ParticipantComponents.POS_COMPONENT_TYPE.key);
        if (posComponent == null || infoComponent == null) {
            //TODO
            throw new RuntimeException();
        }
        final List<Movement> movements = path.getPath().getMovements();
        for (int i = 0; i < movements.size(); i++) {
            final Movement movement = movements.get(i);
            final boolean move = participant.getEvent(BattleParticipantStateView.PRE_MOVE_EVENT).invoker().onMove(participant, movement.getEndPos(), path);
            if (move) {
                break;
            }
            if (infoComponent.useEnergy(path.getCostAtMovement(i))) {
                posComponent.setPos(movement.getEndPos());
                posComponent.setFacing(movement.getRotation(movement.getLength()));
                participant.getEvent(BattleParticipantStateView.POST_MOVE_EVENT).invoker().onMove(participant, path);
            } else {
                return;
            }
        }
    }

    @Override
    public BattleActionRegistry.Type<ParticipantMoveBattleAction> getType() {
        return BattleActionRegistry.PARTICIPANT_MOVE_BATTLE_ACTION;
    }
}
