package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;

public interface ParticipantComponent {
    Codec<Pair<ParticipantComponents.Type<?, ?>, ParticipantComponent>> CODEC = CodecUtil.createDependentPairCodec(ParticipantComponents.REGISTRY.getCodec(), type -> type.codec);

    void init(BattleParticipantState state);

    void deinitEvents();

    ParticipantComponents.Type<?, ?> getType();
}
