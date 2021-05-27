package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;

public class EntityStatModifierType {
    public static final Registry<EntityStatModifierType> REGISTRY = FabricRegistryBuilder.createSimple(EntityStatModifierType.class, TurnBasedCombatExperiment.createId("stat_modifier_types")).buildAndRegister();
    public static final Codec<EntityStatModifier<?>> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<EntityStatModifier<?>, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final EntityStatModifierType type = REGISTRY.parse(ops, map.get("type")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            return type.codec.decode(ops, map.get("data"));
        }

        @Override
        public <T> DataResult<T> encode(final EntityStatModifier<?> input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add(
                    "type",
                    REGISTRY.encodeStart(ops, input.getType())
            ).add(
                    "data",
                    input.getType().codec.encodeStart(ops, input)
            ).build(prefix);
        }
    };
    public static final EntityStatModifierType BASIC_DOUBLE_MODIFIER = new EntityStatModifierType(BasicDoubleEntityStatModifier.CODEC.xmap(Function.identity(), modifier -> (BasicDoubleEntityStatModifier) modifier));
    public static final EntityStatModifierType BASIC_RESISTANCES_MODIFIER = new EntityStatModifierType(BasicResistanceEntityStatModifier.CODEC.xmap(Function.identity(), modifier -> (BasicResistanceEntityStatModifier) modifier));
    public static final EntityStatModifierType BASIC_INT_MODIFIER = new EntityStatModifierType(BasicIntEntityStatModifier.CODEC.xmap(Function.identity(), modifier -> (BasicIntEntityStatModifier) modifier));

    private final Codec<EntityStatModifier<?>> codec;

    public EntityStatModifierType(final Codec<EntityStatModifier<?>> codec) {
        this.codec = codec;
    }

    static {
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("basic_double_modifier"), BASIC_DOUBLE_MODIFIER);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("basic_resistances_modifier"), BASIC_RESISTANCES_MODIFIER);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("basic_int_modifier"), BASIC_INT_MODIFIER);
    }
}
