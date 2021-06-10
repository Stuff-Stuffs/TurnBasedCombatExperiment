package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;

import java.util.List;

public final class Battle {
    private BattleState state;
    private final BattleTimeline timeline;

    public Battle() {
        state = new BattleState();
        timeline = new BattleTimeline();
    }

    public BattleStateView getState() {
        return state;
    }

    public BattleTimelineView getTimeline() {
        return timeline;
    }

    public void push(final BattleAction<?> action) {
        action.applyToState(state);
        timeline.push(action);
    }

    public void trimAndAppend(final int size, final List<BattleAction<?>> actions) {
        timeline.trim(size);
        for (final BattleAction<?> action : actions) {
            timeline.push(action);
        }
        state = new BattleState();
        for (final BattleAction<?> action : timeline) {
            action.applyToState(state);
        }
    }
}
