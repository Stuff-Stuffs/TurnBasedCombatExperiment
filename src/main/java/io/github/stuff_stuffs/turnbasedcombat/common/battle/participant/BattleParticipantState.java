package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.Team;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventKey;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventMap;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.MutableEventHolder;
import org.jetbrains.annotations.Nullable;

public final class BattleParticipantState implements BattleParticipantStateView {
    private final EventMap eventMap;
    private final BattleParticipantHandle handle;
    private final Team team;


    public BattleParticipantState(final BattleParticipantHandle handle, final Team team) {
        this.handle = handle;
        this.team = team;
        eventMap = new EventMap();
        registerEvents();
    }

    private void registerEvents() {

    }

    public @Nullable <T, V> MutableEventHolder<T, V> getEventMut(final EventKey<T, V> key) {
        return eventMap.getMut(key);
    }

    @Override
    public @Nullable <T, V> EventHolder<T, V> getEvent(final EventKey<T, V> key) {
        return eventMap.get(key);
    }

    public Team getTeam() {
        return team;
    }

    public BattleParticipantHandle getHandle() {
        return handle;
    }
}
