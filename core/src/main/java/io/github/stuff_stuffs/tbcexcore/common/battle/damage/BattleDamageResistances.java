package io.github.stuff_stuffs.tbcexcore.common.battle.damage;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;

public final class BattleDamageResistances {
    public static final Codec<BattleDamageResistances> CODEC = Codec.unboundedMap(BattleDamageType.REGISTRY.getCodec(), Codec.DOUBLE).xmap(map -> new BattleDamageResistances(new Reference2DoubleOpenHashMap<>(map)), resistances -> resistances.percents);
    private final Reference2DoubleMap<BattleDamageType> percents;
    private final Reference2DoubleMap<BattleDamageType> processed;

    private BattleDamageResistances(final Reference2DoubleMap<BattleDamageType> percents) {
        this.percents = percents;
        final Reference2DoubleOpenHashMap<BattleDamageType> processed = new Reference2DoubleOpenHashMap<>();
        for (final Reference2DoubleMap.Entry<BattleDamageType> entry : percents.reference2DoubleEntrySet()) {
            final BattleDamageType parent = entry.getKey();
            for (final BattleDamageType type : BattleDamageType.REGISTRY) {
                if (type == parent || parent.isParentOf(type)) {
                    processed.addTo(type, entry.getDoubleValue());
                }
            }
        }
        this.processed = processed;
    }

    public double getRawPercentBlocked(final BattleDamageType type) {
        return percents.getDouble(type);
    }

    public double getPercentBlocked(final BattleDamageType type) {
        return Math.min(processed.getDouble(type), 1);
    }

    public static final class Builder {
        private final Reference2DoubleOpenHashMap<BattleDamageType> weights;

        private Builder() {
            weights = new Reference2DoubleOpenHashMap<>();
        }

        public Builder set(final BattleDamageType type, final double percent) {
            if (percent < 0 || percent > 1) {
                throw new RuntimeException("Invalid percent");
            }
            weights.put(type, percent);
            return this;
        }

        public BattleDamageResistances build() {
            return new BattleDamageResistances(new Reference2DoubleOpenHashMap<>(weights));
        }
    }
}
