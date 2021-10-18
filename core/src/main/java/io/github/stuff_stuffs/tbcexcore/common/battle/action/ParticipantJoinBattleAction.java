package io.github.stuff_stuffs.tbcexcore.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.entity.BattleEntity;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;
import net.minecraft.entity.Entity;

public final class ParticipantJoinBattleAction extends BattleAction<ParticipantJoinBattleAction> {
    public static final Codec<ParticipantJoinBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("actor").forGetter(action -> action.actor),
            BattleParticipantState.CODEC.fieldOf("participantState").forGetter(action -> action.participantState)
    ).apply(instance, ParticipantJoinBattleAction::new));
    private final BattleParticipantState participantState;

    public ParticipantJoinBattleAction(final BattleParticipantHandle actor, final BattleParticipantState participantState) {
        super(actor, 0);
        this.participantState = participantState;
    }

    @Override
    public void applyToState(final BattleState state) {
        final BattleParticipantState copy = CodecUtil.copy(participantState, BattleParticipantState.CODEC);
        copy.setBattleState(state);
        if (!state.join(copy)) {
            throw new RuntimeException();
        }
    }

    @Override
    public BattleActionRegistry.Type<ParticipantJoinBattleAction> getType() {
        return BattleActionRegistry.PARTICIPANT_JOIN_BATTLE_ACTION;
    }

    public static BattleAction<?> create(BattleEntity entity, Battle battle) {
        BattleParticipantHandle handle = new BattleParticipantHandle(battle.getHandle(), ((Entity)entity).getUuid());
        BattleParticipantState state = new BattleParticipantState(handle, entity, (BattleState) battle.getState());
        return new ParticipantJoinBattleAction(BattleParticipantHandle.UNIVERSAL.apply(battle.getHandle()), state);
    }
}
