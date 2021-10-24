package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action;

import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetType;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class SingleTargetParticipantActionInfo<T extends TargetInstance> implements ParticipantActionInfo {
    private final TargetType<T> type;
    private final Action<? super T> action;
    private final List<TooltipComponent> description;

    public SingleTargetParticipantActionInfo(final TargetType<T> type, final Action<? super T> action, final List<TooltipComponent> description) {
        this.type = type;
        this.action = action;
        this.description = description;
    }

    public SingleTargetParticipantActionInfo(final TargetType<T> type, final SimpleAction<? super T> action, final Consumer<BattleAction<?>> sender, final List<TooltipComponent> description) {
        this.type = type;
        this.action = (battleState, user, target) -> sender.accept(action.apply(battleState, user, target));
        this.description = description;
    }

    @Override
    public @Nullable TargetType<T> getNextTargetType(final List<TargetInstance> list) {
        return list.isEmpty() ? type : null;
    }

    @Override
    public boolean canActivate(final BattleStateView battleState, final BattleParticipantHandle user, final List<TargetInstance> list) {
        return list.size() == 1;
    }

    @Override
    public void activate(final BattleStateView battleState, final BattleParticipantHandle user, final List<TargetInstance> list) {
        if (list.isEmpty()) {
            throw new RuntimeException();
        }
        action.apply(battleState, user, (T) list.get(0));
    }

    @Override
    public @Nullable List<TooltipComponent> getDescription(final BattleStateView battleState, final BattleParticipantHandle user, final List<TargetInstance> list) {
        return description;
    }

    public interface Action<T extends TargetInstance> {
        void apply(final BattleStateView battleState, final BattleParticipantHandle user, T target);
    }

    public interface SimpleAction<T extends TargetInstance> {
        BattleAction<?> apply(final BattleStateView battleState, final BattleParticipantHandle user, T target);
    }
}
