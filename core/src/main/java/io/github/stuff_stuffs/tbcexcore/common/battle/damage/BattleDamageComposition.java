package io.github.stuff_stuffs.tbcexcore.common.battle.damage;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public final class BattleDamageComposition implements Iterable<BattleDamageType> {
    public static final Codec<BattleDamageComposition> CODEC = Codec.unboundedMap(BattleDamageType.REGISTRY, Codec.DOUBLE).xmap(map -> new BattleDamageComposition(new Reference2DoubleOpenHashMap<>(map)), composition -> composition.percents);
    private final Reference2DoubleMap<BattleDamageType> percents;

    private BattleDamageComposition(final Reference2DoubleMap<BattleDamageType> raw) {
        percents = raw;
    }

    public double getPercent(final BattleDamageType type) {
        return percents.getDouble(type);
    }

    @NotNull
    @Override
    public Iterator<BattleDamageType> iterator() {
        return Iterators.unmodifiableIterator(percents.keySet().iterator());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Reference2DoubleOpenHashMap<BattleDamageType> weights;

        private Builder() {
            weights = new Reference2DoubleOpenHashMap<>();
        }

        public Builder setWeight(final BattleDamageType type, final double weight) {
            if (weight < 0) {
                throw new RuntimeException("Cannot have negative weights");
            }
            weights.put(type, weight);
            return this;
        }

        public Builder addWeight(final BattleDamageType type, final double weight) {
            final double prev = weights.addTo(type, weight);
            if (prev + weight < 0) {
                throw new RuntimeException("Cannot have negative weights");
            }
            return this;
        }

        public BattleDamageComposition build() {
            final Reference2DoubleMap<BattleDamageType> weighted = new Reference2DoubleOpenHashMap<>();
            weighted.defaultReturnValue(0);
            double sum = 0;
            for (final Reference2DoubleMap.Entry<BattleDamageType> entry : weights.reference2DoubleEntrySet()) {
                final double v = entry.getDoubleValue();
                sum += v;
                if (v != 0) {
                    weighted.put(entry.getKey(), v);
                }
            }
            if (sum == 0) {
                throw new RuntimeException("Cannot have a composition with 0 entries");
            }
            for (final Reference2DoubleMap.Entry<BattleDamageType> entry : weighted.reference2DoubleEntrySet()) {
                entry.setValue(entry.getDoubleValue() / sum);
            }
            return new BattleDamageComposition(weighted);
        }
    }
}
