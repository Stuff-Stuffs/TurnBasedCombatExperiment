package io.github.stuff_stuffs.turnbasedcombat.common.battle.entity.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EntityStatModifierHandle(EntityStatType<?> type, int id) {
    public static final Codec<EntityStatModifierHandle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityStatType.REGISTRY.fieldOf("type").forGetter(EntityStatModifierHandle::type),
            Codec.INT.fieldOf("id").forGetter(EntityStatModifierHandle::id)
    ).apply(instance, EntityStatModifierHandle::new));
}
