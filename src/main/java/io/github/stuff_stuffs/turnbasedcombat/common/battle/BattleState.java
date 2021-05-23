package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityState;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public final class BattleState implements BattleStateView, Iterable<BattleParticipantHandle> {
    private final int battleId;
    private final Object2ReferenceMap<BattleParticipantHandle, BattleParticipant> participants;
    private final Object2ReferenceMap<BattleParticipantHandle, EntityState> participantStates;
    private final Object2ReferenceMap<Team, Set<BattleParticipant>> teams;
    private final Random random;
    private final Battle battle;
    private int turnCount = 0;
    private boolean ended;

    public BattleState(final int battleId, final Battle battle) {
        this.battleId = battleId;
        participants = new Object2ReferenceOpenHashMap<>();
        participantStates = new Object2ReferenceOpenHashMap<>();
        teams = new Object2ReferenceOpenHashMap<>();
        random = new Random(battleId);
        this.battle = battle;
        ended = false;
    }

    public BattleParticipantHandle addParticipant(final BattleParticipant participant) {
        if (ended) {
            throw new RuntimeException();
        }
        final BattleParticipantHandle handle = new BattleParticipantHandle(battleId, participant.getId());
        final BattleParticipant battleParticipant = participants.get(handle);
        if (battleParticipant != null) {
            return handle;
        }
        participants.put(handle, participant);
        participantStates.put(handle, participant.getSkillInfo().createState());
        teams.computeIfAbsent(participant.getTeam(), i -> new ReferenceOpenHashSet<>()).add(participant);
        return handle;
    }

    public boolean removeParticipant(final BattleParticipantHandle handle) {
        if (ended) {
            throw new RuntimeException();
        }
        if (battleId != handle.getBattleId()) {
            throw new RuntimeException();
        }
        final BattleParticipant removed = participants.remove(handle);
        if (removed != null) {
            participantStates.remove(handle);
            final Set<BattleParticipant> team = teams.get(removed.getTeam());
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
        ended = true;
    }

    //TODO throws not enough participants exception?
    public BattleParticipant advanceTurn(final BattleParticipantHandle handle) {
        if (handle.getBattleId() == battleId && handle.getParticipantId().equals(getCurrentTurn().getId())) {
            turnCount++;
            return (BattleParticipant) battle.getTurnChooser().choose(participants.values(), this);
        } else {
            throw new RuntimeException();
        }
    }

    //TODO throws not enough participants exception?
    @Override
    public BattleParticipant getCurrentTurn() {
        return (BattleParticipant) battle.getTurnChooser().getCurrent(participants.values(), this);
    }

    @Override
    public @Nullable BattleParticipant getParticipant(final BattleParticipantHandle handle) {
        if (battleId != handle.getBattleId()) {
            throw new RuntimeException();
        }
        return participants.get(handle);
    }

    public @Nullable EntityState getParticipantState(final BattleParticipantHandle handle) {
        if (battleId != handle.getBattleId()) {
            throw new RuntimeException();
        }
        return participantStates.get(handle);
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

    @NotNull
    @Override
    public Iterator<BattleParticipantHandle> iterator() {
        return new ObjectOpenHashSet<>(participants.keySet()).iterator();
    }
}
