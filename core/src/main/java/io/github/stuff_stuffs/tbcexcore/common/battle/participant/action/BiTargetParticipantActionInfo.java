package io.github.stuff_stuffs.tbcexcore.common.battle.participant.action;

import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetInstance;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.action.target.TargetType;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class BiTargetParticipantActionInfo<T1 extends TargetInstance, T2 extends TargetInstance> implements ParticipantActionInfo {
    private final TargetType<T1> firstTargetType;
    private final TargetType<T2> secondTargetType;
    private final BiFunction<BattleStateView, BattleParticipantHandle, List<TooltipComponent>> firstTooltip;
    private final BiFunction<BattleStateView, BattleParticipantHandle, List<TooltipComponent>> secondTooltip;
    private final Action<? super T1, ? super T2> action;

    public BiTargetParticipantActionInfo(final TargetType<T1> firstTargetType, final TargetType<T2> secondTargetType, final BiFunction<BattleStateView, BattleParticipantHandle, List<TooltipComponent>> firstTooltip, final BiFunction<BattleStateView, BattleParticipantHandle, List<TooltipComponent>> secondTooltip, final Action<? super T1, ? super T2> action) {
        this.firstTargetType = firstTargetType;
        this.secondTargetType = secondTargetType;
        this.firstTooltip = firstTooltip;
        this.secondTooltip = secondTooltip;
        this.action = action;
    }

    public BiTargetParticipantActionInfo(final TargetType<T1> firstTargetType, final TargetType<T2> secondTargetType, final BiFunction<BattleStateView, BattleParticipantHandle, List<TooltipComponent>> firstTooltip, final BiFunction<BattleStateView, BattleParticipantHandle, List<TooltipComponent>> secondTooltip, final Consumer<BattleAction<?>> sender, final SimpleAction<? super T1, ? super T2> action) {
        this.firstTargetType = firstTargetType;
        this.secondTargetType = secondTargetType;
        this.firstTooltip = firstTooltip;
        this.secondTooltip = secondTooltip;
        this.action = (battleState, user, firstTarget, secondTarget) -> sender.accept(action.apply(battleState, user, firstTarget, secondTarget));
    }

    @Override
    public @Nullable TargetType<?> getNextTargetType(final List<TargetInstance> list) {
        return switch (list.size()) {
            case 0 -> firstTargetType;
            case 1 -> secondTargetType;
            default -> null;
        };
    }

    @Override
    public boolean canActivate(final BattleStateView battleState, final BattleParticipantHandle user, final List<TargetInstance> list) {
        return list.size() == 2;
    }

    @Override
    public void activate(final BattleStateView battleState, final BattleParticipantHandle user, final List<TargetInstance> list) {
        action.apply(battleState, user, (T1) list.get(0), (T2) list.get(1));
    }

    @Override
    public @Nullable List<TooltipComponent> getDescription(final BattleStateView battleState, final BattleParticipantHandle user, final List<TargetInstance> list) {
        return switch (list.size()) {
            case 0 -> firstTooltip.apply(battleState, user);
            case 1 -> secondTooltip.apply(battleState, user);
            default -> null;
        };
    }

    public interface Action<T1 extends TargetInstance, T2 extends TargetInstance> {
        void apply(final BattleStateView battleState, final BattleParticipantHandle user, T1 firstTarget, T2 secondTarget);
    }

    public interface SimpleAction<T1 extends TargetInstance, T2 extends TargetInstance> {
        BattleAction<?> apply(final BattleStateView battleState, final BattleParticipantHandle user, T1 firstTarget, T2 secondTarget);
    }
}
