package io.github.stuff_stuffs.tbcexcore.common.battle.state.component;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import java.util.Map;

public final class BattleComponentKey<Mut extends View, View extends BattleComponent> {
    private static final Map<Pair<Class<?>, Class<?>>, BattleComponentKey<?, ?>> CACHE = new Object2ReferenceOpenHashMap<>();
    private final Class<Mut> mut;
    private final Class<View> view;

    private BattleComponentKey(final Class<Mut> mut, final Class<View> view) {
        this.mut = mut;
        this.view = view;
    }

    @Override
    public String toString() {
        return "BattleComponentKey[" + "mut=" + mut + ", view=" + view + ']';
    }

    public static <Mut extends View, View extends BattleComponent> BattleComponentKey<Mut, View> get(final Class<Mut> mut, final Class<View> view) {
        return (BattleComponentKey<Mut, View>) CACHE.computeIfAbsent(Pair.of(mut, view), p -> new BattleComponentKey<>(mut, view));
    }
}
