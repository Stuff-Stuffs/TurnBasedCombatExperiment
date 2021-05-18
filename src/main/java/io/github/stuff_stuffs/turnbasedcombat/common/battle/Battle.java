package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;

public class Battle {
    public static final Codec<Battle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("battleId").forGetter(battle -> battle.battleId),
            BattleTimeline.CODEC.fieldOf("timeline").forGetter(battle -> battle.timeline)
    ).apply(instance, Battle::new));
    private final int battleId;
    private BattleState state;
    private BattleTimeline timeline;

    public Battle(final int battleId, final BattleTimeline timeline) {
        this.battleId = battleId;
        state = new BattleState(battleId);
        this.timeline = timeline;
        for (final BattleAction action : this.timeline) {
            action.applyToState(state);
        }
    }

    public BattleStateView getStateView() {
        return state;
    }

    public BattleTimelineView getTimeline() {
        return timeline;
    }

    public void push(final BattleAction action) {
        action.applyToState(state);
        timeline.push(action);
    }

    public void trimToSize(final int size) {
        if (timeline.size() > size) {
            final BattleTimeline newTimeline = new BattleTimeline();
            state = new BattleState(battleId);
            for (int i = 0; i < size; i++) {
                final BattleAction action = timeline.get(i);
                action.applyToState(state);
                newTimeline.push(action);
            }
            timeline = newTimeline;
        }
    }

    public int getBattleId() {
        return battleId;
    }

    public void tick() {
    }
}
