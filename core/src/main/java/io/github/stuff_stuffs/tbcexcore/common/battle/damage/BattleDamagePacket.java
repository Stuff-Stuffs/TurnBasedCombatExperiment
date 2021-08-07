package io.github.stuff_stuffs.tbcexcore.common.battle.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;

import java.util.Map;

public final class BattleDamagePacket {
    public static final Codec<BattleDamagePacket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(BattleDamageType.REGISTRY, Codec.DOUBLE).fieldOf("amounts").forGetter(packet -> packet.amounts),
            BattleDamageSource.CODEC.fieldOf("source").forGetter(packet -> packet.source)
    ).apply(instance, BattleDamagePacket::new));
    private final Reference2DoubleMap<BattleDamageType> amounts;
    private final BattleDamageSource source;
    private final double sum;

    public BattleDamagePacket(final BattleDamageComposition composition, final BattleDamageSource source, final double amount) {
        this.source = source;
        amounts = new Reference2DoubleOpenHashMap<>();
        sum = amount;
        for (final BattleDamageType type : composition) {
            amounts.put(type, amount * composition.getPercent(type));
        }
    }

    private BattleDamagePacket(final Map<BattleDamageType, Double> amounts, final BattleDamageSource source) {
        this.amounts = new Reference2DoubleOpenHashMap<>(amounts);
        this.source = source;
        double s = 0;
        final DoubleIterator iterator = this.amounts.values().iterator();
        while (iterator.hasNext()) {
            s += iterator.nextDouble();
        }
        sum = s;
    }

    private BattleDamagePacket(final Reference2DoubleMap<BattleDamageType> amounts, final BattleDamageSource source, final double sum) {
        this.amounts = amounts;
        this.source = source;
        this.sum = sum;
    }

    public BattleDamagePacket screen(final BattleDamageResistances resistances) {
        double s = 0;
        final Reference2DoubleMap<BattleDamageType> amounts = new Reference2DoubleOpenHashMap<>();
        for (final Reference2DoubleMap.Entry<BattleDamageType> entry : this.amounts.reference2DoubleEntrySet()) {
            final double mult = entry.getDoubleValue() * (1 - resistances.getPercentBlocked(entry.getKey()));
            if (mult > 0) {
                s += mult;
                amounts.put(entry.getKey(), mult);
            }
        }
        return new BattleDamagePacket(amounts, source, s);
    }

    public double getDamage(final BattleDamageType type) {
        return amounts.getDouble(type);
    }

    public double getTotalDamage() {
        return sum;
    }
}
