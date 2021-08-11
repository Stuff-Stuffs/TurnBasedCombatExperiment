package io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.ParticipantAction;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.List;

public interface BattleParticipantItem {
    List<ParticipantAction> getActions(BattleStateView battleState, BattleParticipantStateView participantState, BattleParticipantInventoryHandle handle);

    BattleParticipantItemType getType();

    BattleParticipantItemCategory getCategory();

    Text getName();

    List<TooltipComponent> getTooltip();
}
