package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventKey;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventMap;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.MutableEventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantState;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class BattleState implements BattleStateView {
    private final Map<BattleParticipantHandle, BattleParticipantState> participants;
    private final EventMap eventMap;
    private boolean ended;

    public BattleState() {
        participants = new Object2ObjectOpenHashMap<>();
        eventMap = new EventMap();
        ended = false;

        registerEvents();
    }

    private void registerEvents() {

    }

    @Override
    public @Nullable BattleParticipantState getParticipant(final BattleParticipantHandle handle) {
        return participants.get(handle);
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
