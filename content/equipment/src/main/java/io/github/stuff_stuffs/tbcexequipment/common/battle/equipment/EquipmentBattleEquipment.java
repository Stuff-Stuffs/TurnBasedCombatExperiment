package io.github.stuff_stuffs.tbcexequipment.common.battle.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentType;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexequipment.common.TBCExEquipment;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentActions;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentInstance;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentType;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.data.EquipmentData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class EquipmentBattleEquipment implements BattleEquipment {
    public static final Codec<BattleEquipment> CODEC = RecordCodecBuilder.<EquipmentBattleEquipment>create(instance -> instance.group(EquipmentInstance.CODEC.fieldOf("equipmentInstance").forGetter(equipment -> equipment.equipmentInstance)).apply(instance, EquipmentBattleEquipment::new)).xmap(Function.identity(), equipment -> (EquipmentBattleEquipment) equipment);
    private final EquipmentInstance equipmentInstance;

    public EquipmentBattleEquipment(final EquipmentInstance equipmentInstance) {
        this.equipmentInstance = equipmentInstance;
    }

    @Override
    public boolean validSlot(final BattleEquipmentSlot slot) {
        return equipmentInstance.getData().validSlot(slot);
    }

    @Override
    public Set<BattleEquipmentSlot> getBlockedSlots() {
        return equipmentInstance.getData().getBlockedSlots();
    }

    @Override
    public void initEvents(final BattleParticipantState state, final BattleEquipmentSlot slot) {
        equipmentInstance.getData().initEvents(state, slot);
    }

    @Override
    public List<ParticipantAction> getActions(final BattleStateView stateView, final BattleParticipantStateView participantView, final BattleEquipmentSlot slot) {
        final List<ParticipantAction> actions = new ArrayList<>(thunk(equipmentInstance.getType(), equipmentInstance.getData(), stateView, participantView, slot));
        actions.add(BattleEquipment.createUnequipAction(participantView, slot));
        return actions;
    }

    private static <T extends EquipmentData> List<ParticipantAction> thunk(final EquipmentType<T> type, final EquipmentData data, final BattleStateView stateView, final BattleParticipantStateView participantView, final BattleEquipmentSlot slot) {
        return EquipmentActions.getActions(type, (T) data, stateView, participantView, slot);
    }

    @Override
    public void deinitEvents() {
        equipmentInstance.getData().deinitEvents();
    }

    @Override
    public BattleEquipmentType getType() {
        return BattleEquipmentTypes.EQUIPMENT_BATTLE_EQUIPMENT_TYPE;
    }
}
