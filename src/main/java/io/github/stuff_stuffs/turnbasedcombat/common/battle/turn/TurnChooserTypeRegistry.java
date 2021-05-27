package io.github.stuff_stuffs.turnbasedcombat.common.battle.turn;

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

public final class TurnChooserTypeRegistry {

    public static final Registry<Type> REGISTRY = FabricRegistryBuilder.createSimple(Type.class, TurnBasedCombatExperiment.createId("turn_chooser_type")).buildAndRegister();
    public static final Codec<TurnChooser> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<TurnChooser, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final Type type = REGISTRY.parse(ops, map.get("type")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            if (type == null) {
                throw new RuntimeException();
            }
            final T data = map.get("data");
            return type.codec.decode(ops, data);
        }

        @Override
        public <T> DataResult<T> encode(final TurnChooser input, final DynamicOps<T> ops, final T prefix) {
            final Map<T, T> map = new Object2ObjectArrayMap<>();
            map.put(ops.createString("id"), REGISTRY.encodeStart(ops, input.getType()).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }));
            map.put(ops.createString("data"), input.getType().codec.encode(input, ops, ops.empty()).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            }));
            return DataResult.success(ops.createMap(map));
        }
    };
    public static final Type SIMPLE_TURN_CHOOSER_TYPE = new Type(SimpleTurnChooser.CODEC);


    public static class Type {
        public final Codec<TurnChooser> codec;

        public Type(final Codec<? extends TurnChooser> codec) {
            this.codec = (Codec<TurnChooser>) codec;
        }
    }

    public static void init() {
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("simple"), SIMPLE_TURN_CHOOSER_TYPE);
    }

    private TurnChooserTypeRegistry() {
    }
}
