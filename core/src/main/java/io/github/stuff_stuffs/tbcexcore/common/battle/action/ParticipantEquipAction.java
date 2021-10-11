package io.github.stuff_stuffs.tbcexcore.common.battle.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantEquipmentItem;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexutil.common.LoggerUtil;

public final class ParticipantEquipAction extends BattleAction<ParticipantEquipAction> {
    public static final Codec<ParticipantEquipAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleParticipantHandle.CODEC.fieldOf("actor").forGetter(BattleAction::getActor),
            BattleEquipmentSlot.REGISTRY.fieldOf("slot").forGetter(action -> action.slot),
            BattleParticipantInventoryHandle.CODEC.fieldOf("equipment").forGetter(action -> action.handle),
            Codec.DOUBLE.fieldOf("energyCost").forGetter(action -> action.energyCost)
    ).apply(instance, ParticipantEquipAction::new));
    private final BattleEquipmentSlot slot;
    private final BattleParticipantInventoryHandle handle;

    public ParticipantEquipAction(final BattleParticipantHandle actor, final BattleEquipmentSlot slot, final BattleParticipantInventoryHandle handle, final double energyCost) {
        super(actor, energyCost);
        this.slot = slot;
        this.handle = handle;
    }

    @Override
    public void applyToState(final BattleState state) {
        final BattleParticipantState participant = state.getParticipant(actor);
        if (participant == null) {
            LoggerUtil.LOGGER.error("Missing battle participant while trying to equip equipment: {} in battle {}", actor, state.getHandle());
            return;
        }
        final BattleParticipantItemStack itemStack = participant.getItemStack(handle);
        if(!(itemStack.getItem() instanceof BattleParticipantEquipmentItem)) {
            LoggerUtil.LOGGER.error("Cannot equip non equipment item {}", handle);
            return;
        }
        if (!participant.equip(slot, itemStack)) {
            LoggerUtil.LOGGER.error("Cannot equip equipment: {} onto participant {} in battle {}", handle, participant, state.getHandle());
            return;
        }
        participant.takeItems(handle, 1);
        participant.getEnergyTracker().use(energyCost);
    }

    @Override
    public BattleActionRegistry.Type<ParticipantEquipAction> getType() {
        return BattleActionRegistry.PARTICIPANT_EQUIP_ACTION;
    }
}
