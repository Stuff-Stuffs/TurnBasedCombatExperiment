package io.github.stuff_stuffs.turnbasedcombat.common.battle;

import com.mojang.serialization.Codec;

public final class Team {
    public static final Codec<Team> CODEC = Codec.STRING.xmap(Team::new, Team::getTeamId);
    private final String teamId;

    public Team(final String teamId) {
        this.teamId = teamId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Team)) {
            return false;
        }

        final Team team = (Team) o;

        return teamId.equals(team.teamId);
    }

    public String getTeamId() {
        return teamId;
    }

    @Override
    public int hashCode() {
        return teamId.hashCode();
    }
}
