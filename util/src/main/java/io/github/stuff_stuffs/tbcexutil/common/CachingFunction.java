package io.github.stuff_stuffs.tbcexutil.common;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public final class CachingFunction<T, R> implements Function<T, R>, Iterable<Map.Entry<T, R>> {
    private final Function<T, R> func;
    private final Map<T, R> cache;

    public CachingFunction(final Function<T, R> func) {
        this.func = func;
        cache = new Object2ReferenceOpenHashMap<>();
    }

    @Override
    public R apply(final T t) {
        return cache.computeIfAbsent(t, func);
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<T, R>> iterator() {
        return Iterators.unmodifiableIterator(cache.entrySet().iterator());
    }

    public int cacheSize() {
        return cache.size();
    }
}
