package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action;

import com.mojang.datafixers.util.Either;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexutil.common.EitherList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class SingleTargetParticipantActionInfo implements ParticipantActionInfo {
    private final TargetType type;
    private final TargetGetter targetGetter;
    private final Action action;

    public SingleTargetParticipantActionInfo(final TargetType type, TargetGetter targetGetter, Action action) {
        this.type = type;
        this.targetGetter = targetGetter;
        this.action = action;
    }

    @Override
    public @Nullable TargetType getNextTargetType(final EitherList<BlockPos, BattleParticipantHandle> list) {
        return list.isEmpty() ? type : null;
    }

    @Override
    public Set<BlockPos> getValidTargetPositions(final BattleStateView battleState, final BattleParticipantHandle user, final EitherList<BlockPos, BattleParticipantHandle> list) {
        return targetGetter.getBlockPositions(battleState, user);
    }

    @Override
    public Set<BattleParticipantHandle> getValidTargetParticipants(final BattleStateView battleState, final BattleParticipantHandle user, final EitherList<BlockPos, BattleParticipantHandle> list) {
        return targetGetter.getParticipants(battleState, user);
    }

    @Override
    public boolean canActivate(final BattleStateView battleState, final BattleParticipantHandle user, final EitherList<BlockPos, BattleParticipantHandle> list) {
        return list.size()==1;
    }

    @Override
    public void activate(final BattleStateView battleState, final BattleParticipantHandle user, final EitherList<BlockPos, BattleParticipantHandle> list) {
        if(list.isEmpty()) {
            throw new RuntimeException();
        }
        action.apply(battleState,user, list.get(0));
    }

    public interface TargetGetter {
        Set<BlockPos> getBlockPositions(final BattleStateView battleState, final BattleParticipantHandle user);

        Set<BattleParticipantHandle> getParticipants(final BattleStateView battleState, final BattleParticipantHandle user);
    }

    public interface Action {
        void apply(final BattleStateView battleState, final BattleParticipantHandle user, Either<BlockPos, BattleParticipantHandle> target);
    }
}
