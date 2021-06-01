package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.google.common.collect.Iterables;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.*;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.turn.TurnChooser;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class BattleState implements BattleStateView, Iterable<BattleParticipantHandle> {
    private final int battleId;
    private final Object2ReferenceMap<BattleParticipantHandle, EntityState> participants;
    private final Object2ReferenceMap<Team, Set<EntityState>> teams;
    private final Random random;
    private final Battle battle;
    private final Map<Class<?>, EventHolder<?>> eventHolders;
    private int turnCount = 0;
    private int roundCount = 0;
    private boolean ended;

    public BattleState(final int battleId, final Battle battle) {
        this.battleId = battleId;
        participants = new Object2ReferenceOpenHashMap<>();
        teams = new Object2ReferenceOpenHashMap<>();
        random = new Random(battleId);
        this.battle = battle;
        eventHolders = new Reference2ObjectOpenHashMap<>();
        populateEvents();
        ended = false;
    }

    private void populateEvents() {
        putEvent(BattleEndedEvent.class, new EventHolder.BasicEventHolder<>(battleEndedEvents -> battleState -> {
            for (final BattleEndedEvent event : battleEndedEvents) {
                event.onBattleEnded(battleState);
            }
        }));
        putEvent(EntityJoinEvent.class, new EventHolder.BasicEventHolder<>(events -> (battleState, entityState) -> {
            for (final EntityJoinEvent event : events) {
                event.onEntityJoin(battleState, entityState);
            }
        }));
        putEvent(EntityLeaveEvent.class, new EventHolder.BasicEventHolder<>(events -> (battleState, entityState) -> {
            for (final EntityLeaveEvent event : events) {
                event.onEntityLeave(battleState, entityState);
            }
        }));
        putEvent(AdvanceTurnEvent.class, new EventHolder.BasicEventHolder<>(events -> (battleState, prev, current) -> {
            for (final AdvanceTurnEvent event : events) {
                event.onAdvanceTurn(battleState, prev, current);
            }
        }));
        putEvent(AdvanceRoundEvent.class, new EventHolder.BasicEventHolder<>(events -> battleState -> {
            for (final AdvanceRoundEvent event : events) {
                event.onAdvanceRound(battleState);
            }
        }));
        putEvent(EntityDeathEvent.class, new EventHolder.BasicEventHolder<>(events -> entityState -> {
            for (final EntityDeathEvent event : events) {
                event.onDeath(entityState);
            }
        }));
    }

    private <T> void putEvent(final Class<T> clazz, final EventHolder<T> eventHolder) {
        eventHolders.put(clazz, eventHolder);
    }

    public <T> EventHolder<T> getEvent(final Class<T> clazz) {
        final EventHolder<T> holder = (EventHolder<T>) eventHolders.get(clazz);
        if(holder==null) {
            throw new RuntimeException("Unregistered event: " + clazz.getCanonicalName());
        }
        return holder;
    }

    public BattleParticipantHandle addParticipant(final EntityState participant) {
        if (ended) {
            throw new RuntimeException();
        }
        final BattleParticipantHandle handle = new BattleParticipantHandle(battleId, participant.getId());
        final EntityState battleParticipant = participants.get(handle);
        if (battleParticipant != null) {
            return handle;
        }
        participant.setBattle(this);
        participant.setHandle(handle);
        participants.put(handle, participant);
        teams.computeIfAbsent(participant.getTeam(), i -> new ReferenceOpenHashSet<>()).add(participant);
        participant.initEvents();
        getEvent(EntityJoinEvent.class).invoker().onEntityJoin(this, participant);
        return handle;
    }

    public boolean removeParticipant(final BattleParticipantHandle handle) {
        if (ended) {
            throw new RuntimeException();
        }
        if (battleId != handle.battleId()) {
            throw new RuntimeException();
        }
        final EntityState removed = participants.get(handle);
        if (removed != null) {
            getEvent(EntityLeaveEvent.class).invoker().onEntityLeave(this, removed);
            participants.remove(handle);
            final Set<EntityState> team = teams.get(removed.getTeam());
            team.remove(removed);
            if (team.size() == 0) {
                teams.remove(removed.getTeam());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isBattleEnded() {
        return ended;
    }

    public void endBattle() {
        if(!ended) {
            getEvent(BattleEndedEvent.class).invoker().onBattleEnded(this);
        }
        ended = true;
    }

    //TODO throws not enough participants exception?
    public EntityState advanceTurn(final BattleParticipantHandle handle) {
        if (handle.battleId() == battleId && (handle.isUniversal() || handle.participantId().equals(getCurrentTurn().getId()))) {
            turnCount++;
            BattleParticipantHandle prev = getCurrentTurn().getHandle();
            final TurnChooser.TurnInfo turnInfo = battle.getTurnChooser().nextTurn(participants.values(), this);
            getEvent(AdvanceTurnEvent.class).invoker().onAdvanceTurn(this, prev, turnInfo.participant().getHandle());
            if (roundCount != turnInfo.roundNumber()) {
                getEvent(AdvanceRoundEvent.class).invoker().onAdvanceRound(this);
                roundCount = turnInfo.roundNumber();
            }
            return (EntityState) turnInfo.participant();
        } else {
            throw new RuntimeException();
        }
    }

    //TODO throws not enough participants exception?
    @Override
    public EntityState getCurrentTurn() {
        return (EntityState) battle.getTurnChooser().getCurrent(participants.values(), this);
    }

    @Override
    public @Nullable EntityState getParticipant(final BattleParticipantHandle handle) {
        if (battleId != handle.battleId()) {
            throw new RuntimeException();
        }
        return participants.get(handle);
    }

    @Override
    public int getParticipantCount() {
        return participants.size();
    }

    @Override
    public int getTeamCount() {
        return teams.size();
    }

    public Random getRandom() {
        return random;
    }

    @Override
    public int getTurnCount() {
        return turnCount;
    }

    @Override
    public int getRoundCount() {
        return roundCount;
    }

    @Override
    public boolean contains(final UUID id) {
        for (final EntityState participant : participants.values()) {
            if (participant.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterable<BattleParticipantHandle> getParticipants() {
        return Iterables.unmodifiableIterable(participants.keySet());
    }

    @NotNull
    @Override
    public Iterator<BattleParticipantHandle> iterator() {
        return new ObjectOpenHashSet<>(participants.keySet()).iterator();
    }
}
