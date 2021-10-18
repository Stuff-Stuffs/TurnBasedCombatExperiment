package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action;

import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.List;

public interface ParticipantAction {
    Text getName();

    List<TooltipComponent> getTooltip();

    ParticipantActionInstance createInstance(BattleStateView battleState, BattleParticipantHandle handle);
}
