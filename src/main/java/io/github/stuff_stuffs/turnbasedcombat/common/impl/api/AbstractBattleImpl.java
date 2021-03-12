package io.github.stuff_stuffs.turnbasedcombat.common.impl.api;

import io.github.stuff_stuffs.turnbasedcombat.common.api.Battle;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleEntity;
import io.github.stuff_stuffs.turnbasedcombat.common.api.BattleHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.api.Team;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public abstract class AbstractBattleImpl implements Battle {
    protected final BattleHandle handle;
    protected final PlayerEntity playerEntity;
    protected final Set<BattleEntity> participants;
    protected final Map<Team, Set<BattleEntity>> teamMap;
    private boolean active;
    private @Nullable EndingReason endingReason;

    protected AbstractBattleImpl(final BattleHandle handle, final PlayerEntity playerEntity, final Set<BattleEntity> participants, final boolean active) {
        this.handle = handle;
        this.playerEntity = playerEntity;
        this.participants = participants;
        teamMap = new Object2ReferenceOpenHashMap<>();
        this.active = active;
        addParticipant((BattleEntity) playerEntity);
        for (final BattleEntity participant : participants) {
            teamMap.computeIfAbsent(participant.getTeam(), i -> new ReferenceOpenHashSet<>()).add(participant);
        }
    }

    @Override
    public boolean remove(final BattleEntity battleEntity) {
        if (participants.remove(battleEntity)) {
            final Team team = battleEntity.getTeam();
            final Set<BattleEntity> teamEntities = teamMap.get(battleEntity.getTeam());
            if (teamEntities == null) {
                throw new RuntimeException();
            }
            teamEntities.remove(battleEntity);
            if (teamEntities.size() == 0) {
                teamMap.remove(team);
            }
            if (battleEntity == playerEntity) {
                end(Battle.EndingReason.PLAYER_LEFT);
            }
            return true;
        }
        return false;
    }

    @Override
    public Set<BattleEntity> getAllies(final BattleEntity battleEntity) {
        if (!contains(battleEntity)) {
            throw new RuntimeException();
        }
        final Set<BattleEntity> team = teamMap.get(battleEntity.getTeam());
        if (team != null) {
            final Set<BattleEntity> allies = new ReferenceOpenHashSet<>(team);
            allies.remove(battleEntity);
            return allies;
        }
        return new ReferenceOpenHashSet<>();
    }

    @Override
    public Set<BattleEntity> getEnemies(final BattleEntity battleEntity) {
        if (!contains(battleEntity)) {
            throw new RuntimeException();
        }
        final Set<BattleEntity> team = teamMap.get(battleEntity.getTeam());
        if (team == null) {
            throw new IllegalStateException("This should be impossible");
        }
        final Set<BattleEntity> enemies = new ObjectOpenHashSet<>(participants);
        enemies.removeAll(team);
        return enemies;
    }

    @Override
    public boolean contains(final BattleEntity battleEntity) {
        return participants.contains(battleEntity);
    }

    @Override
    public void addParticipant(final BattleEntity battleEntity) {
        participants.add(battleEntity);
        teamMap.computeIfAbsent(battleEntity.getTeam(), i -> new ReferenceOpenHashSet<>()).add(battleEntity);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public PlayerEntity getPlayer() {
        return playerEntity;
    }

    @Override
    public void end(final Battle.EndingReason reason) {
        if (endingReason != null) {
            throw new RuntimeException();
        }
        active = false;
        endingReason = reason;
    }

    @Override
    public Battle.EndingReason getEndingReason() {
        if (active) {
            throw new RuntimeException();
        }
        return endingReason;
    }

    public boolean isEnded() {
        return !active || teamMap.size() < 2;
    }
}
