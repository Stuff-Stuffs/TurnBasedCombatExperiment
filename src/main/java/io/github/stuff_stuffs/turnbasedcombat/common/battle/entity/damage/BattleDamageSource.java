package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.BattleParticipantHandle;
import io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.equipment.BattleEquipmentType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record BattleDamageSource(
        @Nullable BattleParticipantHandle attacker,
        DamageComposition composition,
        @Nullable BattleEquipmentType equipmentSlot) {
    public static final Codec<BattleDamageSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.optionalField("attacker", BattleParticipantHandle.CODEC).forGetter(source -> Optional.ofNullable(source.attacker)),
            DamageComposition.CODEC.fieldOf("composition").forGetter(BattleDamageSource::composition),
            Codec.optionalField("slot", BattleEquipmentType.REGISTRY).forGetter(source -> Optional.ofNullable(source.equipmentSlot()))
    ).apply(instance, BattleDamageSource::new));

    private BattleDamageSource(final Optional<BattleParticipantHandle> attacker, final DamageComposition composition, final Optional<BattleEquipmentType> equipmentSlot) {
        this(attacker.orElse(null), composition, equipmentSlot.orElse(null));
    }
}
