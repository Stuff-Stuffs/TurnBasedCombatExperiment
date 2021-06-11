package io.github.stuff_stuffs.turnbasedcombat.common.battle.event.participant;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.equipment.BattleEquipment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.equipment.BattleEquipmentSlot;
import org.jetbrains.annotations.Nullable;

public interface PostEquipmentChangeEvent {
    void onEquipmentChange(BattleParticipantStateView state, BattleEquipmentSlot slot, @Nullable BattleEquipment oldEquipment, @Nullable BattleEquipment newEquipment);

    interface Mut {
        void onEquipmentChange(BattleParticipantState state, BattleEquipmentSlot slot, @Nullable BattleEquipment oldEquipment, @Nullable BattleEquipment newEquipment);
    }
}
