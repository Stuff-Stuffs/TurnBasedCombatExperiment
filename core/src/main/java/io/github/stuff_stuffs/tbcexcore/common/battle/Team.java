package io.github.stuff_stuffs.tbcexcore.common.battle;

import com.mojang.serialization.Codec;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import io.github.stuff_stuffs.tbcexutil.common.colour.HsvColour;
import it.unimi.dsi.fastutil.HashCommon;

import java.util.Objects;

public final class Team {
    public static final Codec<Team> CODEC = Codec.STRING.xmap(Team::new, Team::teamId);
    private final String teamId;
    private Colour cache;

    public Team(final String teamId) {
        this.teamId = teamId;
    }

    public Colour getColour() {
        if (cache == null) {
            long sum = 0;
            for (int i = 0; i < teamId.length(); i++) {
                sum = HashCommon.murmurHash3(sum) ^ teamId.charAt(i);
            }
            sum = HashCommon.murmurHash3(sum);
            final float norm = (sum / (1 + (float) Long.MAX_VALUE));
            final float h = norm * 180 + 180;
            cache = new HsvColour(h, 1, 1);
        }
        return cache;
    }

    public String teamId() {
        return teamId;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        final var that = (Team) obj;
        return Objects.equals(teamId, that.teamId);
    }

    @Override
    public int hashCode() {
        return teamId.hashCode();
    }

    @Override
    public String toString() {
        return "Team[" +
                "teamId=" + teamId + ']';
    }

}
