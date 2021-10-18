package io.github.stuff_stuffs.tbcexcore.common.battle.state;

import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventHolder;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventKey;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.battle.*;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.component.BattleComponent;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.component.BattleComponentKey;
import io.github.stuff_stuffs.tbcexcore.common.battle.world.BattleBounds;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public interface BattleStateView {
    EventKey<PreParticipantJoinEvent.Mut, PreParticipantJoinEvent> PRE_PARTICIPANT_JOIN_EVENT = EventKey.get(PreParticipantJoinEvent.Mut.class, PreParticipantJoinEvent.class);
    EventKey<PostParticipantJoinEvent.Mut, PostParticipantJoinEvent> POST_PARTICIPANT_JOIN_EVENT = EventKey.get(PostParticipantJoinEvent.Mut.class, PostParticipantJoinEvent.class);
    EventKey<PreParticipantLeaveEvent.Mut, PreParticipantLeaveEvent> PRE_PARTICIPANT_LEAVE_EVENT = EventKey.get(PreParticipantLeaveEvent.Mut.class, PreParticipantLeaveEvent.class);
    EventKey<AdvanceTurnEvent.Mut, AdvanceTurnEvent> ADVANCE_TURN_EVENT = EventKey.get(AdvanceTurnEvent.Mut.class, AdvanceTurnEvent.class);
    EventKey<PostParticipantLeaveEvent.Mut, PostParticipantLeaveEvent> POST_PARTICIPANT_LEAVE_EVENT = EventKey.get(PostParticipantLeaveEvent.Mut.class, PostParticipantLeaveEvent.class);

    BattleHandle getHandle();

    @Nullable BattleParticipantStateView getParticipant(BattleParticipantHandle handle);

    @Nullable BattleParticipantHandle getCurrentTurn();

    boolean isEnded();

    <T, V> EventHolder<T, V> getEvent(EventKey<T, V> key);

    <View extends BattleComponent> @Nullable View getComponent(BattleComponentKey<?, View> key);

    BattleBounds getBounds();

    Iterator<BattleParticipantHandle> getParticipants();
}
