package io.github.stuff_stuffs.tbcexcore.common.battle.participant;

import com.mojang.datafixers.util.Pair;
import io.github.stuff_stuffs.tbcexcore.common.TurnBasedCombatExperiment;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventKey;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.EventMap;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.MutableEventHolder;
import io.github.stuff_stuffs.tbcexcore.common.battle.event.participant.*;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class BattleParticipantEvents {
    private static final Map<Identifier, Pair<EventKey<?, ?>, EventFactory<?, ?>>> EVENT_FACTORIES = new Object2ReferenceOpenHashMap<>();

    public static <View, Mut> void register(final Identifier id, final EventKey<Mut, View> key, final EventFactory<Mut, View> factory) {
        if (EVENT_FACTORIES.put(id, Pair.of(key, factory)) != null) {
            //TODO
            throw new RuntimeException();
        }
    }

    public static @Nullable EventKey<?, ?> getKey(final Identifier id) {
        final Pair<EventKey<?, ?>, EventFactory<?, ?>> pair = EVENT_FACTORIES.get(id);
        if (pair == null) {
            return null;
        }
        return pair.getFirst();
    }

    public static void setup(final EventMap eventMap) {
        for (final Pair<EventKey<?, ?>, EventFactory<?, ?>> pair : EVENT_FACTORIES.values()) {
            pair.getSecond().accept(pair.getFirst(), eventMap);
        }
    }

    public interface EventFactory<Mut, View> {
        void accept(EventKey<?, ?> key, EventMap map);
    }

    private BattleParticipantEvents() {
    }

    static {
        register(TurnBasedCombatExperiment.createId("pre_equipment_change"), BattleParticipantStateView.PRE_EQUIPMENT_CHANGE_EVENT, (key, map) -> {
            final EventKey<PreEquipmentChangeEvent.Mut, PreEquipmentChangeEvent> castedKey = (EventKey<PreEquipmentChangeEvent.Mut, PreEquipmentChangeEvent>) key;
            map.register(castedKey, new MutableEventHolder.BasicEventHolder<>(castedKey, view -> (state, slot, oldEquipment, newEquipment) -> {
                view.onEquipmentChange(state, slot, oldEquipment, newEquipment);
                return false;
            }, events -> (state, slot, oldEquipment, newEquipment) -> {
                boolean canceled = false;
                for (final PreEquipmentChangeEvent.Mut event : events) {
                    canceled |= event.onEquipmentChange(state, slot, oldEquipment, newEquipment);
                }
                return canceled;
            }));
        });

        register(TurnBasedCombatExperiment.createId("post_equipment_change"), BattleParticipantStateView.POST_EQUIPMENT_CHANGE_EVENT, (key, map) -> {
            final EventKey<PostEquipmentChangeEvent.Mut, PostEquipmentChangeEvent> castedKey = (EventKey<PostEquipmentChangeEvent.Mut, PostEquipmentChangeEvent>) key;
            map.register(castedKey, new MutableEventHolder.BasicEventHolder<>(castedKey, view -> view::onEquipmentChange, events -> (state, slot, oldEquipment, newEquipment) -> {
                for (final PostEquipmentChangeEvent.Mut event : events) {
                    event.onEquipmentChange(state, slot, oldEquipment, newEquipment);
                }
            }));
        });

        register(TurnBasedCombatExperiment.createId("pre_damage"), BattleParticipantStateView.PRE_DAMAGE_EVENT, (key, map) -> {
            final EventKey<PreDamageEvent.Mut, PreDamageEvent> castedKey = (EventKey<PreDamageEvent.Mut, PreDamageEvent>) key;
            map.register(castedKey, new MutableEventHolder.BasicEventHolder<>(castedKey, view -> (state, damagePacket) -> {
                view.onDamage(state, damagePacket);
                return damagePacket;
            }, events -> (state, damagePacket) -> {
                for (final PreDamageEvent.Mut event : events) {
                    damagePacket = event.onDamage(state, damagePacket);
                }
                return damagePacket;
            }));
        });

        register(TurnBasedCombatExperiment.createId("post_damage"), BattleParticipantStateView.POST_DAMAGE_EVENT, (key, map) -> {
            final EventKey<PostDamageEvent.Mut, PostDamageEvent> castedKey = (EventKey<PostDamageEvent.Mut, PostDamageEvent>) key;
            map.register(castedKey, new MutableEventHolder.BasicEventHolder<>(castedKey, view -> view::onDamage, events -> (state, damagePacket) -> {
                for (final PostDamageEvent.Mut event : events) {
                    event.onDamage(state, damagePacket);
                }
            }));
        });

        register(TurnBasedCombatExperiment.createId("pre_move"), BattleParticipantStateView.PRE_MOVE_EVENT, (key, map) -> {
            final EventKey<PreMoveEvent.Mut, PreMoveEvent> castedKey = (EventKey<PreMoveEvent.Mut, PreMoveEvent>) key;
            map.register(castedKey, new MutableEventHolder.BasicEventHolder<>(castedKey, view -> (battleParticipantStateView, pos, path) -> {
                view.onMove(battleParticipantStateView, pos, path);
                return false;
            }, events -> (battleParticipantState, pos, path) -> {
                boolean canceled = false;
                for (final PreMoveEvent.Mut event : events) {
                    canceled |= event.onMove(battleParticipantState, pos, path);
                }
                return canceled;
            }));
        });

        register(TurnBasedCombatExperiment.createId("post_move"), BattleParticipantStateView.POST_MOVE_EVENT, (key, map) -> {
            final EventKey<PostMoveEvent.Mut, PostMoveEvent> castedKey = (EventKey<PostMoveEvent.Mut, PostMoveEvent>) key;
            map.register(castedKey, new MutableEventHolder.BasicEventHolder<>(castedKey, view -> view::onMove, events -> (battleParticipantState, path) -> {
                for (final PostMoveEvent.Mut event : events) {
                    event.onMove(battleParticipantState, path);
                }
            }));
        });
    }
}