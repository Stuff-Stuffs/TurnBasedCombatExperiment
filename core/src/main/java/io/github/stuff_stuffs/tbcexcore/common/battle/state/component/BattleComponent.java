package io.github.stuff_stuffs.tbcexcore.common.battle.state.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;

public interface BattleComponent {
    Codec<Pair<BattleComponents.Type<?, ?>, BattleComponent>> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Pair<BattleComponents.Type<?, ?>, BattleComponent>, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final BattleComponents.Type<?, ?> type = BattleComponents.REGISTRY.parse(ops, map.get("type")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final BattleComponent component = type.codec.parse(ops, map.get("data")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            return DataResult.success(Pair.of(Pair.of(type, component), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final Pair<BattleComponents.Type<?, ?>, BattleComponent> input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add("type", BattleComponents.REGISTRY.encodeStart(ops, input.getFirst())).add("data", input.getFirst().codec.encodeStart(ops, input.getSecond())).build(prefix);
        }
    };

    void init(BattleState state);

    void deinitEvents();

    BattleComponents.Type<?, ?> getType();
}
