package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.List;

public interface ParticipantAction {
    Text getName();

    List<TooltipComponent> getTooltip();
}
