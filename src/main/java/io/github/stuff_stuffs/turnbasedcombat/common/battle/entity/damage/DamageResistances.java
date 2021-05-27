package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;

public final class DamageResistances {
    public static final Codec<DamageResistances> CODEC = Codec.unboundedMap(DamageType.REGISTRY, Codec.DOUBLE).xmap(m -> new DamageResistances(new Reference2DoubleOpenHashMap<>(m)), screen -> screen.unprocessed);
    private final Reference2DoubleMap<DamageType> unprocessed;
    private final Reference2DoubleMap<DamageType> amounts;

    private DamageResistances(final Reference2DoubleMap<DamageType> unprocessed) {
        this.unprocessed = unprocessed;
        amounts = process(unprocessed);
    }

    private static Reference2DoubleMap<DamageType> process(final Reference2DoubleMap<DamageType> unprocessed) {
        final Reference2DoubleMap<DamageType> processed = new Reference2DoubleOpenHashMap<>(32);
        processed.defaultReturnValue(0);
        for (final DamageType damageType : DamageType.REGISTRY) {
            for (final Reference2DoubleMap.Entry<DamageType> entry : unprocessed.reference2DoubleEntrySet()) {
                if (damageType == entry.getKey() || damageType.isChildOf(entry.getKey())) {
                    processed.put(damageType, Math.max(processed.getDouble(damageType), entry.getDoubleValue()));
                }
            }
        }
        return processed;
    }

    public double get(final DamageType type) {
        return amounts.getDouble(type);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Reference2DoubleMap<DamageType> amounts;

        private Builder() {
            amounts = new Reference2DoubleOpenHashMap<>();
        }

        public Builder set(final DamageType type, final double amount) {
            if(amount>1) {
                throw new IllegalArgumentException();
            }
            amounts.put(type, amount);
            return this;
        }

        public DamageResistances build() {
            return new DamageResistances(new Reference2DoubleOpenHashMap<>(amounts));
        }
    }
}
