package io.github.stuff_stuffs.tbcexcore.common.battle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.BattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.action.EndTurnBattleAction;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.battle.AdvanceTurnEvent;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleBounds;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import net.minecraft.world.World;

import java.util.List;

public final class Battle implements AdvanceTurnEvent {
    public static final Codec<Battle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleHandle.CODEC.fieldOf("handle").forGetter(battle -> battle.handle),
            BattleTimeline.CODEC.fieldOf("timeline").forGetter(battle -> battle.timeline),
            BattleBounds.CODEC.fieldOf("bounds").forGetter(battle -> battle.bounds)
    ).apply(instance, Battle::new));
    private BattleState state;
    private final BattleHandle handle;
    private final BattleTimeline timeline;
    private final BattleBounds bounds;
    private World world;
    private TurnTimer timer;

    private Battle(final BattleHandle handle, final BattleTimeline timeline, final BattleBounds bounds) {
        this.timeline = timeline;
        this.handle = handle;
        state = createState(handle, bounds, 20 * 30, 20 * 30);
        this.bounds = bounds;
        for (final BattleAction<?> action : timeline) {
            action.applyToState(state);
        }
    }

    public Battle(final BattleHandle handle, final BattleBounds bounds, final int turnTimerRemaining, final int turnTimerMax) {
        this.handle = handle;
        state = createState(handle, bounds, turnTimerRemaining, turnTimerMax);
        this.bounds = bounds;
        timeline = new BattleTimeline();
    }

    public void setWorld(final World world) {
        this.world = world;
        state.setWorld(world);
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

    public BattleBounds getBounds() {
        return bounds;
    }

    public void push(final BattleAction<?> action) {
        final BattleParticipantHandle currentTurn = state.getCurrentTurn();
        if (action.getActor().isUniversal() || action.getActor().equals(currentTurn)) {
            timeline.push(action);
            action.applyToState(state);
        } else {
            throw new TBCExException("tried action not on turn");
        }
    }

    public void trimAndAppend(final int size, final List<BattleAction<?>> actions, final int turnTimerRemaining, final int turnTimerMax) {
        if (size != timeline.getSize()) {
            timeline.trim(size);
            for (final BattleAction<?> action : actions) {
                timeline.push(action);
            }
            state = createState(handle, bounds, turnTimerRemaining, turnTimerMax);
            for (final BattleAction<?> action : timeline) {
                action.applyToState(state);
            }
        } else if (actions.size() != 0) {
            for (final BattleAction<?> action : actions) {
                timeline.push(action);
                action.applyToState(state);
            }
        }
        timer = new TurnTimer(turnTimerMax, turnTimerRemaining);
    }

    private BattleState createState(final BattleHandle handle, final BattleBounds bounds, final int turnTimerRemaining, final int turnTimerMax) {
        final BattleState battleState = new BattleState(handle, bounds);
        battleState.setWorld(world);
        battleState.getEvent(BattleStateView.ADVANCE_TURN_EVENT).register(this);
        timer = new TurnTimer(turnTimerMax, turnTimerRemaining);
        return battleState;
    }

    @Override
    public void onAdvanceTurn(final BattleStateView battleState, final BattleParticipantHandle current) {
        timer = new TurnTimer(20 * 30, 20 * 30);
    }

    public void tick() {
        if (timer.tick()) {
            push(new EndTurnBattleAction(BattleParticipantHandle.UNIVERSAL.apply(handle)));
        }
        state.tick();
    }

    public int getTurnTimerRemaining() {
        return timer.getRemaining();
    }

    public int getTurnTimerMax() {
        return timer.getMax();
    }
}
