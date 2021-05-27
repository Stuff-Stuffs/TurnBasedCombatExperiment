package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class DoubleEntityStatModifier implements EntityStatModifier<Double> {
    public static final Codec<DoubleEntityStatModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("applicationStage").forGetter(modifier -> modifier.applicationStage),
            Codec.INT.xmap(i -> Operation.values()[i], Enum::ordinal).fieldOf("operation").forGetter(modifier -> modifier.operation),
            Codec.DOUBLE.fieldOf("x").forGetter(modifier -> modifier.x)
    ).apply(instance, DoubleEntityStatModifier::new));
    public final int applicationStage;
    public final Operation operation;
    public final double x;

    private DoubleEntityStatModifier(final int applicationStage, final Operation operation, final double x) {
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
        return null;
    }
}
