package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooser;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooserTypeRegistry;

public final class Battle {
    public static final Codec<Battle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("battleId").forGetter(battle -> battle.battleId),
            TurnChooserTypeRegistry.CODEC.fieldOf("turnChooser").forGetter(battle -> battle.turnChooser),
            BattleTimeline.CODEC.fieldOf("timeline").forGetter(battle -> battle.timeline)
    ).apply(instance, Battle::new));
    private final int battleId;
    private final TurnChooser turnChooser;
    private BattleState state;
    private BattleTimeline timeline;

    public Battle(final int battleId, final TurnChooser turnChooser, final BattleTimeline timeline) {
        this.battleId = battleId;
        this.turnChooser = turnChooser;
        this.turnChooser.reset();
        this.timeline = timeline;
        state = new BattleState(battleId, this);
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
            state = new BattleState(battleId, this);
            turnChooser.reset();
            for (int i = 0; i < size; i++) {
                final BattleAction action = timeline.get(i);
                action.applyToState(state);
                newTimeline.push(action);
            }
            timeline = newTimeline;
        }
    }

    public TurnChooser getTurnChooser() {
        return turnChooser;
    }

    public int getBattleId() {
        return battleId;
    }

    public void tick() {
    }
}
