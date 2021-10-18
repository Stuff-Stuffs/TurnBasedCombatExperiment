package io.github.stuff_stuffs.tbcexcore.common.battle.state;

import com.google.common.collect.Iterators;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventHolder;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventKey;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventMap;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.MutableEventHolder;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.ParticipantPosComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.component.BattleComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.component.BattleComponentKey;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.component.BattleComponents;
import io.github.stuff_stuffs.tbcexcore.common.battle.turnchooser.TurnChooser;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleBounds;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

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
            //TODO
            throw new RuntimeException();
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

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
