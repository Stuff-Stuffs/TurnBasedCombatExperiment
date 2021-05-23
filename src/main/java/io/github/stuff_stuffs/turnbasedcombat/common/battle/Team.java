package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.mojang.serialization.Codec;

public record Team(String teamId) {
    public static final Codec<Team> CODEC = Codec.STRING.xmap(Team::new, Team::teamId);
}
