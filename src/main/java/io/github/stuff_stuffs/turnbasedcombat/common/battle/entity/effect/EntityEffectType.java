package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public final class EntityEffectType {
    public static final Registry<EntityEffectType> REGISTRY = FabricRegistryBuilder.createSimple(EntityEffectType.class, TurnBasedCombatExperiment.createId("entity_effects")).buildAndRegister();

    public static final Codec<EntityEffect> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<EntityEffect, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final EntityEffectType type = REGISTRY.parse(ops, map.get("type")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            return type.codec.decode(ops, map.get("data"));
        }

        @Override
        public <T> DataResult<T> encode(final EntityEffect input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add(
                    "type",
                    REGISTRY.encodeStart(ops, input.getType())
            ).add(
                    "data",
                    input.getType().codec.encodeStart(ops, input)
            ).build(prefix);
        }
    };

    private final Function<EntityEffect, Text> nameFunction;
    private final Codec<EntityEffect> codec;
    private final BinaryOperator<EntityEffect> combiner;
    private final BiConsumer<EntityEffect, List<Text>> tooltipAppender;

    public EntityEffectType(final Function<EntityEffect, Text> nameFunction, final Codec<EntityEffect> codec, final BinaryOperator<EntityEffect> combiner, final BiConsumer<EntityEffect, List<Text>> tooltipAppender) {
        this.nameFunction = nameFunction;
        this.codec = codec;
        this.combiner = combiner;
        this.tooltipAppender = tooltipAppender;
    }

    public Text getName(final EntityEffect entityEffect) {
        return nameFunction.apply(entityEffect);
    }

    public void appendTooltip(final EntityEffect entityEffect, final List<Text> texts) {
        tooltipAppender.accept(entityEffect, texts);
    }

    public EntityEffect combine(final EntityEffect first, final EntityEffect second) {
        if (first.getType() != this || second.getType() != this) {
            throw new RuntimeException();
        }
        return combiner.apply(first, second);
    }
}
