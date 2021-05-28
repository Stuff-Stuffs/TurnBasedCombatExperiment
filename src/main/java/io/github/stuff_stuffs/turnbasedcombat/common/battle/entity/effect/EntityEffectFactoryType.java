package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;

public final class EntityEffectFactoryType {
    public static final Registry<EntityEffectFactoryType> REGISTRY = FabricRegistryBuilder.createSimple(EntityEffectFactoryType.class, TurnBasedCombatExperiment.createId("effect_factories")).buildAndRegister();
    public static final Codec<EntityEffectFactory> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<EntityEffectFactory, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final EntityEffectFactoryType type = REGISTRY.parse(ops, map.get("type")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            return type.codec.decode(ops, map.get("data"));
        }

        @Override
        public <T> DataResult<T> encode(final EntityEffectFactory input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add(
                    "type",
                    REGISTRY.encodeStart(ops, input.getType())
            ).add(
                    "data",
                    input.getType().codec.encodeStart(ops, input)
            ).build(prefix);
        }
    };

    private final Codec<EntityEffectFactory> codec;

    public EntityEffectFactoryType(final Codec<EntityEffectFactory> codec) {
        this.codec = codec;
    }
}
