package io.github.stuff_stuffs.tbcexcore.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantInfoComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;

public class ParticipantUnequipAction extends BattleAction<ParticipantUnequipAction> {
    public static final Codec<ParticipantUnequipAction> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BattleParticipantHandle.CODEC.fieldOf("actor").forGetter(action -> action.actor),
                    BattleEquipmentSlot.REGISTRY.fieldOf("slot").forGetter(action -> action.slot),
                    Codec.DOUBLE.fieldOf("cost").forGetter(action -> action.energyCost)
            ).apply(instance, ParticipantUnequipAction::new)
    );
    private final BattleEquipmentSlot slot;

    public ParticipantUnequipAction(final BattleParticipantHandle actor, final BattleEquipmentSlot slot, final double energyCost) {
        super(actor, energyCost);
        this.slot = slot;
    }

    @Override
    public void applyToState(final BattleState state) {
        final BattleParticipantState participant = state.getParticipant(actor);
        if (participant == null) {
            LoggerUtil.LOGGER.error("Missing battle participant while trying to equip equipment: {} in battle {}", actor, state.getHandle());
            return;
        }
        final ParticipantInfoComponent component = participant.getMutComponent(ParticipantComponents.INFO_COMPONENT_TYPE.key);
        if (component == null) {
            throw new TBCExException("component is null");
        }
        if (component.useEnergy(energyCost)) {
            component.equip(slot, null);
        }
    }

    @Override
    public BattleActionRegistry.Type<ParticipantUnequipAction> getType() {
        return BattleActionRegistry.PARTICIPANT_UNEQUIP_ACTION;
    }
}
