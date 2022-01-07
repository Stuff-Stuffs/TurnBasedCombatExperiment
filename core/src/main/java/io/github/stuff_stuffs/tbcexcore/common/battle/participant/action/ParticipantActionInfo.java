package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetType;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import net.minecraft.text.OrderedText;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ParticipantActionInfo {
    @Nullable TargetType<?> getNextTargetType(List<TargetInstance> list);

    boolean canActivate(BattleStateView battleState, BattleParticipantHandle user, List<TargetInstance> list);

    void activate(BattleStateView battleState, BattleParticipantHandle user, List<TargetInstance> list);

    @Nullable List<OrderedText> getDescription(BattleStateView battleState, BattleParticipantHandle user, List<TargetInstance> list);
}
