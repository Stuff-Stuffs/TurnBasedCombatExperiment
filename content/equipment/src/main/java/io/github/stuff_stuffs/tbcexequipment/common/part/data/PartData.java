package io.github.stuff_stuffs.tbcexequipment.common.part.data;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItem;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexequipment.common.material.Material;
import io.github.stuff_stuffs.tbcexequipment.common.part.Part;
import net.minecraft.text.Text;

import java.util.List;

public interface PartData {
    Part<?> getType();

    default void initEvents(final BattleParticipantState state, final BattleEquipmentSlot slot) {
    }

    default void deinitEvents() {
    }

    Text getName();

    List<Text> getDescription();

    Material getMaterial();

    int getLevel();

    default BattleParticipantItem.RarityInstance getRarity() {
        return new BattleParticipantItem.RarityInstance(getMaterial().getRarity(), 0.5);
    }
}
