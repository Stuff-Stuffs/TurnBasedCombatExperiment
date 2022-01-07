package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action;

import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetType;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import net.minecraft.text.OrderedText;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

public final class NonTargettableParticipantActionInfo implements ParticipantActionInfo {
    private final BiConsumer<BattleStateView, BattleParticipantHandle> action;

    public NonTargettableParticipantActionInfo(final BiConsumer<BattleStateView, BattleParticipantHandle> action) {
        this.action = action;
    }

    @Override
    public @Nullable TargetType getNextTargetType(final List<TargetInstance> list) {
        return null;
    }

    @Override
    public boolean canActivate(final BattleStateView battleState, final BattleParticipantHandle user, final List<TargetInstance> list) {
        return true;
    }

    @Override
    public void activate(final BattleStateView battleState, final BattleParticipantHandle user, final List<TargetInstance> list) {
        action.accept(battleState, user);
    }

    @Override
    public @Nullable List<OrderedText> getDescription(final BattleStateView battleState, final BattleParticipantHandle user, final List<TargetInstance> list) {
        return null;
    }
}
