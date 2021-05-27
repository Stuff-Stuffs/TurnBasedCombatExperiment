package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class BasicDoubleEntityStatModifier implements EntityStatModifier<Double> {
    public static final Codec<BasicDoubleEntityStatModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("applicationStage").forGetter(modifier -> modifier.applicationStage),
            DoubleOperation.CODEC.fieldOf("operation").forGetter(modifier -> modifier.operation),
            Codec.DOUBLE.fieldOf("x").forGetter(modifier -> modifier.x)
    ).apply(instance, BasicDoubleEntityStatModifier::new));
    public final int applicationStage;
    public final DoubleOperation operation;
    public final double x;

    private BasicDoubleEntityStatModifier(final int applicationStage, final DoubleOperation operation, final double x) {
        this.applicationStage = applicationStage;
        this.operation = operation;
        this.x = x;
    }

    @Override
    public Double modify(final Double input) {
        return operation.apply(x, input);
    }

    @Override
    public int getApplicationStage() {
        return applicationStage;
    }

    @Override
    public EntityStatModifierType getType() {
        return EntityStatModifierType.BASIC_DOUBLE_MODIFIER;
    }
}
