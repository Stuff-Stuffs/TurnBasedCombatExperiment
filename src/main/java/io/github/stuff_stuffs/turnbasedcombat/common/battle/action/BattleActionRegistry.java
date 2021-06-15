package io.github.stuff_stuffs.turnbasedcombat.common.battle.action;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.turnbasedcombat.common.TurnBasedCombatExperiment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;

public final class BattleActionRegistry {
    public static final Registry<Type<?>> REGISTRY = FabricRegistryBuilder.createSimple((Class<Type<?>>) (Object) Type.class, TurnBasedCombatExperiment.createId("battle_actions")).buildAndRegister();
    public static final Codec<BattleAction<?>> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<BattleAction<?>, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final Type<?> type = REGISTRY.parse(ops, map.get("type")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            return type.codec.decode(ops, map.get("data")).map(pair -> Pair.of(pair.getFirst(), pair.getSecond()));
        }

        @Override
        public <T> DataResult<T> encode(final BattleAction<?> input, final DynamicOps<T> ops, final T prefix) {
            final Type<?> type = input.getType();
            return ops.mapBuilder().add(
                    "type",
                    REGISTRY.encodeStart(ops, type)
            ).add(
                    "data",
                    ((Codec<BattleAction<?>>) type.codec).encodeStart(ops, input)
            ).build(prefix);
        }
    };
    public static final Type<ParticipantJoinBattleAction> PARTICIPANT_JOIN_BATTLE_ACTION = new Type<>(ParticipantJoinBattleAction.CODEC);
    public static final Type<MoveBattleAction> MOVE_BATTLE_ACTION = new Type<>(MoveBattleAction.CODEC);

    public static final class Type<T extends BattleAction<T>> {
        public final Codec<T> codec;

        public Type(final Codec<T> codec) {
            this.codec = codec;
        }
    }

    public static void init() {
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("join"), PARTICIPANT_JOIN_BATTLE_ACTION);
        Registry.register(REGISTRY, TurnBasedCombatExperiment.createId("move"), MOVE_BATTLE_ACTION);
    }

    private BattleActionRegistry() {
    }
}
