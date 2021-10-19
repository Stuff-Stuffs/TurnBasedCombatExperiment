package io.github.stuff_stuffs.tbcexcore.common.battle.state;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexcore.common.TBCExCore;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventKey;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventMap;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.MutableEventHolder;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.battle.*;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class BattleEvents {
    private static final Map<Identifier, Pair<EventKey<?, ?>, Factory<?, ?>>> EVENT_FACTORIES = new Object2ReferenceOpenHashMap<>();
    private static final BiMap<Identifier, EventKey<?, ?>> IDENTIFIER_EVENT_MAPPING = HashBiMap.create();

    public static <Mut, View> void register(final Identifier id, final EventKey<Mut, View> key, final Factory<Mut, View> factory) {
        if (IDENTIFIER_EVENT_MAPPING.put(id, key) != null) {
            //TODO
            throw new RuntimeException();
        }
        EVENT_FACTORIES.put(id, Pair.of(key, factory));
    }

    public static EventKey<?, ?> getKey(final Identifier id) {
        final Pair<EventKey<?, ?>, Factory<?, ?>> pair = EVENT_FACTORIES.get(id);
        if (pair == null) {
            return null;
        }
        return pair.getFirst();
    }

    public static void setup(final EventMap map) {
        for (final Pair<EventKey<?, ?>, Factory<?, ?>> pair : EVENT_FACTORIES.values()) {
            pair.getSecond().accept(pair.getFirst(), map);
        }
    }

    public interface Factory<Mut, View> {
        void accept(EventKey<?, ?> key, EventMap map);
    }

    private BattleEvents() {
    }

    static {
        register(TBCExCore.createId("pre_participant_join"), BattleStateView.PRE_PARTICIPANT_JOIN_EVENT, (key, map) -> {
            final EventKey<PreParticipantJoinEvent.Mut, PreParticipantJoinEvent> castedKey = (EventKey<PreParticipantJoinEvent.Mut, PreParticipantJoinEvent>) key;
            map.register(castedKey, new MutableEventHolder.BasicEventHolder<>(castedKey, view -> (battleState, participant) -> {
                view.onParticipantJoin(battleState, participant);
                return false;
            }, events -> (battleState, participant) -> {
                boolean canceled = false;
                for (final PreParticipantJoinEvent.Mut event : events) {
                    canceled |= event.onParticipantJoin(battleState, participant);
                }
                return canceled;
            }));
        });

        register(TBCExCore.createId("post_particpant_join"), BattleStateView.POST_PARTICIPANT_JOIN_EVENT, (key, map) -> {
            final EventKey<PostParticipantJoinEvent.Mut, PostParticipantJoinEvent> castedKey = (EventKey<PostParticipantJoinEvent.Mut, PostParticipantJoinEvent>) key;
            map.register(castedKey, new MutableEventHolder.BasicEventHolder<>(castedKey, view -> view::onParticipantJoin, events -> (battleState, participant) -> {
                for (final PostParticipantJoinEvent.Mut event : events) {
                    event.onParticipantJoin(battleState, participant);
                }
            }));
        });

        register(TBCExCore.createId("pre_participant_leave"), BattleStateView.PRE_PARTICIPANT_LEAVE_EVENT, (key, map) -> {
            final EventKey<PreParticipantLeaveEvent.Mut, PreParticipantLeaveEvent> castedKey = (EventKey<PreParticipantLeaveEvent.Mut, PreParticipantLeaveEvent>) key;
            map.register(castedKey, new MutableEventHolder.BasicEventHolder<>(castedKey, view -> (battleState, participantState) -> {
                view.onParticipantLeave(battleState, participantState);
                return false;
            }, events -> (battleState, participantState) -> {
                boolean canceled = false;
                for (final PreParticipantLeaveEvent.Mut event : events) {
                    canceled |= event.onParticipantLeave(battleState, participantState);
                }
                return canceled;
            }));
        });

        register(TBCExCore.createId("post_participant_leave"), BattleStateView.POST_PARTICIPANT_LEAVE_EVENT, (key, map) -> {
            final EventKey<PostParticipantLeaveEvent.Mut, PostParticipantLeaveEvent> castedKey = (EventKey<PostParticipantLeaveEvent.Mut, PostParticipantLeaveEvent>) key;
            map.register(castedKey, new MutableEventHolder.BasicEventHolder<>(castedKey, view -> view::onParticipantLeave, events -> (battleState, participantState) -> {
                for (final PostParticipantLeaveEvent.Mut event : events) {
                    event.onParticipantLeave(battleState, participantState);
                }
            }));
        });

        register(TBCExCore.createId("advance_turn"), BattleStateView.ADVANCE_TURN_EVENT, (key, map) -> {
            final EventKey<AdvanceTurnEvent.Mut, AdvanceTurnEvent> castedKey = (EventKey<AdvanceTurnEvent.Mut, AdvanceTurnEvent>) key;
            map.register(castedKey, new MutableEventHolder.BasicEventHolder<>(castedKey, view -> view::onAdvanceTurn, events -> ((battleState, current) -> {
                for (final AdvanceTurnEvent.Mut event : events) {
                    event.onAdvanceTurn(battleState, current);
                }
            })));
        });
    }
}
