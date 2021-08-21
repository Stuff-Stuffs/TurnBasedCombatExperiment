package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexutil.common.EitherList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.BiConsumer;

public final class NonTargettableParticipantActionInfo implements ParticipantActionInfo {
    private final BiConsumer<BattleStateView, BattleParticipantHandle> action;

    public NonTargettableParticipantActionInfo(final BiConsumer<BattleStateView, BattleParticipantHandle> action) {
        this.action = action;
    }

    @Override
    public @Nullable TargetType getNextTargetType(final EitherList<BlockPos, BattleParticipantHandle> list) {
        return null;
    }

    @Override
    public Set<BlockPos> getValidTargetPositions(final BattleStateView battleState, final BattleParticipantHandle user, final EitherList<BlockPos, BattleParticipantHandle> list) {
        throw new RuntimeException();
    }

    @Override
    public Set<BattleParticipantHandle> getValidTargetParticipants(final BattleStateView battleState, final BattleParticipantHandle user, final EitherList<BlockPos, BattleParticipantHandle> list) {
        throw new RuntimeException();
    }

    @Override
    public boolean canActivate(final BattleStateView battleState, final BattleParticipantHandle user, final EitherList<BlockPos, BattleParticipantHandle> list) {
        return true;
    }

    @Override
    public void activate(final BattleStateView battleState, final BattleParticipantHandle user, final EitherList<BlockPos, BattleParticipantHandle> list) {
        if(!list.isEmpty()) {
            throw new RuntimeException();
        }
        action.accept(battleState, user);
    }
}
