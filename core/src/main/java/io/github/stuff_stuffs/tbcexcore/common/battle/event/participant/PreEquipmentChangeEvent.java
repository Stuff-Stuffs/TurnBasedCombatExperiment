package io.github.stuff_stuffs.tbcexcore.common.battle.event.participant;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import org.jetbrains.annotations.Nullable;

public interface PreEquipmentChangeEvent {
    void onEquipmentChange(BattleParticipantStateView state, BattleEquipmentSlot slot, @Nullable BattleEquipment oldEquipment, @Nullable BattleEquipment newEquipment);

    interface Mut {
        boolean onEquipmentChange(BattleParticipantState state, BattleEquipmentSlot slot, @Nullable BattleEquipment oldEquipment, @Nullable BattleEquipment newEquipment);
    }
}
