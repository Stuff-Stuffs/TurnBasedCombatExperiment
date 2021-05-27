package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.function.BinaryOperator;

public final class EntityEffectRegistry {
    public static final Registry<Type<?>> REGISTRY = FabricRegistryBuilder.createSimple((Class<Type<?>>) (Object) Type.class, TurnBasedCombatExperiment.createId("entity_effect")).buildAndRegister();
    public static final Codec<EntityEffect> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<EntityEffect, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final Type<?> type = REGISTRY.parse(ops, map.get("type")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            if (type == null) {
                throw new RuntimeException();
            }
            final T data = map.get("data");
            return type.codec.decode(ops, data);
        }

        @Override
        public <T> DataResult<T> encode(final EntityEffect input, final DynamicOps<T> ops, final T prefix) {
            final Map<T, T> map = new Object2ObjectArrayMap<>();
            map.put(ops.createString("type"), REGISTRY.encodeStart(ops, input.getType()).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }));
            map.put(ops.createString("data"), input.getType().codec.encodeStart(ops, input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }));
            return DataResult.success(ops.createMap(map));
        }
    };

    private EntityEffectRegistry() {
    }

    public static class Type<T extends EntityEffect> {
        private final Codec<EntityEffect> codec;
        public final BinaryOperator<EntityEffect> combiner;

        public Type(final Codec<T> codec, final BinaryOperator<EntityEffect> combiner) {
            this.codec = (Codec<EntityEffect>) codec;
            this.combiner = combiner;
        }
    }
}
