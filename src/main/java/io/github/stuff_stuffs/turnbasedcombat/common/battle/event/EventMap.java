package io.github.stuff_stuffs.turnbasedcombat.common.battle.event;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class EventMap {
    private final Map<EventKey<?, ?>, MutableEventHolder<?, ?>> mutEventMap;
    private final Map<EventKey<?, ?>, EventHolder<?, ?>> eventMap;

    public EventMap() {
        mutEventMap = new Object2ObjectOpenHashMap<>();
        eventMap = new Object2ObjectOpenHashMap<>();
    }

    public <T, V> void register(final EventKey<T, V> key, final EventHolder<T, V> eventHolder) {
        if (eventMap.containsKey(key) || mutEventMap.containsKey(key)) {
            throw new RuntimeException("Duplicate event keys found!");
        }
        if (eventHolder instanceof MutableEventHolder<?, ?> mut) {
            mutEventMap.put(key, mut);
        } else {
            eventMap.put(key, eventHolder);
        }
    }

    public <T, V> @Nullable MutableEventHolder<T, V> getMut(final EventKey<T, V> key) {
        return (MutableEventHolder<T, V>) mutEventMap.get(key);
    }

    public <T, V> @Nullable EventHolder<T, V> get(final EventKey<T, V> key) {
        EventHolder<?, ?> holder = mutEventMap.get(key);
        if (holder == null) {
            holder = eventMap.get(key);
        }
        return (EventHolder<T, V>) holder;
    }
}
