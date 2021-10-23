package io.github.stuff_stuffs.tbcexcore.common.battle.state;

import com.google.common.collect.Iterators;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.Team;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventHolder;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventKey;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventMap;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.MutableEventHolder;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantPosComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.component.BattleComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.component.BattleComponentKey;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.component.BattleComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.turnchooser.TurnChooser;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleBounds;
import io.github.stuff_stuffs.tbcexcore.common.util.BattleShapeCache;
import io.github.stuff_stuffs.tbcexutil.common.TBCExException;
import io.github.stuff_stuffs.tbcexutil.common.WorldShapeCache;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class BattleState implements BattleStateView {
    private final Map<BattleParticipantHandle, BattleParticipantState> participants;
    private final BattleHandle handle;
    private final EventMap eventMap;
    private final BattleBounds bounds;
    private final TurnChooser turnChooser;
    private final Map<BattleComponents.Type<?, ?>, BattleComponent> componentsByType;
    private final Map<BattleComponentKey<?, ?>, BattleComponent> componentsByKey;
    private boolean ended;
    private World world;
    private BattleShapeCache shapeCache;

    public BattleState(final BattleHandle handle, final BattleBounds bounds) {
        eventMap = new EventMap();
        BattleEvents.setup(eventMap);

        this.handle = handle;
        this.bounds = bounds;
        participants = new Object2ObjectOpenHashMap<>();
        ended = false;

        turnChooser = new TurnChooser(this);

        componentsByKey = new Reference2ReferenceOpenHashMap<>();
        componentsByType = new Reference2ReferenceOpenHashMap<>();
        boolean lastAdded = true;
        while (lastAdded) {
            lastAdded = false;
            for (final BattleComponents.Type<?, ?> type : BattleComponents.REGISTRY) {
                if (componentsByType.containsKey(type)) {
                    continue;
                }
                boolean acceptable = true;
                for (final BattleComponentKey<?, ?> key : type.requiredComponents) {
                    if (!componentsByKey.containsKey(key)) {
                        acceptable = false;
                        break;
                    }
                }
                if (acceptable) {
                    final BattleComponent apply = type.extractor.apply(this);
                    if (apply != null) {
                        componentsByType.put(type, apply);
                        componentsByKey.put(type.key, apply);
                        apply.init(this);
                        lastAdded = true;
                    }
                }
            }
        }
        getEvent(POST_PARTICIPANT_LEAVE_EVENT).register((battleState, participantView) -> {
            final Set<Team> active = new ObjectOpenHashSet<>();
            final Iterator<BattleParticipantHandle> iterator = battleState.getParticipants();
            while (iterator.hasNext()) {
                final BattleParticipantStateView participant = battleState.getParticipant(iterator.next());
                if (participant == null) {
                    throw new TBCExException("missing battle participant in battle");
                }
                active.add(participant.getTeam());
            }
            if (active.size() < 2) {
                ended = true;
                getEvent(BATTLE_END_EVENT).invoker().onBattleEnd(BattleState.this);
            }
        });
    }

    @Override
    public BattleHandle getHandle() {
        return handle;
    }

    @Override
    public @Nullable BattleParticipantState getParticipant(final BattleParticipantHandle handle) {
        return participants.get(handle);
    }

    public boolean join(final BattleParticipantState state) {
        if (!state.getHandle().battleId().equals(handle)) {
            throw new RuntimeException();
        }
        final ParticipantPosComponent posComponent = state.getMutComponent(ParticipantComponents.POS_COMPONENT_TYPE.key);
        if (posComponent == null) {
            throw new TBCExException("required component is null");
        }
        posComponent.setPos(bounds.getNearest(state.getPos()));
        if (participants.containsKey(state.getHandle())) {
            throw new RuntimeException("Duplicate handles attempted to join battle");
        }
        if (!getEvent(PRE_PARTICIPANT_JOIN_EVENT).invoker().onParticipantJoin(this, state)) {
            participants.put(state.getHandle(), state);
            getEvent(POST_PARTICIPANT_JOIN_EVENT).invoker().onParticipantJoin(this, state);
            return true;
        }
        return false;
    }

    public boolean leave(final BattleParticipantHandle handle) {
        if (!participants.containsKey(handle)) {
            throw new RuntimeException("Participant cannot leave battle they are not in!");
        }
        final BattleParticipantState state = participants.get(handle);
        if (!getEvent(PRE_PARTICIPANT_LEAVE_EVENT).invoker().onParticipantLeave(this, state)) {
            participants.remove(handle);
            getEvent(POST_PARTICIPANT_LEAVE_EVENT).invoker().onParticipantLeave(this, state);
            state.leave();
            return true;
        }
        return false;
    }

    @Override
    public @Nullable BattleParticipantHandle getCurrentTurn() {
        return turnChooser.valid() ? turnChooser.getCurrentTurn() : null;
    }

    public void advanceTurn() {
        if (turnChooser.valid()) {
            turnChooser.advance();
            getEvent(ADVANCE_TURN_EVENT).invoker().onAdvanceTurn(this, turnChooser.getCurrentTurn());
        }
    }

    @Override
    public boolean isEnded() {
        return ended;
    }

    @Override
    public BattleShapeCache getShapeCache() {
        return shapeCache;
    }

    @Override
    public <T, V> EventHolder<T, V> getEvent(final EventKey<T, V> key) {
        return eventMap.get(key);
    }

    public <T, V> MutableEventHolder<T, V> getEventMut(final EventKey<T, V> key) {
        return eventMap.getMut(key);
    }

    @Override
    public <View extends BattleComponent> @Nullable View getComponent(final BattleComponentKey<?, View> key) {
        return (View) componentsByKey.get(key);
    }

    public <Mut extends View, View extends BattleComponent> @Nullable Mut getMutComponent(final BattleComponentKey<Mut, View> key) {
        return (Mut) componentsByKey.get(key);
    }

    @Override
    public BattleBounds getBounds() {
        return bounds;
    }

    @Override
    public Iterator<BattleParticipantHandle> getParticipants() {
        return Iterators.unmodifiableIterator(participants.keySet().iterator());
    }

    @Override
    public Spliterator<BattleParticipantHandle> getSpliteratorParticipants() {
        return participants.keySet().spliterator();
    }

    public void tick() {
        if(world!=null) {
            shapeCache = new BattleShapeCache(world, this);
        }
    }

    public void setWorld(final World world) {
        this.world = world;
        shapeCache = new BattleShapeCache(world, this);
    }

    public World getWorld() {
        return world;
    }
}
