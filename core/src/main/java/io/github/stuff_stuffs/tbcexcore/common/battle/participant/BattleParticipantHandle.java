package io.github.stuff_stuffs.tbcexcore.common.battle.participant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.tbcexcore.common.battle.BattleHandle;
import io.github.stuff_stuffs.tbcexcore.common.util.CodecUtil;

import java.util.UUID;
import java.util.function.Function;

public record BattleParticipantHandle(BattleHandle battleId, UUID participantId) {
    private static final UUID UNIVERSAL_UUID = new UUID(-1, -1);
    public static final Function<BattleHandle, BattleParticipantHandle> UNIVERSAL = battleId -> new BattleParticipantHandle(battleId, UNIVERSAL_UUID);
    public static final Codec<BattleParticipantHandle> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BattleHandle.CODEC.fieldOf("battleId").forGetter(BattleParticipantHandle::battleId),
                    CodecUtil.UUID_CODEC.fieldOf("participantId").forGetter(BattleParticipantHandle::participantId)
            ).apply(instance, BattleParticipantHandle::new)
    );

    public boolean isUniversal() {
        return participantId.equals(UNIVERSAL_UUID);
    }
}
