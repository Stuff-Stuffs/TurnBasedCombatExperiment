package io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.participant.BattleParticipantHandle;

public record BattleParticipantInventoryHandle(BattleParticipantHandle handle, int id) {
    public static final Codec<BattleParticipantInventoryHandle> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BattleParticipantHandle.CODEC.fieldOf("handle").forGetter(BattleParticipantInventoryHandle::handle),
                    Codec.INT.fieldOf("id").forGetter(BattleParticipantInventoryHandle::id)
            ).apply(instance, BattleParticipantInventoryHandle::new));
}
