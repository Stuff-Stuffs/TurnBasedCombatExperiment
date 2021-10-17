package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;

public interface ParticipantComponent {
    Codec<Pair<ParticipantComponents.Type<?, ?>, ParticipantComponent>> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Pair<ParticipantComponents.Type<?, ?>, ParticipantComponent>, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final ParticipantComponents.Type<?, ?> type = ParticipantComponents.REGISTRY.parse(ops, map.get("type")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final ParticipantComponent component = type.codec.parse(ops, map.get("data")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            return DataResult.success(Pair.of(Pair.of(type, component), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final Pair<ParticipantComponents.Type<?, ?>, ParticipantComponent> input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add("type", ParticipantComponents.REGISTRY.encodeStart(ops, input.getFirst())).add("data", input.getFirst().codec.encodeStart(ops, input.getSecond())).build(prefix);
        }
    };

    void init(BattleParticipantState state);

    void deinitEvents();

    ParticipantComponents.Type<?, ?> getType();
}
