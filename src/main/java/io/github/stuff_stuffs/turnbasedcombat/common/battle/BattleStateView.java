package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventKey;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantStateView;
import org.jetbrains.annotations.Nullable;

public interface BattleStateView {
    @Nullable BattleParticipantStateView getParticipant(BattleParticipantHandle handle);

    boolean isEnded();

    <T, V> EventHolder<T, V> getEvent(EventKey<T, V> key);
}
