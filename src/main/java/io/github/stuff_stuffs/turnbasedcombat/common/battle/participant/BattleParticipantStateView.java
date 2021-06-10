package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventKey;
import org.jetbrains.annotations.Nullable;

public interface BattleParticipantStateView {
    <T, V> @Nullable EventHolder<T, V> getEvent(EventKey<T, V> key);
}
