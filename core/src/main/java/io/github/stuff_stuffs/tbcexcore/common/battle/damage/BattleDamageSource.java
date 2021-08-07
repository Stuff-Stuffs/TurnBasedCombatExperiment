package io.github.stuff_stuffs.tbcexcore.common.battle.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;

import java.util.Optional;

public record BattleDamageSource(Optional<BattleParticipantHandle> attacker) {
    public static final Codec<BattleDamageSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.optionalField("attacker", BattleParticipantHandle.CODEC).forGetter(BattleDamageSource::attacker)
    ).apply(instance, BattleDamageSource::new));
}
