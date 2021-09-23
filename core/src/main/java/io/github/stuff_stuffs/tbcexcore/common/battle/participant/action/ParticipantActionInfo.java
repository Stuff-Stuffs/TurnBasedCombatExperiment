package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetType;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ParticipantActionInfo {
    @Nullable TargetType getNextTargetType(List<TargetInstance> list);

    boolean canActivate(BattleStateView battleState, BattleParticipantHandle user, List<TargetInstance> list);

    void activate(BattleStateView battleState, BattleParticipantHandle user, List<TargetInstance> list);

    @Nullable List<TooltipComponent> getDescription(BattleStateView battleState, BattleParticipantHandle user, List<TargetInstance> list);
}
