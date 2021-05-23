package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.BattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.EndBattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.EndTurnAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.action.LeaveBattleAction;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooser;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooserTypeRegistry;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnTimer;

public final class Battle {
    public static final Codec<Battle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("battleId").forGetter(battle -> battle.battleId),
            TurnChooserTypeRegistry.CODEC.fieldOf("turnChooser").forGetter(battle -> battle.turnChooser),
            BattleTimeline.CODEC.fieldOf("timeline").forGetter(battle -> battle.timeline)
    ).apply(instance, Battle::new));
    private final int battleId;
    private final TurnChooser turnChooser;
    private final TurnTimer turnTimer;
    private BattleState state;
    private BattleTimeline timeline;
    private int lastTurn = -1;

    public Battle(final int battleId, final TurnChooser turnChooser, final BattleTimeline timeline) {
        this.battleId = battleId;
        this.turnChooser = turnChooser;
        turnTimer = new TurnTimer(TurnBasedCombatExperiment.getMaxTurnTime() * 20);
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
        if (state.isBattleEnded()) {
            throw new RuntimeException();
        }
        if (action.getHandle().getBattleId() != battleId) {
            throw new RuntimeException();
        }
        if (action.getHandle().isUniversal() || action.getHandle().getParticipantId().equals(state.getCurrentTurn().getId())) {
            action.applyToState(state);
            timeline.push(action);
        } else {
            throw new RuntimeException();
        }
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
        if (!state.isBattleEnded()) {
            if (lastTurn != state.getTurnCount()) {
                lastTurn = state.getTurnCount();
                for (final BattleParticipantHandle handle : state) {
                    final EntityState entityState = state.getParticipantState(handle);
                    if (entityState == null) {
                        throw new RuntimeException();
                    }
                    if (entityState.getHealth() < 1) {
                        push(new LeaveBattleAction(BattleParticipantHandle.UNIVERSAL.apply(battleId), handle));
                    }
                }
            }
            if (state.getTeamCount() < 2 || lastTurn > TurnBasedCombatExperiment.getMaxTurnCount()) {
                push(new EndBattleAction(BattleParticipantHandle.UNIVERSAL.apply(battleId)));
            } else if (turnTimer.shouldEndTurn()) {
                push(new EndTurnAction(BattleParticipantHandle.UNIVERSAL.apply(battleId)));
            }
        }
    }
}
