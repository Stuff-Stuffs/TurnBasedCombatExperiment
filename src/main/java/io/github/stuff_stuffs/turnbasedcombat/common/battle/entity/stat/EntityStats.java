package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat;

import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.EntityStateView;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class EntityStats {
    private final Map<EntityStatType<?>, StatModifiers<?>> stats;

    public EntityStats() {
        stats = new Reference2ObjectOpenHashMap<>();
    }

    private <T> @Nullable StatModifiers<T> getModifiers(final EntityStatType<T> type) {
        return (StatModifiers<T>) stats.get(type);
    }

    private <T> StatModifiers<T> getOrCreateModifiers(final EntityStatType<T> type) {
        return (StatModifiers<T>) stats.computeIfAbsent(type, t -> new StatModifiers<>());
    }

    public <T> Handle addModifier(final EntityStatType<T> type, final EntityStatModifier<T> modifier) {
        return getOrCreateModifiers(type).add(modifier);
    }

    public <T> T modify(final EntityStatType<T> type, final T initialValue, final EntityStateView view) {
        final StatModifiers<T> modifiers = getModifiers(type);
        if (modifiers == null) {
            return initialValue;
        }
        return modifiers.modify(initialValue, view);
    }


    private static class StatModifiers<T> {
        private static final Comparator<EntityStatModifier<?>> COMPARATOR = Comparator.comparingInt(EntityStatModifier::getApplicationStage);
        private final Int2ReferenceMap<EntityStatModifier<T>> modifiers;
        private int nextId = 0;
        private final List<EntityStatModifier<T>> sorted;


        public StatModifiers() {
            modifiers = new Int2ReferenceOpenHashMap<>();
            sorted = new ReferenceArrayList<>();
        }

        public Handle add(final EntityStatModifier<T> modifier) {
            final int id = nextId++;
            modifiers.put(id, modifier);
            sorted.add(modifier);
            sorted.sort(COMPARATOR);
            return new Handle(this, id);
        }

        private void remove(final int id) {
            final EntityStatModifier<T> removed = modifiers.remove(id);
            if (removed == null) {
                throw new RuntimeException();
            }
            sorted.remove(removed);
        }

        public T modify(T val, final EntityStateView view) {
            for (final EntityStatModifier<T> modifier : sorted) {
                val = modifier.modify(val, view);
            }
            return val;
        }
    }

    public static final class Handle {
        private final StatModifiers<?> modifiers;
        private final int id;
        private boolean destroyed;

        private Handle(final StatModifiers<?> modifiers, final int id) {
            this.modifiers = modifiers;
            this.id = id;
        }

        public void destroy() {
            if (!destroyed) {
                destroyed = true;
                modifiers.remove(id);
            }
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }
}
