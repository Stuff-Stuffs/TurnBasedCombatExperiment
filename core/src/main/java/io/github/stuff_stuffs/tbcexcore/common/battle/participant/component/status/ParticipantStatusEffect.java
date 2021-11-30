package io.github.stuff_stuffs.tbcexcore.common.battle.participant.component.status;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantState;
import io.github.stuff_stuffs.tbcexutil.common.CodecUtil;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.List;

public interface ParticipantStatusEffect {
    Codec<Pair<ParticipantStatusEffects.Type, ParticipantStatusEffect>> CODEC = CodecUtil.createDependentPairCodec(ParticipantStatusEffects.REGISTRY.getCodec(), type -> type.codec);

    Text getName();

    List<TooltipComponent> getDescription();

    void init(BattleParticipantState state);

    void deinit();

    ParticipantStatusEffects.Type getType();
}
