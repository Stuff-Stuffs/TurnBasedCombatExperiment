package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.Set;

public final class BattleState implements BattleStateView {
    private final int battleId;
    private final Int2ReferenceMap<BattleParticipant> participants;
    private final Object2ReferenceMap<Team, Set<BattleParticipant>> teams;
    private final Random random;
    private final Battle battle;
    private boolean ended;

    public BattleState(final int battleId, final Battle battle) {
        this.battleId = battleId;
        participants = new Int2ReferenceOpenHashMap<>();
        teams = new Object2ReferenceOpenHashMap<>();
        random = new Random(battleId);
        this.battle = battle;
        ended = false;
    }

    public BattleParticipantHandle addParticipant(final BattleParticipant participant) {
        if (ended) {
            throw new RuntimeException();
        }
        final BattleParticipant battleParticipant = participants.get(participant.getId());
        if (battleParticipant != null) {
            return new BattleParticipantHandle(battleId, participant.getId());
        }
        participants.put(participant.getId(), participant);
        teams.computeIfAbsent(participant.getTeam(), i -> new ReferenceOpenHashSet<>()).add(participant);
        return new BattleParticipantHandle(battleId, participant.getId());
    }

    public boolean removeParticipant(final BattleParticipantHandle handle) {
        if (ended) {
            throw new RuntimeException();
        }
        if (battleId != handle.getBattleId()) {
            throw new RuntimeException();
        }
        final BattleParticipant removed = participants.remove(handle.getParticipantId());
        if (removed != null) {
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
        if (handle.getBattleId() == battleId && handle.getParticipantId() == getCurrentTurn().getId()) {
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
        return participants.get(handle.getParticipantId());
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
}
