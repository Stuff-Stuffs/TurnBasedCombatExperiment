package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.effect;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

public class EntityEffectRegistry {
    public static final Registry<Type<?>> REGISTRY = FabricRegistryBuilder.createSimple((Class<Type<?>>) (Object) Type.class, TurnBasedCombatExperiment.createId("entity_effect")).buildAndRegister();
    public static final Codec<EntityEffect> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<EntityEffect, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final Identifier id = Identifier.CODEC.decode(ops, map.get("id")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }).getFirst();
            final Type<?> type = REGISTRY.get(id);
            if (type == null) {
                throw new RuntimeException();
            }
            final T data = map.get("data");
            return type.codec.decode(ops, data);
        }

        @Override
        public <T> DataResult<T> encode(final EntityEffect input, final DynamicOps<T> ops, final T prefix) {
            final Map<T, T> map = new Object2ObjectArrayMap<>();
            map.put(ops.createString("id"), Identifier.CODEC.encode(REGISTRY.getId(input.getType()), ops, ops.empty()).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }));
            map.put(ops.createString("data"), input.getType().codec.encodeStart(ops, input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }));
            return DataResult.success(ops.createMap(map));
        }
    };
    public static final Codec<List<EntityEffect>> LIST_CODEC = Codec.list(CODEC);

    public static class Type<T extends EntityEffect> {
        private final Codec<EntityEffect> codec;
        public final BinaryOperator<EntityEffect> combiner;

        public Type(final Codec<T> codec, final BinaryOperator<EntityEffect> combiner) {
            this.codec = (Codec<EntityEffect>) codec;
            this.combiner = combiner;
        }
    }
}
