package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.IntFunction;

public final class BattleParticipantHandle {
    public static final IntFunction<BattleParticipantHandle> UNIVERSAL = battleId -> new BattleParticipantHandle(battleId, -1);
    public static final Codec<BattleParticipantHandle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("battleId").forGetter(BattleParticipantHandle::getBattleId),
            Codec.INT.fieldOf("participantId").forGetter(BattleParticipantHandle::getParticipantId)).
            apply(instance, BattleParticipantHandle::new)
    );
    private final int battleId;
    private final int participantId;

    public BattleParticipantHandle(final int battleId, final int participantId) {
        this.battleId = battleId;
        this.participantId = participantId;
    }

    public int getBattleId() {
        return battleId;
    }

    public int getParticipantId() {
        return participantId;
    }

    public boolean isUniversal() {
        return participantId==-1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BattleParticipantHandle)) {
            return false;
        }

        final BattleParticipantHandle that = (BattleParticipantHandle) o;

        if (battleId != that.battleId) {
            return false;
        }
        return participantId == that.participantId;
    }

    @Override
    public int hashCode() {
        int result = battleId;
        result = 31 * result + participantId;
        return result;
    }
}
