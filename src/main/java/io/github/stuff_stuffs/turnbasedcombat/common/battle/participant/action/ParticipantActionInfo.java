package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.action;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.util.EitherList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ParticipantActionInfo {
    @Nullable TargetType getNextTargetType(EitherList<BlockPos, BattleParticipantHandle> list);

    Set<BlockPos> getValidTargetPositions(BattleStateView battleState, BattleParticipantHandle user, EitherList<BlockPos, BattleParticipantHandle> list);

    Set<BattleParticipantHandle> getValidTargetParticipants(BattleStateView battleState, BattleParticipantHandle user, EitherList<BlockPos, BattleParticipantHandle> list);

    boolean canActivate(BattleStateView battleState, BattleParticipantHandle user, EitherList<BlockPos, BattleParticipantHandle> list);

    void activate(BattleStateView battleState, BattleParticipantHandle user, EitherList<BlockPos, BattleParticipantHandle> list);
}
