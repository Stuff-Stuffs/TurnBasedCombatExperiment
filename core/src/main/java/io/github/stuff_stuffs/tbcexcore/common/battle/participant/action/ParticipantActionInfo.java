package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexutil.common.EitherList;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface ParticipantActionInfo {
    @Nullable TargetType getNextTargetType(EitherList<BlockPos, BattleParticipantHandle> list);

    Set<BlockPos> getValidTargetPositions(BattleStateView battleState, BattleParticipantHandle user, EitherList<BlockPos, BattleParticipantHandle> list);

    Set<BattleParticipantHandle> getValidTargetParticipants(BattleStateView battleState, BattleParticipantHandle user, EitherList<BlockPos, BattleParticipantHandle> list);

    boolean canActivate(BattleStateView battleState, BattleParticipantHandle user, EitherList<BlockPos, BattleParticipantHandle> list);

    void activate(BattleStateView battleState, BattleParticipantHandle user, EitherList<BlockPos, BattleParticipantHandle> list);

    @Nullable List<TooltipComponent> getDescription(BattleStateView battleState, BattleParticipantHandle user, EitherList<BlockPos, BattleParticipantHandle> list);
}
