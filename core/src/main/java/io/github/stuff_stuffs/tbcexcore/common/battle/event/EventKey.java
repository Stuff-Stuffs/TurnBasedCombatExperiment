package io.github.stuff_stuffs.tbcexcore.common.battle.event;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import java.util.Map;

public final class EventKey<Mut, View> {
    private static final Map<Pair<Class<?>, Class<?>>, EventKey<?, ?>> CACHE = new Object2ReferenceOpenHashMap<>();
    private final Class<Mut> type;
    private final Class<View> viewType;

    private EventKey(final Class<Mut> type, final Class<View> viewType) {
        this.type = type;
        this.viewType = viewType;
    }

    public Class<Mut> getType() {
        return type;
    }

    public Class<View> getViewType() {
        return viewType;
    }

    @Override
    public String toString() {
        return "EventKey[" + "type=" + type + ", " + "viewType=" + viewType + ']';
    }

    public static <Mut, View> EventKey<Mut, View> get(final Class<Mut> mut, final Class<View> view) {
        return (EventKey<Mut, View>) CACHE.computeIfAbsent(Pair.of(mut, view), pair -> new EventKey<>(pair.getFirst(), pair.getSecond()));
    }
}
