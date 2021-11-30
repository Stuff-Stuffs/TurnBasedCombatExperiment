package io.github.stuff_stuffs.tbcexcore.common.battle.state.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.github.stuff_stuffs.tbcexcore.common.battle.state.BattleState;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;

public interface BattleComponent {
    Codec<Pair<BattleComponents.Type<?, ?>, BattleComponent>> CODEC = CodecUtil.createDependentPairCodec(BattleComponents.REGISTRY.getCodec(), new CodecUtil.DependentEncoder<>() {
        @Override
        public <T> DataResult<T> encode(final BattleComponents.Type<?, ?> coValue, final BattleComponent value, final DynamicOps<T> ops) {
            return coValue.codec.encodeStart(ops, value);
        }
    }, new CodecUtil.DependentDecoder<>() {
        @Override
        public <T> DataResult<BattleComponent> decode(final BattleComponents.Type<?, ?> coValue, final T value, final DynamicOps<T> ops) {
            return coValue.codec.parse(ops, value);
        }
    });

    void init(BattleState state);

    void deinitEvents();

    BattleComponents.Type<?, ?> getType();
}
