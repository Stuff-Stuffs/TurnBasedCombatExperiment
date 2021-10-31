package io.github.stuff_stuffs.tbcexequipment.common.part;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItem;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;

public interface PartData {
    Part<?> getType();

    default void initEvents(final BattleParticipantState state, BattleEquipmentSlot slot) {
    }

    default void deinitEvents() {

    }

    //TODO implement me
    default BattleParticipantItem.RarityInstance getRarity() {
        return new BattleParticipantItem.RarityInstance(BattleParticipantItem.Rarity.COMMON, 0);
    }
}
