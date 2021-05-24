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
import java.util.UUID;

public final class BattleState implements BattleStateView, Iterable<BattleParticipantHandle> {
    private final int battleId;
    private final Object2ReferenceMap<BattleParticipantHandle, EntityState> participants;
    private final Object2ReferenceMap<Team, Set<EntityState>> teams;
    private final Random random;
    private final Battle battle;
    private int turnCount = 0;
    private boolean ended;

    public BattleState(final int battleId, final Battle battle) {
        this.battleId = battleId;
        participants = new Object2ReferenceOpenHashMap<>();
        teams = new Object2ReferenceOpenHashMap<>();
        random = new Random(battleId);
        this.battle = battle;
        ended = false;
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
        participants.put(handle, participant);
        teams.computeIfAbsent(participant.getTeam(), i -> new ReferenceOpenHashSet<>()).add(participant);
        return handle;
    }

    public boolean removeParticipant(final BattleParticipantHandle handle) {
        if (ended) {
            throw new RuntimeException();
        }
        if (battleId != handle.battleId()) {
            throw new RuntimeException();
        }
        final EntityState removed = participants.remove(handle);
        if (removed != null) {
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
        ended = true;
    }

    //TODO throws not enough participants exception?
    public EntityState advanceTurn(final BattleParticipantHandle handle) {
        if (handle.battleId() == battleId && (handle.isUniversal() || handle.participantId().equals(getCurrentTurn().getId()))) {
            turnCount++;
            return (EntityState) battle.getTurnChooser().choose(participants.values(), this);
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
    public boolean contains(UUID id) {
        for (EntityState participant : participants.values()) {
            if(participant.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public Iterator<BattleParticipantHandle> iterator() {
        return new ObjectOpenHashSet<>(participants.keySet()).iterator();
    }
}
