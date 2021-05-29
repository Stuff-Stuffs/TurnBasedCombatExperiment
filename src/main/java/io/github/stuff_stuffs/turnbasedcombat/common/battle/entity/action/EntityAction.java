package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.action;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.List;

public interface EntityAction {
    Text getName();

    List<TooltipComponent> getTooltip();

    BattleAction getAction(EntityStateView entityState);
}
