package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public interface ParticipantStatusEffect {
    Codec<Pair<ParticipantStatusEffects.Type, ParticipantStatusEffect>> CODEC = CodecUtil.createDependentPairCodec(ParticipantStatusEffects.REGISTRY.getCodec(), type -> type.codec);

    Text getName();

    List<OrderedText> getDescription();

    void init(BattleParticipantState state);

    void deinit();

    ParticipantStatusEffects.Type getType();
}
