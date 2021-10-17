package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import java.util.Map;

public final class ParticipantComponentKey<Mut extends View, View extends ParticipantComponent> {
    private static final Map<Pair<Class<?>, Class<?>>, ParticipantComponentKey<?, ?>> CACHE = new Object2ReferenceOpenHashMap<>();
    private final Class<Mut> mut;
    private final Class<View> view;

    private ParticipantComponentKey(final Class<Mut> mut, final Class<View> view) {
        this.mut = mut;
        this.view = view;
    }

    @Override
    public String toString() {
        return "ParticipantComponentKey[" + "mut=" + mut + ", view=" + view + ']';
    }

    public static <Mut extends View, View extends ParticipantComponent> ParticipantComponentKey<Mut, View> get(final Class<Mut> mut, final Class<View> view) {
        return (ParticipantComponentKey<Mut, View>) CACHE.computeIfAbsent(Pair.of(mut, view), p -> new ParticipantComponentKey<>(mut, view));
    }
}
