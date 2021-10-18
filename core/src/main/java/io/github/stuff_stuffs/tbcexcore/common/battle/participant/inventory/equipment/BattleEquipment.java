package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment;

import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface BattleEquipment {
    default List<ParticipantAction> getActions(final BattleStateView stateView, final BattleParticipantStateView participantView) {
        return Collections.emptyList();
    }

    boolean validSlot(BattleEquipmentSlot slot);

    Set<BattleEquipmentSlot> getBlockedSlots();

    void initEvents(BattleParticipantState state);

    void deinitEvents();

    BattleEquipmentType getType();
}
