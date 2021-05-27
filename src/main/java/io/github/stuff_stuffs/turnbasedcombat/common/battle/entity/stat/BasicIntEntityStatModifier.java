package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class BasicIntEntityStatModifier implements EntityStatModifier<Integer> {
    public static final Codec<BasicIntEntityStatModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("applicationStage").forGetter(modifier -> modifier.applicationStage),
            IntOperation.CODEC.fieldOf("operation").forGetter(modifier -> modifier.operation),
            Codec.INT.fieldOf("x").forGetter(modifier -> modifier.x)
    ).apply(instance, BasicIntEntityStatModifier::new));
    public final int applicationStage;
    public final IntOperation operation;
    public final int x;

    private BasicIntEntityStatModifier(final int applicationStage, final IntOperation operation, final int x) {
        this.applicationStage = applicationStage;
        this.operation = operation;
        this.x = x;
    }

    @Override
    public Integer modify(final Integer input) {
        return operation.apply(x, input);
    }

    @Override
    public int getApplicationStage() {
        return applicationStage;
    }

    @Override
    public EntityStatModifierType getType() {
        return EntityStatModifierType.BASIC_INT_MODIFIER;
    }
}
