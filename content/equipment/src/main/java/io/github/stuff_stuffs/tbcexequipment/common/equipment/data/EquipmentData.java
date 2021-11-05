package io.github.stuff_stuffs.tbcexequipment.common.equipment.data;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItem;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemCategory;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexequipment.common.equipment.EquipmentType;
import io.github.stuff_stuffs.tbcexequipment.common.part.PartInstance;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EquipmentData {
    EquipmentType<?> getType();

    boolean validSlot(BattleEquipmentSlot slot);

    Set<BattleEquipmentSlot> getBlockedSlots();

    void initEvents(BattleParticipantState state, BattleEquipmentSlot slot);

    void deinitEvents();

    boolean isInCategory(BattleParticipantItemCategory category);

    Text getName();

    List<Text> getTooltip();

    BattleParticipantItem.RarityInstance getRarity();

    Map<Identifier, PartInstance> getParts();
}
