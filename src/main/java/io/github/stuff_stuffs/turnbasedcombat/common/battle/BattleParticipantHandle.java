package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.util.CodecUtil;

import java.util.UUID;
import java.util.function.IntFunction;

public record BattleParticipantHandle(int battleId, UUID participantId) {
    private static final UUID UNIVERSAL_UUID = new UUID(-1, -1);
    public static final IntFunction<BattleParticipantHandle> UNIVERSAL = battleId -> new BattleParticipantHandle(battleId, UNIVERSAL_UUID);
    public static final Codec<BattleParticipantHandle> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("battleId").forGetter(BattleParticipantHandle::getBattleId),
                    CodecUtil.UUID_CODEC.fieldOf("participantId").forGetter(BattleParticipantHandle::getParticipantId)
            ).apply(instance, BattleParticipantHandle::new)
    );

    public int getBattleId() {
        return battleId;
    }

    public UUID getParticipantId() {
        return participantId;
    }

    public boolean isUniversal() {
        return participantId.equals(UNIVERSAL_UUID);
    }
}
