package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;

import java.util.List;

public final class Battle {
    public static final Codec<Battle> CODEC = BattleTimeline.CODEC.xmap(Battle::new, battle -> battle.timeline);
    private BattleState state;
    private final BattleTimeline timeline;

    private Battle(final BattleTimeline timeline) {
        this.timeline = timeline;
        state = new BattleState();
        for (final BattleAction<?> action : timeline) {
            action.applyToState(state);
        }
    }

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
