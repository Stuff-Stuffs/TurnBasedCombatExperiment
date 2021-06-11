package io.github.stuff_stuffs.turnbasedcombat.common.battle.event;

import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public interface MutableEventHolder<T, V> extends EventHolder<T, V> {
    EventListenerHandle registerMut(T listener);

    class BasicEventHolder<T, V> implements MutableEventHolder<T, V> {
        private final Int2ReferenceMap<T> events;
        private final @Nullable Function<V, T> viewConverter;
        private final Class<T> clazz;
        private final Function<Collection<T>, T> factory;
        private int nextId = 0;
        private T invoker;

        public BasicEventHolder(final EventKey<T, V> key, final Function<Collection<T>, T> factory) {
            clazz = key.type();
            this.factory = factory;
            viewConverter = null;
            events = new Int2ReferenceLinkedOpenHashMap<>();
            invoker = factory.apply(events.values());
        }

        public BasicEventHolder(final EventKey<T, V> key, @Nullable final Function<V, T> viewConverter, final Function<Collection<T>, T> factory) {
            this.viewConverter = viewConverter;
            clazz = key.type();
            events = new Int2ReferenceLinkedOpenHashMap<>();
            this.factory = factory;
            invoker = factory.apply(events.values());
        }

        @Override
        public EventListenerHandle registerMut(final T listener) {
            final int id = nextId++;
            events.put(id, listener);
            invoker = factory.apply(events.values());
            return new EventListenerHandle(this, id, this::unregister);
        }

        @Override
        public EventListenerHandle register(final V viewListener) {
            if (clazz.isInstance(viewListener)) {
                return registerMut((T) viewListener);
            }
            if (viewConverter == null) {
                throw new UnsupportedOperationException("Tried to register view listener on unsupported event");
            }
            return registerMut(viewConverter.apply(viewListener));
        }

        private void unregister(final EventListenerHandle handle) {
            if (handle.holder != this) {
                throw new RuntimeException();
            }
            events.remove(handle.id);
            invoker = factory.apply(events.values());
        }

        @Override
        public T invoker() {
            return invoker;
        }
    }

    class SortedEventHolder<T, V> implements MutableEventHolder<T, V> {
        private final Int2ReferenceMap<T> events;
        private final @Nullable Function<V, T> viewConverter;
        private final Class<T> clazz;
        private final Comparator<? super T> comparator;
        private final List<T> sorted;
        private final Function<Collection<T>, T> factory;
        private int nextId = 0;
        private T invoker;

        public SortedEventHolder(final EventKey<T, V> key, final Function<Collection<T>, T> factory, final Comparator<? super T> comparator) {
            this(key, null, factory, comparator);
        }

        public SortedEventHolder(final EventKey<T, V> key, @Nullable final Function<V, T> viewConverter, final Function<Collection<T>, T> factory, final Comparator<? super T> comparator) {
            events = new Int2ReferenceLinkedOpenHashMap<>();
            clazz = key.type();
            this.viewConverter = viewConverter;
            this.comparator = comparator;
            sorted = new ReferenceArrayList<>();
            this.factory = factory;
            invoker = factory.apply(sorted);
        }

        @Override
        public EventListenerHandle registerMut(final T listener) {
            final int id = nextId++;
            sorted.add(listener);
            sorted.sort(comparator);
            events.put(id, listener);
            invoker = factory.apply(sorted);
            return new EventListenerHandle(this, id, this::unregister);
        }

        @Override
        public EventListenerHandle register(final V viewListener) {
            if (clazz.isInstance(viewListener)) {
                return registerMut((T) viewListener);
            }
            if (viewConverter == null) {
                throw new UnsupportedOperationException("Tried to register view listener on unsupported event");
            }
            return registerMut(viewConverter.apply(viewListener));
        }

        private void unregister(final EventListenerHandle handle) {
            if (handle.holder != this) {
                throw new RuntimeException();
            }
            events.remove(handle.id);
            sorted.clear();
            sorted.addAll(events.values());
            sorted.sort(comparator);
            invoker = factory.apply(sorted);
        }

        @Override
        public T invoker() {
            return invoker;
        }
    }
}
