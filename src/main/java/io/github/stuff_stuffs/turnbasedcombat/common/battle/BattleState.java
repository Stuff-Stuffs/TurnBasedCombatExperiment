package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventKey;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventMap;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.MutableEventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.battle.PostParticipantJoinEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.battle.PreParticipantJoinEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.battle.PreParticipantLeaveEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public final class BattleState implements BattleStateView {
    private final Map<BattleParticipantHandle, BattleParticipantState> participants;
    private final BattleHandle handle;
    private final EventMap eventMap;
    private boolean ended;

    public BattleState(final BattleHandle handle) {
        this.handle = handle;
        participants = new Object2ObjectOpenHashMap<>();
        eventMap = new EventMap();
        ended = false;

        registerEvents();
    }

    private void registerEvents() {
        eventMap.register(PRE_PARTICIPANT_JOIN_EVENT, new MutableEventHolder.BasicEventHolder<>(PRE_PARTICIPANT_JOIN_EVENT, view -> (battleState, participant) -> {
            view.onParticipantJoin(battleState, participant);
            return false;
        }, events -> (battleState, participant) -> {
            boolean canceled = false;
            for (final PreParticipantJoinEvent.Mut event : events) {
                canceled |= event.onParticipantJoin(battleState, participant);
            }
            return canceled;
        }));
        eventMap.register(POST_PARTICIPANT_JOIN_EVENT, new MutableEventHolder.BasicEventHolder<>(POST_PARTICIPANT_JOIN_EVENT, view -> view::onParticipantJoin, events -> (battleState, participant) -> {
            for (final PostParticipantJoinEvent.Mut event : events) {
                event.onParticipantJoin(battleState, participant);
            }
        }));
        eventMap.register(PRE_PARTICIPANT_LEAVE_EVENT, new MutableEventHolder.BasicEventHolder<>(PRE_PARTICIPANT_LEAVE_EVENT, view -> (battleState, participantState) -> {
            view.onParticipantLeave(battleState, participantState);
            return false;
        }, events -> (battleState, participantState) -> {
            boolean canceled = false;
            for (final PreParticipantLeaveEvent.Mut event : events) {
                canceled |= event.onParticipantLeave(battleState, participantState);
            }
            return canceled;
        }));
    }

    @Override
    public @Nullable BattleParticipantState getParticipant(final BattleParticipantHandle handle) {
        return participants.get(handle);
    }

    public boolean join(final Function<BattleHandle, BattleParticipantState> func) {
        final BattleParticipantState state = func.apply(handle);
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
            return true;
        }
        return false;
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
}
