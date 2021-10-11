package io.github.stuff_stuffs.tbcexcore.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexutil.common.path.Movement;
import io.github.stuff_stuffs.tbcexutil.common.path.Path;

public class ParticipantMoveBattleAction extends BattleAction<ParticipantMoveBattleAction> {
    public static final Codec<ParticipantMoveBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("actor").forGetter(action -> action.actor),
            Path.CODEC.fieldOf("path").forGetter(action -> action.path)
    ).apply(instance, ParticipantMoveBattleAction::new));
    private final Path path;

    public ParticipantMoveBattleAction(final BattleParticipantHandle actor, final Path path) {
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
        final Movement last = path.getMovements().get(path.getMovements().size() - 1);
        participant.setPos(last.getEndPos());
        participant.setFacing(last.getRotation(last.getLength()));
    }

    @Override
    public BattleActionRegistry.Type<ParticipantMoveBattleAction> getType() {
        return null;
    }
}
