package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage.DamageResistances;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage.DamageType;

public class BasicResistanceEntityStatModifier implements EntityStatModifier<DamageResistances> {
    public static final Codec<BasicResistanceEntityStatModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("applicationStage").forGetter(modifier -> modifier.applicationStage),
            Operation.CODEC.fieldOf("operation").forGetter(modifier -> modifier.operation),
            DamageResistances.CODEC.fieldOf("resistances").forGetter(modifier -> modifier.resistances)
    ).apply(instance, BasicResistanceEntityStatModifier::new));
    public final int applicationStage;
    public final EntityStatModifier.Operation operation;
    public final DamageResistances resistances;

    public BasicResistanceEntityStatModifier(final int applicationStage, final EntityStatModifier.Operation operation, final DamageResistances resistances) {
        this.applicationStage = applicationStage;
        this.operation = operation;
        this.resistances = resistances;
    }

    @Override
    public DamageResistances modify(final DamageResistances input) {
        final DamageResistances.Builder builder = DamageResistances.builder();
        for (final DamageType damageType : DamageType.REGISTRY) {
            builder.set(damageType, Math.min(operation.apply(input.get(damageType), resistances.get(damageType)), 1));
        }
        return builder.build();
    }

    @Override
    public int getApplicationStage() {
        return applicationStage;
    }

    @Override
    public EntityStatModifierType getType() {
        return null;
    }
}
