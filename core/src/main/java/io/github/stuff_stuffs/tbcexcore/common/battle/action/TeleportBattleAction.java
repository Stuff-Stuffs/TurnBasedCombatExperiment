package io.github.stuff_stuffs.tbcexcore.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantInfoComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantPosComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import net.minecraft.util.math.BlockPos;

public final class TeleportBattleAction extends BattleAction<TeleportBattleAction> {
    public static final Codec<TeleportBattleAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("actor").forGetter(action -> action.actor),
            BattleParticipantHandle.CODEC.fieldOf("target").forGetter(action -> action.target),
            BlockPos.CODEC.fieldOf("pos").forGetter(action -> action.pos),
            Codec.DOUBLE.fieldOf("energyCost").forGetter(action -> action.energyCost)
    ).apply(instance, TeleportBattleAction::new));
    private final BattleParticipantHandle target;
    private final BlockPos pos;

    public TeleportBattleAction(final BattleParticipantHandle actor, final BattleParticipantHandle target, final BlockPos pos, final double energyCost) {
        super(actor, energyCost);
        this.target = target;
        this.pos = pos;
    }

    @Override
    public void applyToState(final BattleState state) {
        final BattleParticipantState participant = state.getParticipant(target);
        if (participant == null) {
            throw new RuntimeException();
        }
        final ParticipantInfoComponent infoComponent = participant.getMutComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        final ParticipantPosComponent posComponent = participant.getMutComponent(ParticipantComponents.POS_COMPONENT_TYPE.key);
        if (infoComponent == null || posComponent == null) {
            throw new TBCExException("component is null");
        }
        if (infoComponent.useEnergy(energyCost)) {
            posComponent.setPos(pos);
        }
    }

    @Override
    public BattleActionRegistry.Type<TeleportBattleAction> getType() {
        return BattleActionRegistry.TELEPORT_BATTLE_ACTION;
    }
}
