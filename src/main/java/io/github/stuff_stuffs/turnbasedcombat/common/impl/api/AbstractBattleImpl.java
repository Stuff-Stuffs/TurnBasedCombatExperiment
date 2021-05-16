package io.github.stuff_stuffs.turnbasedcombat.common.impl.api;

import io.github.stuff_stuffs.turnbasedcombat.common.api.*;
import io.github.stuff_stuffs.turnbasedcombat.common.component.BattleEntityComponent;
import io.github.stuff_stuffs.turnbasedcombat.common.component.Components;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public abstract class AbstractBattleImpl implements Battle {
    protected final BattleHandle handle;
    private final BattleBounds bounds;
    protected final BattleLog log;
    protected final Set<BattleEntity> participants;
    protected final Set<BattleEntity> activeParticipants;
    protected final Map<Team, Set<BattleEntity>> teamMap;
    private boolean active;
    private @Nullable EndingReason endingReason;

    protected AbstractBattleImpl(final BattleHandle handle, final BattleBounds bounds, final BattleLog log, final boolean active) {
        this.handle = handle;
        this.bounds = bounds;
        this.log = log;
        participants = new ReferenceOpenHashSet<>();
        activeParticipants = new ReferenceOpenHashSet<>();
        teamMap = new Object2ReferenceOpenHashMap<>();
        this.active = active;
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
            if (activeParticipants.size() == 0) {
                end(Battle.EndingReason.NO_ACTIVE_PARTICIPANTS);
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
        final BattleEntityComponent be = Components.BATTLE_ENTITY_COMPONENT_KEY.get(battleEntity);
        if (be.isInBattle() && !handle.equals(be.getBattleHandle())) {
            throw new RuntimeException();
        } else {
            be.setBattleHandle(handle);
        }
        if (battleEntity.isActiveEntity()) {
            activeParticipants.add(battleEntity);
        }
        teamMap.computeIfAbsent(battleEntity.getTeam(), i -> new ReferenceOpenHashSet<>()).add(battleEntity);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void end(final Battle.EndingReason reason) {
        if (active) {
            for (final BattleEntity participant : participants) {
                final BattleEntityComponent battleEntity = Components.BATTLE_ENTITY_COMPONENT_KEY.get(participant);
                battleEntity.setBattleHandle(null);
            }
        }
        active = false;
        endingReason = reason;
    }

    @Override
    public Battle.EndingReason getEndingReason() {
        if (active) {
            LOGGER.error("Trying to get EndingReason for a battle that is active");
        }
        return endingReason;
    }

    @Override
    public Set<BattleEntity> getActiveBattleEntities() {
        return new ReferenceOpenHashSet<>(activeParticipants);
    }

    public boolean shouldEnd() {
        return teamMap.size() < 2;
    }

    @Override
    public BattleHandle getHandle() {
        return handle;
    }

    @Override
    public BattleBounds getBounds() {
        return bounds;
    }

    @Override
    public BattleLog getLog() {
        return log;
    }

    @Override
    public Set<BattleEntity> getBattleEntities() {
        return participants;
    }
}
