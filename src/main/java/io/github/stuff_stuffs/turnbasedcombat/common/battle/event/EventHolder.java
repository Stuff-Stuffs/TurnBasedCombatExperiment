package io.github.stuff_stuffs.turnbasedcombat.common.battle.event;

import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public interface EventHolder<T> {

    Handle register(T listener);

    void unregister(Handle handle);

    T invoker();

    class BasicEventHolder<T> implements EventHolder<T> {
        private final Int2ReferenceMap<T> events;
        private final Function<Collection<T>, T> factory;
        private int nextId = 0;
        private T invoker;

        public BasicEventHolder(final Function<Collection<T>, T> factory) {
            events = new Int2ReferenceLinkedOpenHashMap<>();
            this.factory = factory;
            invoker = factory.apply(events.values());
        }

        @Override
        public Handle register(final T listener) {
            final int id = nextId++;
            events.put(id, listener);
            invoker = factory.apply(events.values());
            return new Handle(this, id);
        }

        @Override
        public void unregister(final Handle handle) {
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

    class SortedEventHolder<T> implements EventHolder<T> {
        private final Int2ReferenceMap<T> events;
        private final Comparator<? super T> comparator;
        private final List<T> sorted;
        private final Function<Collection<T>, T> factory;
        private int nextId = 0;
        private T invoker;

        public SortedEventHolder(final Function<Collection<T>, T> factory, final Comparator<? super T> comparator) {
            events = new Int2ReferenceLinkedOpenHashMap<>();
            this.comparator = comparator;
            sorted = new ReferenceArrayList<>();
            this.factory = factory;
            invoker = factory.apply(sorted);
        }

        @Override
        public Handle register(final T listener) {
            final int id = nextId++;
            sorted.add(listener);
            sorted.sort(comparator);
            events.put(id, listener);
            invoker = factory.apply(sorted);
            return new Handle(this, id);
        }

        @Override
        public void unregister(final Handle handle) {
            if (handle.holder != this) {
                throw new RuntimeException();
            }
            handle.destroyed = true;
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

    class Handle {
        private final EventHolder<?> holder;
        private final int id;
        private boolean destroyed = false;

        private Handle(final EventHolder<?> holder, final int id) {
            this.holder = holder;
            this.id = id;
        }

        public boolean isDestroyed() {
            return destroyed;
        }

        public void destroy() {
            if (!destroyed) {
                holder.unregister(this);
                destroyed = true;
            }
        }
    }
}
