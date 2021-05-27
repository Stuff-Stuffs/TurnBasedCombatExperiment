package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DamagePacket(BattleDamageSource source, double amount) {
    public static final Codec<DamagePacket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BattleDamageSource.CODEC.fieldOf("source").forGetter(DamagePacket::source),
            Codec.DOUBLE.fieldOf("amount").forGetter(DamagePacket::amount)
    ).apply(instance, DamagePacket::new));

    public DamagePacket screen(final DamageResistances screen) {
        final DamageComposition.Builder builder = DamageComposition.builder();
        double a = 0;
        DamageType targetNormalizer = null;
        for (final DamageType damageType : source.composition()) {
            if (targetNormalizer == null && (1 - screen.get(damageType)) != 0) {
                targetNormalizer = damageType;
                a = source.composition().get(damageType) * (1 - screen.get(damageType));
            }
            builder.set(damageType, source.composition().get(damageType) * (1 - screen.get(damageType)));
        }
        final DamageComposition composition = builder.build();
        if (targetNormalizer == null) {
            return new DamagePacket(new BattleDamageSource(source.attacker(), composition, source.equipmentSlot()), 0);
        }
        final double c = amount * a;
        final double d = amount * composition.get(targetNormalizer);
        return new DamagePacket(new BattleDamageSource(source.attacker(), composition, source.equipmentSlot()), amount * (c / d));
    }
}
