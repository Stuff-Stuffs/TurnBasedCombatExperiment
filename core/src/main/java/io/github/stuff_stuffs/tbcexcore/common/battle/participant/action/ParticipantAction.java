package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action;

import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public interface ParticipantAction {
    Text getName();

    List<OrderedText> getTooltip();

    ParticipantActionInstance createInstance(BattleStateView battleState, BattleParticipantHandle handle, Consumer<BattleAction<?>> sender);
}
