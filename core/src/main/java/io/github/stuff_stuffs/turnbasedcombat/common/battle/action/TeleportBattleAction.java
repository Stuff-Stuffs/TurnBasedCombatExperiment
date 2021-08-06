package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import net.minecraft.util.math.BlockPos;

public final class TeleportBattleAction extends BattleAction<TeleportBattleAction> {
    public static final Codec<TeleportBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("actor").forGetter(action -> action.actor),
            BattleParticipantHandle.CODEC.fieldOf("target").forGetter(action -> action.target),
            BlockPos.CODEC.fieldOf("pos").forGetter(action -> action.pos)
    ).apply(instance, TeleportBattleAction::new));
    private final BattleParticipantHandle target;
    private final BlockPos pos;

    public TeleportBattleAction(final BattleParticipantHandle actor, final BattleParticipantHandle target, final BlockPos pos) {
        super(actor);
        this.target = target;
        this.pos = pos;
    }

    @Override
    public void applyToState(final BattleState state) {
        final BattleParticipantState participant = state.getParticipant(target);
        if (participant == null) {
            throw new RuntimeException();
        }
        participant.setPos(pos);
    }

    @Override
    public BattleActionRegistry.Type<TeleportBattleAction> getType() {
        return BattleActionRegistry.TELEPORT_BATTLE_ACTION;
    }
}
