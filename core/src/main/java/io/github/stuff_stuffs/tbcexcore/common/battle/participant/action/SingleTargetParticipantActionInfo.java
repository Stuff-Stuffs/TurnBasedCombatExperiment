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

public class SingleTargetParticipantActionInfo implements ParticipantActionInfo {
    private final TargetType type;
    private final Action action;
    private final List<TooltipComponent> description;

    public SingleTargetParticipantActionInfo(final TargetType type, final Action action, final List<TooltipComponent> description) {
        this.type = type;
        this.action = action;
        this.description = description;
    }

    public SingleTargetParticipantActionInfo(final TargetType type, final SimpleAction action, final Consumer<BattleAction<?>> sender, final List<TooltipComponent> description) {
        this.type = type;
        this.action = (battleState, user, target) -> sender.accept(action.apply(battleState, user, target));
        this.description = description;
    }

    @Override
    public @Nullable TargetType getNextTargetType(final List<TargetInstance> list) {
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
        action.apply(battleState, user, list.get(0));
    }

    @Override
    public @Nullable List<TooltipComponent> getDescription(final BattleStateView battleState, final BattleParticipantHandle user, final List<TargetInstance> list) {
        return description;
    }

    public interface Action {
        void apply(final BattleStateView battleState, final BattleParticipantHandle user, TargetInstance target);
    }

    public interface SimpleAction {
        BattleAction<?> apply(final BattleStateView battleState, final BattleParticipantHandle user, TargetInstance target);
    }
}
