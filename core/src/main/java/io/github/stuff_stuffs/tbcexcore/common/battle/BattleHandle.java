package io.github.stuff_stuffs.tbcexcore.common.battle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

//TODO dimension id?
public record BattleHandle(int id) {
    public static final Codec<BattleHandle> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("id").forGetter(handle -> handle.id)).apply(instance, BattleHandle::new));
}
