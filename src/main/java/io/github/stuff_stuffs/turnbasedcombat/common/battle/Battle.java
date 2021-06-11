package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;

import java.util.List;

public final class Battle {
    public static final Codec<Battle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleHandle.CODEC.fieldOf("handle").forGetter(battle -> battle.handle),
            BattleTimeline.CODEC.fieldOf("timeline").forGetter(battle -> battle.timeline)
    ).apply(instance, Battle::new));
    private BattleState state;
    private final BattleHandle handle;
    private final BattleTimeline timeline;

    private Battle(final BattleHandle handle, final BattleTimeline timeline) {
        this.timeline = timeline;
        this.handle = handle;
        state = new BattleState(this.handle);
        for (final BattleAction<?> action : timeline) {
            action.applyToState(state);
        }
    }

    public Battle(final BattleHandle handle) {
        this.handle = handle;
        state = new BattleState(this.handle);
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
        state = new BattleState(handle);
        for (final BattleAction<?> action : timeline) {
            action.applyToState(state);
        }
    }
}
