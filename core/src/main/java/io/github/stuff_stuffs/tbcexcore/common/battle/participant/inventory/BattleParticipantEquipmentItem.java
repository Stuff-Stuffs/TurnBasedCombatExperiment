package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipment;

public interface BattleParticipantEquipmentItem extends BattleParticipantItem {
    BattleEquipment createEquipmentInstance(BattleParticipantItemStack stack);
}
