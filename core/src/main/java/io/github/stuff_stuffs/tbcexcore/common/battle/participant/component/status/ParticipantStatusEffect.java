package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.List;

public interface ParticipantStatusEffect {
    Codec<Pair<ParticipantStatusEffects.Type, ParticipantStatusEffect>> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Pair<ParticipantStatusEffects.Type, ParticipantStatusEffect>, T>> decode(final DynamicOps<T> ops, final T input) {
            final MapLike<T> map = ops.getMap(input).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final ParticipantStatusEffects.Type type = ParticipantStatusEffects.REGISTRY.parse(ops, map.get("type")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            final ParticipantStatusEffect component = type.codec.parse(ops, map.get("data")).getOrThrow(false, s -> {
                throw new RuntimeException(s);
            });
            return DataResult.success(Pair.of(Pair.of(type, component), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final Pair<ParticipantStatusEffects.Type, ParticipantStatusEffect> input, final DynamicOps<T> ops, final T prefix) {
            return ops.mapBuilder().add("type", ParticipantStatusEffects.REGISTRY.encodeStart(ops, input.getFirst())).add("data", input.getFirst().codec.encodeStart(ops, input.getSecond())).build(prefix);
        }
    };

    Text getName();

    List<TooltipComponent> getDescription();

    void init(BattleParticipantState state);

    void deinit();

    ParticipantStatusEffects.Type getType();
}
