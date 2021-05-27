package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat;

import com.mojang.serialization.Codec;

public interface EntityStatModifier<T> {
    int MULTIPLY_BASE = 0;
    int ADD_BASE = 1;
    int MULTIPLY = 2;
    int ADD_TOTAL = 3;

    T modify(T input);

    int getApplicationStage();

    EntityStatModifierType getType();

    enum Operation {
        ADD {
            @Override
            public double apply(final double x, final double y) {
                return x + y;
            }
        },
        MULTIPLY {
            @Override
            public double apply(final double x, final double y) {
                return x * y;
            }
        };

        public abstract double apply(double x, double y);

        public static final Codec<Operation> CODEC = Codec.STRING.xmap(Operation::valueOf, Enum::name);
    }
}
