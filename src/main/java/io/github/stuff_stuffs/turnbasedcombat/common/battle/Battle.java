package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.world.BattleBounds;

import java.util.List;

public final class Battle {
    public static final Codec<Battle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleHandle.CODEC.fieldOf("handle").forGetter(battle -> battle.handle),
            BattleTimeline.CODEC.fieldOf("timeline").forGetter(battle -> battle.timeline),
            BattleBounds.CODEC.fieldOf("bounds").forGetter(battle -> battle.bounds)
    ).apply(instance, Battle::new));
    private BattleState state;
    private final BattleHandle handle;
    private final BattleTimeline timeline;
    private final BattleBounds bounds;

    private Battle(final BattleHandle handle, final BattleTimeline timeline, final BattleBounds bounds) {
        this.timeline = timeline;
        this.handle = handle;
        state = new BattleState(this.handle, bounds);
        this.bounds = bounds;
        for (final BattleAction<?> action : timeline) {
            action.applyToState(state);
        }
    }

    public Battle(final BattleHandle handle, final BattleBounds bounds) {
        this.handle = handle;
        state = new BattleState(this.handle, bounds);
        this.bounds = bounds;
        timeline = new BattleTimeline();
    }

    public BattleHandle getHandle() {
        return handle;
    }

    public BattleStateView getState() {
        return state;
    }

    public BattleTimelineView getTimeline() {
        return timeline;
    }

    public void push(final BattleAction<?> action) {
        final BattleParticipantHandle currentTurn = state.getCurrentTurn();
        if(action.getActor().isUniversal()||action.getActor().equals(currentTurn)) {
            action.applyToState(state);
            timeline.push(action);
        } else {
            //TODO log
            throw new RuntimeException();
        }
    }

    public void trimAndAppend(final int size, final List<BattleAction<?>> actions) {
        timeline.trim(size);
        for (final BattleAction<?> action : actions) {
            timeline.push(action);
        }
        state = new BattleState(handle, bounds);
        for (final BattleAction<?> action : timeline) {
            action.applyToState(state);
        }
    }
}
