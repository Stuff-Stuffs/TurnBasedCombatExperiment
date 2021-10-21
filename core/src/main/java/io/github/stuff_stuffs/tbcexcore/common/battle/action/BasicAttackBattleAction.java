package io.github.stuff_stuffs.tbcexcore.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.damage.BattleDamagePacket;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantInfoComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;

public final class BasicAttackBattleAction extends BattleAction<BasicAttackBattleAction> {
    public static final Codec<BasicAttackBattleAction> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BattleParticipantHandle.CODEC.fieldOf("actor").forGetter(action -> action.actor),
                    BattleParticipantHandle.CODEC.fieldOf("target").forGetter(action -> action.target),
                    BattleDamagePacket.CODEC.fieldOf("damagePacket").forGetter(action -> action.damagePacket),
                    Codec.DOUBLE.fieldOf("energyCost").forGetter(action -> action.energyCost)
            ).apply(instance, BasicAttackBattleAction::new)
    );
    private final BattleParticipantHandle target;
    private final BattleDamagePacket damagePacket;

    public BasicAttackBattleAction(final BattleParticipantHandle actor, final BattleParticipantHandle target, final BattleDamagePacket damagePacket, final double energyCost) {
        super(actor, energyCost);
        this.target = target;
        this.damagePacket = damagePacket;
    }

    @Override
    public void applyToState(final BattleState state) {
        final BattleParticipantState participant = state.getParticipant(target);
        if (participant == null) {
            LoggerUtil.LOGGER.error("Missing battle participant while trying to attack: {} in battle {}", target, state.getHandle());
            return;
        }
        final ParticipantInfoComponent component = participant.getMutComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if(component==null) {
            LoggerUtil.LOGGER.error("Missing required component(info) to do basic attack: {}", target);
            return;
        }
        component.damage(damagePacket);
    }

    @Override
    public BattleActionRegistry.Type<BasicAttackBattleAction> getType() {
        return BattleActionRegistry.BASIC_ATTACK_ACTION;
    }
}
