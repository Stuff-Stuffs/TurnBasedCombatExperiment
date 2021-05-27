package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public final class DamageComposition implements Iterable<DamageType> {
    public static final Codec<DamageComposition> CODEC = Codec.unboundedMap(DamageType.REGISTRY, Codec.DOUBLE).xmap(m -> new DamageComposition(new Reference2DoubleOpenHashMap<>(m)), composition -> composition.composition);
    private final Reference2DoubleMap<DamageType> composition;

    private DamageComposition(final Reference2DoubleMap<DamageType> composition) {
        this.composition = composition;
    }

    public double get(final DamageType damageType) {
        return composition.getDouble(damageType);
    }

    @NotNull
    @Override
    public Iterator<DamageType> iterator() {
        return Iterators.unmodifiableIterator(composition.keySet().iterator());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Reference2DoubleMap<DamageType> composition;

        private Builder() {
            composition = new Reference2DoubleOpenHashMap<>();
        }

        public Builder set(final DamageType damageType, final double amount) {
            if (amount < 0) {
                throw new IllegalArgumentException();
            }
            composition.put(damageType, amount);
            return this;
        }

        public DamageComposition build() {
            final Reference2DoubleMap<DamageType> processed = new Reference2DoubleOpenHashMap<>(composition.size());
            double sum = 0;
            for (final DoubleIterator iterator = composition.values().iterator(); iterator.hasNext(); ) {
                sum += iterator.nextDouble();
            }
            for (final Reference2DoubleMap.Entry<DamageType> entry : composition.reference2DoubleEntrySet()) {
                final double amount = sum == 0 ? 0 : entry.getDoubleValue() / sum;
                processed.put(entry.getKey(), amount);
            }
            return new DamageComposition(processed);
        }
    }
}
