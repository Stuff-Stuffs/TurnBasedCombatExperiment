package io.github.stuff_stuffs.turnbasedcombat.common.api;

public final class Team {
    public static final Team DEFAULT_PLAYER_TEAM = new Team(-1);
    private final long id;

    public Team(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Team team = (Team) o;

        return id == team.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
