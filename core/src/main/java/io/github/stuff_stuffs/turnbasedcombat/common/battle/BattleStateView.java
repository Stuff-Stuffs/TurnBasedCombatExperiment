package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventHolder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.EventKey;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.battle.PostParticipantJoinEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.battle.PostParticipantLeaveEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.battle.PreParticipantJoinEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.event.battle.PreParticipantLeaveEvent;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.world.BattleBounds;
import org.jetbrains.annotations.Nullable;

public interface BattleStateView {
    EventKey<PreParticipantJoinEvent.Mut, PreParticipantJoinEvent> PRE_PARTICIPANT_JOIN_EVENT = new EventKey<>(PreParticipantJoinEvent.Mut.class, PreParticipantJoinEvent.class);
    EventKey<PostParticipantJoinEvent.Mut, PostParticipantJoinEvent> POST_PARTICIPANT_JOIN_EVENT = new EventKey<>(PostParticipantJoinEvent.Mut.class, PostParticipantJoinEvent.class);
    EventKey<PreParticipantLeaveEvent.Mut, PreParticipantLeaveEvent> PRE_PARTICIPANT_LEAVE_EVENT = new EventKey<>(PreParticipantLeaveEvent.Mut.class, PreParticipantLeaveEvent.class);
    EventKey<PostParticipantLeaveEvent.Mut, PostParticipantLeaveEvent> POST_PARTICIPANT_LEAVE_EVENT = new EventKey<>(PostParticipantLeaveEvent.Mut.class, PostParticipantLeaveEvent.class);

    @Nullable BattleParticipantStateView getParticipant(BattleParticipantHandle handle);

    @Nullable BattleParticipantHandle getCurrentTurn();

    boolean isEnded();

    <T, V> EventHolder<T, V> getEvent(EventKey<T, V> key);

    BattleBounds getBounds();
}
