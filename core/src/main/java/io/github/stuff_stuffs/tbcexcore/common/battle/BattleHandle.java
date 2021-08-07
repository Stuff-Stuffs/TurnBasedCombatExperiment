package io.github.stuff_stuffs.tbcexcore.common.battle;

import com.mojang.serialization.Codec;

//TODO dimension id?
public record BattleHandle(int id) {
    public static final Codec<BattleHandle> CODEC = Codec.INT.xmap(BattleHandle::new, BattleHandle::id);
}
